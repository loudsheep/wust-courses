import json
from app.services.provider import get_provider
from app.services.provider_resolver import resolve_runtime_provider
from app.services.llm import run_llm, stream_llm
from app.services.rag import run_rag
from app.models.chat import Conversation, Message


def build_components(message: str, content: str):
    text = message.lower()
    components = []

    if "code" in text:
        components.append(
            {
                "type": "code_block",
                "language": "python",
                "code": content,
            }
        )

    elif "action" in text:
        components.append(
            {
                "type": "action_buttons",
                "buttons": [
                    {"label": "Run analysis", "primary": True},
                    {"label": "Export"},
                    {"label": "Save"},
                ],
            }
        )

    else:
        components.append(
            {
                "type": "suggestion_chips",
                "chips": [
                    "Explain more",
                    "Give example",
                    "Show docs",
                ],
            }
        )

    return components


def build_retrieval_components(chunks: list[dict]):
    if not chunks:
        return []

    return [
        {
            "type": "citation_group",
            "citations": [
                {
                    "document_id": c["document_id"],
                    "document_name": c["document_name"],
                    "chunk_index": c["chunk_index"],
                    "page_number": None,
                    "excerpt": c["text"][:300],
                    "score": c["score"],
                }
                for c in chunks
            ],
        },
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


def handle_chat(req, db):
    cfg = get_provider(db, req.provider_id)

    runtime = resolve_runtime_provider(cfg)

    rag_result = run_rag(req.message, db)
    rag_context = rag_result[0] if rag_result else None
    retrieved_chunks = rag_result[1] if rag_result else []

    content = run_llm(runtime, req.message, rag_context)

    components = build_components(req.message, content)
    components.extend(build_retrieval_components(retrieved_chunks))

    return {
        "content": content,
        "components": components,
    }


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
    # Remove the last message from history (current user message) to avoid duplication in run_llm
    history = history[:-1]

    runtime = resolve_runtime_provider(cfg)
    rag_result = run_rag(req.message, db)
    rag_context = rag_result[0] if rag_result else None
    retrieved_chunks = rag_result[1] if rag_result else []

    yield f"data: {json.dumps({'conversation_id': conv_id})}\n\n"

    full_content = ""
    async for chunk in stream_llm(runtime, req.message, rag_context, history=history):
        full_content += chunk
        yield f"data: {json.dumps({'content': chunk})}\n\n"

    components = build_components(req.message, full_content)
    components.extend(build_retrieval_components(retrieved_chunks))

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
