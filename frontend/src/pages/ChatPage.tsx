import { useState, useRef, useEffect, useCallback } from 'react';
import { Send, Shuffle, FileText, Copy, Check, Bot, ChevronDown } from 'lucide-react';
import { cn } from '@/lib/utils';
import { api, type LLMProviderConfig } from '@/lib/api';

// ── Types ──────────────────────────────────────────────────────────────────

type Role = 'user' | 'assistant';

interface SuggestionChips {
  type: 'suggestion_chips';
  chips: string[];
}
interface CitationGroup {
  type: 'citation_group';
  citations: { title: string; excerpt: string }[];
}
interface ActionButtons {
  type: 'action_buttons';
  buttons: { label: string; primary?: boolean }[];
}
interface CodeBlock {
  type: 'code_block';
  language: string;
  code: string;
}

type UIComponent = SuggestionChips | CitationGroup | ActionButtons | CodeBlock;

interface Message {
  id: string;
  role: Role;
  content: string;
  components?: UIComponent[];
  streaming?: boolean;
}

// ── Mock data ──────────────────────────────────────────────────────────────

const MOCK_RESPONSES: Array<{ content: string; components: UIComponent[] }> = [
  {
    content:
      'I found relevant information across 3 documents in your knowledge base. Here are the most relevant passages:',
    components: [
      {
        type: 'citation_group',
        citations: [
          {
            title: 'Technical Overview.pdf',
            excerpt:
              'RAG systems combine information retrieval with language generation. Documents are split into chunks, embedded as vectors, and retrieved by semantic similarity.',
          },
          {
            title: 'Architecture Guide.md',
            excerpt:
              'The embedding model converts text into dense vector representations stored in ChromaDB for efficient nearest-neighbour search.',
          },
        ],
      },
      {
        type: 'suggestion_chips',
        chips: ['Explain chunking', 'How does embedding work?', 'Show full architecture'],
      },
    ],
  },
  {
    content: "Here's a code example from your uploaded documentation:",
    components: [
      {
        type: 'code_block',
        language: 'python',
        code: `from sentence_transformers import SentenceTransformer
import chromadb

model = SentenceTransformer("all-mpnet-base-v2")
client = chromadb.Client()
collection = client.create_collection("documents")

def index_chunk(chunk_id: str, text: str) -> None:
    embedding = model.encode(text).tolist()
    collection.add(
        ids=[chunk_id],
        embeddings=[embedding],
        documents=[text],
    )`,
      },
      {
        type: 'suggestion_chips',
        chips: ['Explain this', 'Async version?', 'Add error handling'],
      },
    ],
  },
  {
    content: 'Based on your documents, I identified three action items worth reviewing:',
    components: [
      {
        type: 'action_buttons',
        buttons: [
          { label: 'Export as PDF', primary: true },
          { label: 'Copy to clipboard' },
          { label: 'Share link' },
        ],
      },
      {
        type: 'suggestion_chips',
        chips: ['Tell me more', 'Prioritise these', 'Start over'],
      },
    ],
  },
  {
    content:
      "I couldn't find specific information about that in your current document set. Try uploading more relevant files or rephrasing your query.",
    components: [
      {
        type: 'action_buttons',
        buttons: [{ label: 'Upload documents', primary: true }, { label: 'Try different query' }],
      },
    ],
  },
  {
    content:
      'The key concept here is **semantic search** — instead of matching exact keywords, the system finds chunks that are semantically similar to your query using cosine similarity between embedding vectors.\n\nThis means you can ask in natural language and still get precise, contextually relevant results.',
    components: [
      {
        type: 'suggestion_chips',
        chips: ["What's cosine similarity?", 'How are chunks sized?', 'Show me an example'],
      },
    ],
  },
];

const INITIAL_MESSAGES: Message[] = [
  {
    id: 'welcome',
    role: 'assistant',
    content:
      "Hello! I'm your RAG assistant. Upload some documents and I'll help you explore their contents. You can ask anything in natural language.",
    components: [
      {
        type: 'suggestion_chips',
        chips: ['How does this work?', 'What can you do?', 'Show me a mock response'],
      },
    ],
  },
];

// ── Helpers ────────────────────────────────────────────────────────────────

function uid() {
  return Math.random().toString(36).slice(2);
}

function randomMock() {
  return MOCK_RESPONSES[Math.floor(Math.random() * MOCK_RESPONSES.length)];
}

function renderContent(text: string) {
  return text.split('\n\n').map((para, i) => {
    const parts = para.split(/(\*\*.*?\*\*)/);
    return (
      <p key={i} className={i > 0 ? 'mt-3' : ''}>
        {parts.map((part, j) =>
          part.startsWith('**') && part.endsWith('**') ? (
            <strong key={j} className="font-semibold text-zinc-100">
              {part.slice(2, -2)}
            </strong>
          ) : (
            part
          ),
        )}
      </p>
    );
  });
}

// ── Sub-components ─────────────────────────────────────────────────────────

function SuggestionChipsBlock({
  chips,
  onSelect,
}: {
  chips: string[];
  onSelect: (chip: string) => void;
}) {
  return (
    <div className="flex flex-wrap gap-2 mt-3">
      {chips.map((chip, i) => (
        <button
          key={i}
          onClick={() => onSelect(chip)}
          className="px-3 py-1.5 text-xs rounded-full border border-zinc-700 text-zinc-400 hover:border-zinc-500 hover:text-zinc-200 hover:bg-zinc-800/60 transition-all"
        >
          {chip}
        </button>
      ))}
    </div>
  );
}

function CitationGroupBlock({
  citations,
}: {
  citations: Array<{ title: string; excerpt: string }>;
}) {
  return (
    <div className="mt-3 space-y-2">
      {citations.map((c, i) => (
        <div
          key={i}
          className="rounded-lg border border-zinc-800 bg-zinc-900/60 p-3 hover:border-zinc-700 transition-colors cursor-pointer"
        >
          <div className="flex items-center gap-2 mb-1.5">
            <FileText className="h-3.5 w-3.5 text-zinc-500 shrink-0" />
            <span className="text-xs font-medium text-zinc-400">{c.title}</span>
          </div>
          <p className="text-xs text-zinc-500 leading-relaxed line-clamp-2">{c.excerpt}</p>
        </div>
      ))}
    </div>
  );
}

function ActionButtonsBlock({ buttons }: { buttons: Array<{ label: string; primary?: boolean }> }) {
  return (
    <div className="flex flex-wrap gap-2 mt-3">
      {buttons.map((btn, i) => (
        <button
          key={i}
          className={cn(
            'px-3.5 py-1.5 text-xs rounded-md font-medium transition-colors',
            btn.primary
              ? 'bg-zinc-100 text-zinc-900 hover:bg-zinc-200'
              : 'border border-zinc-700 text-zinc-400 hover:bg-zinc-800 hover:text-zinc-200',
          )}
        >
          {btn.label}
        </button>
      ))}
    </div>
  );
}

function CodeBlockWidget({ language, code }: { language: string; code: string }) {
  const [copied, setCopied] = useState(false);

  function copy() {
    void navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  }

  return (
    <div className="mt-3 rounded-lg border border-zinc-800 overflow-hidden text-left">
      <div className="flex items-center justify-between px-4 py-2 bg-zinc-900 border-b border-zinc-800">
        <span className="text-xs text-zinc-500 font-mono">{language}</span>
        <button
          onClick={copy}
          className="flex items-center gap-1.5 text-xs text-zinc-500 hover:text-zinc-300 transition-colors"
        >
          {copied ? <Check className="h-3.5 w-3.5" /> : <Copy className="h-3.5 w-3.5" />}
          {copied ? 'Copied' : 'Copy'}
        </button>
      </div>
      <pre className="p-4 text-xs text-zinc-300 font-mono overflow-x-auto bg-zinc-950 leading-relaxed">
        <code>{code}</code>
      </pre>
    </div>
  );
}

function MessageBubble({
  message,
  onChipClick,
}: {
  message: Message;
  onChipClick: (text: string) => void;
}) {
  const isUser = message.role === 'user';

  return (
    <div className={cn('flex gap-3 group', isUser && 'flex-row-reverse')}>
      {/* Avatar */}
      <div
        className={cn(
          'h-7 w-7 shrink-0 rounded-full flex items-center justify-center mt-0.5',
          isUser ? 'bg-zinc-700 text-zinc-300 text-xs font-semibold' : 'bg-zinc-800 text-zinc-400',
        )}
      >
        {isUser ? 'U' : <Bot className="h-3.5 w-3.5" />}
      </div>

      <div className={cn('min-w-0 flex-1', isUser && 'flex flex-col items-end')}>
        {/* Message text */}
        <div
          className={cn(
            'rounded-2xl px-4 py-3 text-sm leading-relaxed max-w-[85%]',
            isUser ? 'bg-zinc-800 text-zinc-100 rounded-tr-sm' : 'text-zinc-300 rounded-tl-sm',
          )}
        >
          {renderContent(message.content)}
          {message.streaming && (
            <span className="inline-block w-0.5 h-4 bg-zinc-400 ml-0.5 animate-pulse align-text-bottom" />
          )}
        </div>

        {/* UI components */}
        {!isUser && message.components && (
          <div className="max-w-[85%] w-full">
            {message.components.map((c, i) => {
              switch (c.type) {
                case 'suggestion_chips':
                  return <SuggestionChipsBlock key={i} chips={c.chips} onSelect={onChipClick} />;
                case 'citation_group':
                  return <CitationGroupBlock key={i} citations={c.citations} />;
                case 'action_buttons':
                  return <ActionButtonsBlock key={i} buttons={c.buttons} />;
                case 'code_block':
                  return <CodeBlockWidget key={i} language={c.language} code={c.code} />;
              }
            })}
          </div>
        )}
      </div>
    </div>
  );
}

function TypingIndicator() {
  return (
    <div className="flex gap-3">
      <div className="h-7 w-7 shrink-0 rounded-full bg-zinc-800 flex items-center justify-center text-zinc-400">
        <Bot className="h-3.5 w-3.5" />
      </div>
      <div className="rounded-2xl rounded-tl-sm px-4 py-3 bg-zinc-900/60 flex items-center gap-1">
        <span className="h-1.5 w-1.5 rounded-full bg-zinc-500 animate-bounce [animation-delay:0ms]" />
        <span className="h-1.5 w-1.5 rounded-full bg-zinc-500 animate-bounce [animation-delay:150ms]" />
        <span className="h-1.5 w-1.5 rounded-full bg-zinc-500 animate-bounce [animation-delay:300ms]" />
      </div>
    </div>
  );
}

// ── Main component ─────────────────────────────────────────────────────────

export function ChatPage() {
  const [messages, setMessages] = useState<Message[]>(INITIAL_MESSAGES);
  const [input, setInput] = useState('');
  const [typing, setTyping] = useState(false);
  const [providers, setProviders] = useState<LLMProviderConfig[]>([]);
  const [selectedProviderId, setSelectedProviderId] = useState<string>('');
  const bottomRef = useRef<HTMLDivElement>(null);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    api.providers
      .list()
      .then((list) => {
        setProviders(list);
        const active = list.find((p) => p.is_active) ?? list[0];
        if (active) setSelectedProviderId(active.id);
      })
      .catch(() => {
        /* backend not running, silently skip */
      });
  }, []);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, typing]);

  // auto-resize textarea
  useEffect(() => {
    const el = textareaRef.current;
    if (!el) return;
    el.style.height = 'auto';
    el.style.height = Math.min(el.scrollHeight, 160) + 'px';
  }, [input]);

  const sendMessage = useCallback(
    async (text: string) => {
      const trimmed = text.trim();
      if (!trimmed || typing) return;

      setInput('');

      setMessages((prev) => [...prev, { id: uid(), role: 'user', content: trimmed }]);

      setTyping(true);

      await new Promise((r) => setTimeout(r, 1000 + Math.random() * 800));

      setTyping(false);

      const mock = randomMock();
      setMessages((prev) => [
        ...prev,
        {
          id: uid(),
          role: 'assistant',
          content: mock.content,
          components: mock.components,
        },
      ]);
    },
    [typing],
  );

  function handleKeyDown(e: React.KeyboardEvent<HTMLTextAreaElement>) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      void sendMessage(input);
    }
  }

  return (
    <div className="flex flex-col h-full">
      {/* Header */}
      <div className="shrink-0 h-14 border-b border-zinc-800 flex items-center px-6 gap-3">
        <Bot className="h-4 w-4 text-zinc-500" />
        <span className="text-sm font-medium text-zinc-300">Chat</span>

        <div className="ml-auto flex items-center gap-3">
          {/* Model selector */}
          <div className="relative flex items-center">
            <select
              value={selectedProviderId}
              onChange={(e) => setSelectedProviderId(e.target.value)}
              disabled={providers.length === 0}
              className={cn(
                'appearance-none text-xs rounded-lg border px-3 pr-7 py-1.5 focus:outline-none focus:ring-1 focus:ring-zinc-600 transition-colors',
                providers.length === 0
                  ? 'border-zinc-800 bg-transparent text-zinc-600 cursor-default'
                  : 'border-zinc-700 bg-zinc-900 text-zinc-300 hover:border-zinc-600 cursor-pointer',
              )}
            >
              {providers.length === 0 ? (
                <option value="">No providers configured</option>
              ) : (
                providers.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name} — {p.model}
                  </option>
                ))
              )}
            </select>
            <ChevronDown className="absolute right-2 h-3 w-3 text-zinc-500 pointer-events-none" />
          </div>

          <span className="text-xs text-zinc-700">mock mode</span>
        </div>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto py-6">
        <div className="max-w-3xl mx-auto px-6 space-y-6">
          {messages.map((m) => (
            <MessageBubble key={m.id} message={m} onChipClick={(t) => void sendMessage(t)} />
          ))}
          {typing && <TypingIndicator />}
          <div ref={bottomRef} />
        </div>
      </div>

      {/* Input */}
      <div className="shrink-0 border-t border-zinc-800 p-4">
        <div className="max-w-3xl mx-auto">
          <div className="flex items-center gap-2 rounded-xl border border-zinc-700 bg-zinc-900 px-4 py-3 focus-within:border-zinc-600 transition-colors">
            <textarea
              ref={textareaRef}
              rows={1}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Ask a question…"
              disabled={typing}
              className="flex-1 bg-transparent text-sm text-zinc-200 placeholder:text-zinc-500 resize-none focus:outline-none min-h-[20px] max-h-40 leading-5 disabled:opacity-50 self-center"
            />
            <div className="flex items-center gap-1.5 shrink-0">
              <button
                onClick={() => void sendMessage('Generate a random mock response')}
                disabled={typing}
                title="Generate mock response"
                className="h-8 w-8 flex items-center justify-center rounded-lg text-zinc-500 hover:text-zinc-300 hover:bg-zinc-800 transition-colors disabled:opacity-30"
              >
                <Shuffle className="h-4 w-4" />
              </button>
              <button
                onClick={() => void sendMessage(input)}
                disabled={!input.trim() || typing}
                className="h-8 w-8 flex items-center justify-center rounded-lg bg-zinc-100 text-zinc-900 hover:bg-zinc-200 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
              >
                <Send className="h-3.5 w-3.5" />
              </button>
            </div>
          </div>
          <p className="text-center text-xs text-zinc-700 mt-2">
            Responses are mocked — wire up the RAG backend to get real answers
          </p>
        </div>
      </div>
    </div>
  );
}
