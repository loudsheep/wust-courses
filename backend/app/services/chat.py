import json
from app.services.provider_resolver import resolve_runtime_provider
from app.services.agent import run_agent
from app.models.chat import Conversation, Message


def build_retrieval_components(chunks: list[dict]):
    if not chunks:
        return []

    return [
        {
            "type": "retrieval_panel",
            "chunks": [
                {
                    "document_name": c["document_name"],
                    "excerpt": c["text"][:200],
                    "score": c["score"],
                    "chunk_index": c["chunk_index"],
                }
                for c in chunks
            ],
        },
    ]


async def stream_chat(req, db, cfg):
    conv_id = req.conversation_id
    if not conv_id:
        conv = Conversation(title=req.message[:50])
        db.add(conv)
        db.commit()
        db.refresh(conv)
        conv_id = conv.id

    # Save user message
    user_msg = Message(conversation_id=conv_id, role="user", content=req.message)
    db.add(user_msg)
    db.commit()

    # Fetch history
    history = (
        db.query(Message)
        .filter(Message.conversation_id == conv_id)
        .order_by(Message.created_at.asc())
        .all()
    )
    # Remove the last message from history (current user message) to avoid duplication in the agent
    history = history[:-1]

    runtime = resolve_runtime_provider(cfg)

    yield f"data: {json.dumps({'conversation_id': conv_id})}\n\n"

    full_content = ""
    persisted_tool_calls = []
    retrieved_chunks = []
    suggestion_chips = []

    async for event in run_agent(runtime, req.message, db, history=history):
        if event["type"] == "content":
            full_content += event["text"]
            yield f"data: {json.dumps({'content': event['text']})}\n\n"
        elif event["type"] == "tool_call":
            if event["tool"] == "suggest_followups":
                continue
            payload = {k: v for k, v in event.items() if k != "type"}
            yield f"data: {json.dumps({'tool_call': payload})}\n\n"
            if event["status"] in ("done", "error"):
                persisted_tool_calls.append({"type": "tool_call", **payload})
        elif event["type"] == "retrieved_chunks":
            retrieved_chunks = event["chunks"]
        elif event["type"] == "suggestion_chips":
            suggestion_chips = event["chips"]

    components = build_retrieval_components(retrieved_chunks)
    if suggestion_chips:
        components.append({"type": "suggestion_chips", "chips": suggestion_chips})
    components.extend(persisted_tool_calls)

    # Save assistant message
    assistant_msg = Message(
        conversation_id=conv_id,
        role="assistant",
        content=full_content,
        components=components
    )
    db.add(assistant_msg)
    db.commit()

    yield f"data: {json.dumps({'components': components})}\n\n"
    yield "data: [DONE]\n\n"
