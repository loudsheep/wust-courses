import os
from langchain_core.messages import AIMessage, AIMessageChunk, HumanMessage, SystemMessage, ToolMessage

from app.services.llm import extract_text_content
from app.services.provider_resolver import build_llm
from app.services.tools import build_tools

MAX_ITERATIONS = 5
CHAT_HISTORY_LIMIT = int(os.environ.get("CHAT_HISTORY_LIMIT", 20))

AGENT_SYSTEM_PROMPT = (
    "You are a helpful assistant for the user's personal document knowledge base. "
    "You have tools to search the user's uploaded documents, list what documents are "
    "available, and fetch additional context or metadata. "
    "Use search_documents when the user's question might be answered by their documents. "
    "Use keyword_search for specific names, numbers, or dates that semantic search might miss. "
    "Use search_documents_filtered if you need to search within a specific document or file type. "
    "Use list_documents if you need to know what is available. "
    "Use get_document_chunk if a search excerpt is truncated and you need more context. "
    "Use get_document_metadata to find out properties of a document like its size, type, or upload date. "
    "If documents don't contain relevant information, say so and answer from your own "
    "knowledge if possible. Do not call tools when they are clearly unnecessary. "
    "After answering a question that involved searching documents, you should generally call "
    "suggest_followups with 2-3 short follow-up questions the user might want to ask next. "
    "Skip it only when the answer is a dead end or the question was trivial."
)


def _build_messages(message: str, history=None):
    messages = [SystemMessage(content=AGENT_SYSTEM_PROMPT)]
    if history:
        limited_history = history[-CHAT_HISTORY_LIMIT:]
        for m in limited_history:
            if m.role == "user":
                messages.append(HumanMessage(content=m.content))
            else:
                messages.append(AIMessage(content=m.content))
    messages.append(HumanMessage(content=message))
    return messages


def _summarize_result(tool_name: str, result: str, chunks_added: int = 0) -> str:
    if tool_name in ("search_documents", "keyword_search", "search_documents_filtered"):
        return f"Found {chunks_added} chunk(s)" if chunks_added else "No results found"
    if tool_name == "list_documents":
        if result.startswith("No documents"):
            return "No documents indexed"
        return f"{result.count(chr(10)) + 1} document(s) listed"
    if tool_name == "get_document_chunk":
        if "not found" in result:
            return "Chunk not found"
        return "Retrieved chunk with context"
    if tool_name == "get_document_metadata":
        if "not found" in result:
            return "Document not found"
        return "Retrieved metadata"
    return "Done"


async def run_agent(runtime, message: str, db, history=None):
    """
    Async generator yielding dicts:
      {"type": "content", "text": "..."}
      {"type": "tool_call", "id": ..., "tool": ..., "status": "running"|"done"|"error",
       "args": {...}, "result_summary": "..."}   (result_summary only on done/error)
      {"type": "retrieved_chunks", "chunks": [...]}   -- always the last event
    """
    tools, retrieved_chunks, suggestion_chips = build_tools(db)
    llm = build_llm(runtime).bind_tools(tools)
    tools_by_name = {t.name: t for t in tools}

    messages = _build_messages(message, history)

    for _ in range(MAX_ITERATIONS):
        full_chunk: AIMessageChunk | None = None
        async for chunk in llm.astream(messages):
            full_chunk = chunk if full_chunk is None else full_chunk + chunk
            text = extract_text_content(chunk.content)
            if text:
                yield {"type": "content", "text": text}

        if full_chunk is None:
            break

        ai_message = AIMessage(content=full_chunk.content, tool_calls=full_chunk.tool_calls)
        messages.append(ai_message)

        if not ai_message.tool_calls:
            break

        for call in ai_message.tool_calls:
            yield {
                "type": "tool_call",
                "id": call["id"],
                "tool": call["name"],
                "status": "running",
                "args": call["args"],
            }
            tool_fn = tools_by_name.get(call["name"])
            try:
                if tool_fn is None:
                    raise ValueError(f"Unknown tool: {call['name']}")
                chunks_before = len(retrieved_chunks)
                result = tool_fn.invoke(call["args"])
                chunks_added = len(retrieved_chunks) - chunks_before
                summary = _summarize_result(call["name"], result, chunks_added=chunks_added)
                yield {
                    "type": "tool_call",
                    "id": call["id"],
                    "tool": call["name"],
                    "status": "done",
                    "args": call["args"],
                    "result_summary": summary,
                }
                messages.append(ToolMessage(content=str(result), tool_call_id=call["id"]))
            except Exception as e:
                yield {
                    "type": "tool_call",
                    "id": call["id"],
                    "tool": call["name"],
                    "status": "error",
                    "args": call["args"],
                    "result_summary": str(e),
                }
                messages.append(ToolMessage(content=f"Error: {e}", tool_call_id=call["id"]))

    yield {"type": "retrieved_chunks", "chunks": retrieved_chunks}
    if suggestion_chips:
        yield {"type": "suggestion_chips", "chips": suggestion_chips}
