import { useState, useRef, useEffect, useCallback } from 'react';
import { Send, Bot, Loader2, AlertCircle, CheckCircle2, Plus, MessageSquare, Trash2, Menu, X } from 'lucide-react';
import { cn } from '@/lib/utils';
import { api, type LLMProviderConfig, type ToolCall, type ConversationSummary, type Message } from '@/lib/api';

function uid() {
  return Math.random().toString(36).slice(2);
}

function renderContent(text: string) {
  if (!text) return null;
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
          className="px-3 py-1.5 text-xs rounded-full border border-zinc-700 text-zinc-400 hover:border-zinc-500 hover:text-zinc-200 hover:bg-zinc-800/60"
        >
          {chip}
        </button>
      ))}
    </div>
  );
}

function RetrievalPanelBlock({
  chunks,
}: {
  chunks: Array<{ document_name: string; excerpt: string; score: number; chunk_index: number }>;
}) {
  const [open, setOpen] = useState(false);

  if (chunks.length === 0) return null;

  return (
    <div className="mt-3">
      <button
        onClick={() => setOpen((o) => !o)}
        className="text-xs text-zinc-500 hover:text-zinc-300 underline-offset-2 hover:underline"
      >
        {open ? 'Hide sources' : `Show sources (${chunks.length})`}
      </button>

      {open && (
        <div className="mt-2 space-y-2">
          {chunks.map((c, i) => (
            <div key={i} className="rounded-lg border border-zinc-800 bg-zinc-900/40 p-3">
              <div className="flex items-center justify-between gap-2 mb-1">
                <span className="text-xs text-zinc-400 truncate">{c.document_name}</span>
                <span className="text-xs text-zinc-600 shrink-0">
                  chunk {c.chunk_index} · {Math.round(c.score * 100)}%
                </span>
              </div>
              <p className="text-xs text-zinc-500 line-clamp-3">{c.excerpt}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

const TOOL_LABELS: Record<string, string> = {
  search_documents: 'Searching documents',
  list_documents: 'Listing documents',
  get_document_chunk: 'Fetching chunk context',
};

function ToolCallBlock({ toolCall }: { toolCall: ToolCall }) {
  const label = TOOL_LABELS[toolCall.tool] ?? toolCall.tool;
  const isRunning = toolCall.status === 'running';
  const isError = toolCall.status === 'error';

  return (
    <div className="mt-2 flex items-center gap-2 rounded-lg border border-zinc-800 bg-zinc-900/60 px-3 py-2 text-xs">
      {isRunning ? (
        <Loader2 className="h-3.5 w-3.5 text-zinc-500 animate-spin shrink-0" />
      ) : isError ? (
        <AlertCircle className="h-3.5 w-3.5 text-red-500 shrink-0" />
      ) : (
        <CheckCircle2 className="h-3.5 w-3.5 text-zinc-500 shrink-0" />
      )}
      <span className="text-zinc-400">{label}</span>
      {toolCall.result_summary && (
        <span className="text-zinc-600">- {toolCall.result_summary}</span>
      )}
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
            'px-3.5 py-1.5 text-xs rounded-md font-medium',
            btn.primary ? 'bg-zinc-100 text-zinc-900' : 'border border-zinc-700 text-zinc-400',
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
    navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 1500);
  }

  return (
    <div className="mt-3 border border-zinc-800 rounded-lg overflow-hidden">
      <div className="flex justify-between px-4 py-2 bg-zinc-900">
        <span className="text-xs text-zinc-500">{language}</span>
        <button onClick={copy} className="text-xs text-zinc-500 hover:text-zinc-300">
          {copied ? 'Copied' : 'Copy'}
        </button>
      </div>
      <pre className="p-4 text-xs text-zinc-300 bg-zinc-950 overflow-x-auto">
        <code>{code}</code>
      </pre>
    </div>
  );
}

// ── Message Bubble ─────────────────────────────────────────────────────────

function MessageBubble({
  message,
  onChipClick,
}: {
  message: Message | (Message & { streaming?: boolean });
  onChipClick: (text: string) => void;
}) {
  const isUser = message.role === 'user';

  return (
    <div className={cn('flex gap-3', isUser && 'flex-row-reverse')}>
      <div
        className={cn(
          'h-7 w-7 rounded-full flex items-center justify-center shrink-0',
          isUser ? 'bg-zinc-700 text-xs' : 'bg-zinc-800',
        )}
      >
        {isUser ? 'U' : <Bot className="h-3.5 w-3.5" />}
      </div>

      <div className="flex-1 min-w-0">
        <div
          className={cn(
            'rounded-2xl px-4 py-3 text-sm max-w-[90%] break-words',
            isUser ? 'bg-zinc-800 text-zinc-100 ml-auto' : 'text-zinc-300',
          )}
        >
          {renderContent(message.content)}
          {'streaming' in message && message.streaming && (
            <span className="inline-block w-1 h-4 bg-zinc-400 ml-1 animate-pulse align-middle" />
          )}
        </div>

        {!isUser && message.components && (
          <div className="max-w-[90%]">
            {message.components.map((c, i) => {
              switch (c.type) {
                case 'suggestion_chips':
                  return <SuggestionChipsBlock key={i} chips={c.chips} onSelect={onChipClick} />;
                case 'action_buttons':
                  return <ActionButtonsBlock key={i} buttons={c.buttons} />;
                case 'code_block':
                  return <CodeBlockWidget key={i} language={c.language} code={c.code} />;
                case 'retrieval_panel':
                  return <RetrievalPanelBlock key={i} chunks={c.chunks} />;
                case 'tool_call':
                  return <ToolCallBlock key={i} toolCall={c} />;
              }
            })}
          </div>
        )}
      </div>
    </div>
  );
}

// ── Main Chat ──────────────────────────────────────────────────────────────

const WELCOME_MESSAGE: Message = {
  id: 'welcome',
  role: 'assistant',
  content: "Hello! I'm your RAG assistant. Ask me anything.",
  created_at: new Date().toISOString(),
};

export function ChatPage() {
  const [conversations, setConversations] = useState<ConversationSummary[]>([]);
  const [messages, setMessages] = useState<Message[]>([WELCOME_MESSAGE]);
  const [input, setInput] = useState('');
  const [typing, setTyping] = useState(false);
  const [conversationId, setConversationId] = useState<string | undefined>();
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const [providers, setProviders] = useState<LLMProviderConfig[]>([]);
  const [selectedProviderId, setSelectedProviderId] = useState('');

  const bottomRef = useRef<HTMLDivElement>(null);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  // load conversations
  const loadConversations = useCallback(async () => {
    try {
      const list = await api.chat.listConversations();
      setConversations(list);
    } catch (e) {
      console.error('Failed to load conversations', e);
    }
  }, []);

  // load providers
  useEffect(() => {
    api.providers.list(true).then((list) => {
      setProviders(list);
      const active = list.find((p) => p.is_active);
      if (active) setSelectedProviderId(active.id);
    });
    void loadConversations();
  }, [loadConversations]);

  // load messages for conversation
  const selectConversation = useCallback(async (id: string) => {
    setConversationId(id);
    setTyping(true);
    try {
      const msgs = await api.chat.getMessages(id);
      setMessages(msgs.length > 0 ? msgs : [WELCOME_MESSAGE]);
    } catch (e) {
      console.error('Failed to load messages', e);
      setMessages([WELCOME_MESSAGE]);
    } finally {
      setTyping(false);
    }
  }, []);

  const startNewChat = () => {
    setConversationId(undefined);
    setMessages([WELCOME_MESSAGE]);
    setInput('');
  };

  const deleteConversation = async (e: React.MouseEvent, id: string) => {
    e.stopPropagation();
    if (!confirm('Are you sure you want to delete this conversation?')) return;
    try {
      await api.chat.deleteConversation(id);
      if (conversationId === id) {
        startNewChat();
      }
      void loadConversations();
    } catch (e) {
      console.error('Failed to delete conversation', e);
    }
  };

  // scroll
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, typing]);

  // autosize
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
      const userMessageId = uid();
      const newUserMsg: Message = {
        id: userMessageId,
        role: 'user',
        content: trimmed,
        created_at: new Date().toISOString(),
      };
      setMessages((prev) => [...prev, newUserMsg]);

      setTyping(true);

      const assistantMessageId = uid();
      setMessages((prev) => [
        ...prev,
        { id: assistantMessageId, role: 'assistant', content: '', streaming: true, created_at: new Date().toISOString() },
      ]);

      try {
        const response = await api.chat.send({
          message: trimmed,
          provider_id: selectedProviderId || undefined,
          conversation_id: conversationId,
        });

        if (!response.ok) throw new Error('Failed to send message');

        const reader = response.body?.getReader();
        if (!reader) throw new Error('No reader');

        const decoder = new TextDecoder();
        let fullContent = '';
        let buffer = '';

        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          buffer += decoder.decode(value, { stream: true });
          const lines = buffer.split('\n');
          buffer = lines.pop() || '';

          for (const line of lines) {
            const trimmedLine = line.trim();
            if (trimmedLine.startsWith('data: ')) {
              const data = trimmedLine.slice(6).trim();
              if (data === '[DONE]') break;
              if (!data) continue;

              try {
                const parsed = JSON.parse(data);
                if (parsed.conversation_id && !conversationId) {
                  setConversationId(parsed.conversation_id);
                  void loadConversations();
                }
                if (parsed.content) {
                  fullContent += parsed.content;
                  setMessages((prev) =>
                    prev.map((m) =>
                      m.id === assistantMessageId ? { ...m, content: fullContent } : m,
                    ),
                  );
                }
                if (parsed.tool_call) {
                  const tc: ToolCall = { type: 'tool_call', ...parsed.tool_call };
                  setMessages((prev) =>
                    prev.map((m) => {
                      if (m.id !== assistantMessageId) return m;
                      const existing = m.components ?? [];
                      const idx = existing.findIndex(
                        (c) => c.type === 'tool_call' && c.id === tc.id,
                      );
                      const updated =
                        idx >= 0
                          ? existing.map((c, i) => (i === idx ? tc : c))
                          : [...existing, tc];
                      return { ...m, components: updated };
                    }),
                  );
                }
                if (parsed.components) {
                  setMessages((prev) =>
                    prev.map((m) =>
                      m.id === assistantMessageId ? { ...m, components: parsed.components } : m,
                    ),
                  );
                }
              } catch (e) {
                console.error('Error parsing SSE data', e);
              }
            }
          }
        }

        setMessages((prev) =>
          prev.map((m) => (m.id === assistantMessageId ? { ...m, streaming: false } : m)),
        );
      } catch (e) {
        console.error(e);
        setMessages((prev) =>
          prev.map((m) =>
            m.id === assistantMessageId
              ? { ...m, content: '❌ Error: cannot reach backend (FastAPI).', streaming: false }
              : m,
          ),
        );
      } finally {
        setTyping(false);
      }
    },
    [typing, selectedProviderId, conversationId, loadConversations],
  );

  function onKeyDown(e: React.KeyboardEvent<HTMLTextAreaElement>) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      void sendMessage(input);
    }
  }

  return (
    <div className="flex h-full overflow-hidden">
      {/* Sidebar */}
      <div
        style={{ width: sidebarOpen ? '260px' : '0' }}
        className={cn(
          'border-zinc-800 bg-zinc-950/50 flex flex-col transition-all duration-300 overflow-hidden shrink-0 z-20',
          sidebarOpen ? 'border-r opacity-100' : 'opacity-0',
        )}
      >
        {sidebarOpen && (
          <>
            <div className="p-4 border-b border-zinc-800 flex items-center justify-between">
              <button
                onClick={startNewChat}
                className="flex items-center gap-2 px-3 py-2 bg-zinc-800 hover:bg-zinc-700 rounded-lg text-sm transition-colors flex-1"
              >
                <Plus className="h-4 w-4" />
                New Chat
              </button>
            </div>

            <div className="flex-1 overflow-y-auto p-2 space-y-1">
              {conversations.map((c) => (
                <div
                  key={c.id}
                  onClick={() => void selectConversation(c.id)}
                  className={cn(
                    'group flex items-center gap-3 px-3 py-2 rounded-lg cursor-pointer transition-colors text-sm',
                    conversationId === c.id ? 'bg-zinc-800 text-zinc-100' : 'text-zinc-500 hover:bg-zinc-900 hover:text-zinc-300',
                  )}
                >
                  <MessageSquare className="h-4 w-4 shrink-0" />
                  <span className="truncate flex-1">{c.title || 'Untitled Chat'}</span>
                  <button
                    onClick={(e) => void deleteConversation(e, c.id)}
                    className="opacity-0 group-hover:opacity-100 p-1 hover:text-red-400 transition-all"
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                  </button>
                </div>
              ))}
            </div>
          </>
        )}
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0 bg-zinc-950">
        {/* header */}
        <div className="h-14 border-b border-zinc-800 flex items-center px-4 md:px-6">
          <button
            onClick={() => setSidebarOpen((s) => !s)}
            className="p-2 hover:bg-zinc-900 rounded-lg mr-2"
          >
            {sidebarOpen ? <X className="h-4 w-4" /> : <Menu className="h-4 w-4" />}
          </button>
          
          <Bot className="h-4 w-4 text-zinc-500 shrink-0" />
          <span className="ml-2 text-sm font-medium truncate mr-4">
            {conversationId ? conversations.find(c => c.id === conversationId)?.title || 'Chat' : 'New Chat'}
          </span>

          <div className="ml-auto flex items-center gap-2">
            <span className="text-[10px] uppercase tracking-wider text-zinc-600 font-bold hidden sm:inline">Model:</span>
            <select
              value={selectedProviderId}
              onChange={(e) => setSelectedProviderId(e.target.value)}
              className="text-xs bg-zinc-900 border border-zinc-700 px-2 py-1 rounded focus:outline-none focus:ring-1 focus:ring-zinc-600 max-w-[150px] md:max-w-none"
            >
              {providers.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name} — {p.model}
                </option>
              ))}
              {providers.length === 0 && <option disabled>No active models</option>}
            </select>
          </div>
        </div>

        {/* messages */}
        <div className="flex-1 overflow-y-auto p-4 md:p-6">
          <div className="max-w-3xl mx-auto space-y-6 pb-4">
            {messages.map((m) => (
              <MessageBubble key={m.id} message={m} onChipClick={(t) => void sendMessage(t)} />
            ))}

            {typing && (
              <div className="flex items-center gap-2 text-zinc-500 text-xs px-10">
                <Loader2 className="h-3 w-3 animate-spin" />
                Thinking...
              </div>
            )}

            <div ref={bottomRef} />
          </div>
        </div>

        {/* input */}
        <div className="border-t border-zinc-800 p-4 bg-zinc-950/80 backdrop-blur-sm">
          <div className="max-w-3xl mx-auto flex gap-2">
            <textarea
              ref={textareaRef}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={onKeyDown}
              placeholder="Ask something..."
              className="flex-1 bg-zinc-900 text-sm p-3 rounded-xl border border-zinc-800 focus:border-zinc-700 focus:outline-none resize-none transition-colors"
              rows={1}
            />

            <button
              onClick={() => void sendMessage(input)}
              disabled={!input.trim() || typing}
              className={cn(
                "px-4 rounded-xl transition-all",
                !input.trim() || typing 
                  ? "bg-zinc-800 text-zinc-500" 
                  : "bg-zinc-100 text-zinc-900 hover:bg-white"
              )}
            >
              <Send className="h-4 w-4" />
            </button>
          </div>
          <p className="text-[10px] text-zinc-600 text-center mt-3">
            AI can make mistakes. Check important info.
          </p>
        </div>
      </div>
    </div>
  );
}
