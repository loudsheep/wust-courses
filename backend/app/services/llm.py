from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from app.services.provider_resolver import (
    resolve_runtime_provider,
    validate_provider,
    build_llm,
)


def extract_text_content(content):
    if isinstance(content, str):
        return content
    if isinstance(content, list):
        text = ""
        for block in content:
            if isinstance(block, dict) and block.get("type") == "text":
                text += block.get("text", "")
            elif isinstance(block, str):
                text += block
        return text
    return str(content)


def build_prompt(message, rag_context, history=None):
    prompt_items = []
    
    if rag_context:
        prompt_items.append(("system", f"Use the following context to answer:\n{rag_context}"))
    else:
        prompt_items.append(("system", "You are a helpful assistant."))

    if history:
        prompt_items.append(MessagesPlaceholder(variable_name="chat_history"))

    prompt_items.append(("human", "{input}"))

    prompt = ChatPromptTemplate.from_messages(prompt_items)
    
    formatted_history = []
    if history:
        for m in history:
            if m.role == "user":
                formatted_history.append(HumanMessage(content=m.content))
            else:
                formatted_history.append(AIMessage(content=m.content))
                
    return prompt.format_messages(input=message, chat_history=formatted_history)


def run_llm(runtime, message, rag_context, history=None):
    llm = build_llm(runtime)
    messages = build_prompt(message, rag_context, history)
    response = llm.invoke(messages)
    return extract_text_content(response.content)


async def stream_llm(runtime, message, rag_context, history=None):
    llm = build_llm(runtime)
    messages = build_prompt(message, rag_context, history)

    # Note: Use astream for async streaming if supported, or stream for sync.
    # Most LangChain ChatModels support astream.
    async for chunk in llm.astream(messages):
        text = extract_text_content(chunk.content)
        if text:
            yield text
