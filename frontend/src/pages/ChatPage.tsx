import { useState, useRef, useEffect, useCallback } from 'react';
import { Send, FileText, Bot } from 'lucide-react';
import { cn } from '@/lib/utils';
import { api, type LLMProviderConfig, type UIComponent } from '@/lib/api';

type Role = 'user' | 'assistant';

interface Message {
  id: string;
  role: Role;
  content: string;
  components?: UIComponent[];
  streaming?: boolean;
}

function uid() {
  return Math.random().toString(36).slice(2);
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

function CitationGroupBlock({
  citations,
}: {
  citations: Array<{ title: string; excerpt: string }>;
}) {
  return (
    <div className="mt-3 space-y-2">
      {citations.map((c, i) => (
        <div key={i} className="rounded-lg border border-zinc-800 bg-zinc-900/60 p-3">
          <div className="flex items-center gap-2 mb-1.5">
            <FileText className="h-3.5 w-3.5 text-zinc-500" />
            <span className="text-xs text-zinc-400">{c.title}</span>
          </div>
          <p className="text-xs text-zinc-500 line-clamp-2">{c.excerpt}</p>
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
  message: Message;
  onChipClick: (text: string) => void;
}) {
  const isUser = message.role === 'user';

  return (
    <div className={cn('flex gap-3', isUser && 'flex-row-reverse')}>
      <div
        className={cn(
          'h-7 w-7 rounded-full flex items-center justify-center',
          isUser ? 'bg-zinc-700 text-xs' : 'bg-zinc-800',
        )}
      >
        {isUser ? 'U' : <Bot className="h-3.5 w-3.5" />}
      </div>

      <div className="flex-1">
        <div
          className={cn(
            'rounded-2xl px-4 py-3 text-sm max-w-[85%]',
            isUser ? 'bg-zinc-800 text-zinc-100 ml-auto' : 'text-zinc-300',
          )}
        >
          {renderContent(message.content)}
          {message.streaming && (
            <span className="inline-block w-1 h-4 bg-zinc-400 ml-1 animate-pulse" />
          )}
        </div>

        {!isUser && message.components && (
          <div className="max-w-[85%]">
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

// ── Main Chat ──────────────────────────────────────────────────────────────

export function ChatPage() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: 'welcome',
      role: 'assistant',
      content: "Hello! I'm your RAG assistant. Ask me anything.",
    },
  ]);

  const [input, setInput] = useState('');
  const [typing, setTyping] = useState(false);
  const [conversationId, setConversationId] = useState<string | undefined>();

  const [providers, setProviders] = useState<LLMProviderConfig[]>([]);
  const [selectedProviderId, setSelectedProviderId] = useState('');

  const bottomRef = useRef<HTMLDivElement>(null);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  // providers
  useEffect(() => {
    api.providers.list().then((list) => {
      setProviders(list);
      const active = list.find((p) => p.is_active);
      if (active) setSelectedProviderId(active.id);
    });
  }, []);

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
      setMessages((prev) => [...prev, { id: userMessageId, role: 'user', content: trimmed }]);

      setTyping(true);

      const assistantMessageId = uid();
      setMessages((prev) => [
        ...prev,
        { id: assistantMessageId, role: 'assistant', content: '', streaming: true },
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
                if (parsed.conversation_id) {
                  setConversationId(parsed.conversation_id);
                }
                if (parsed.content) {
                  fullContent += parsed.content;
                  setMessages((prev) =>
                    prev.map((m) =>
                      m.id === assistantMessageId ? { ...m, content: fullContent } : m,
                    ),
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
    [typing, selectedProviderId, conversationId],
  );

  function onKeyDown(e: React.KeyboardEvent<HTMLTextAreaElement>) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      void sendMessage(input);
    }
  }

  return (
    <div className="flex flex-col h-full">
      {/* header */}
      <div className="h-14 border-b border-zinc-800 flex items-center px-6">
        <Bot className="h-4 w-4 text-zinc-500" />
        <span className="ml-2 text-sm">Chat</span>

        <div className="ml-auto">
          <select
            value={selectedProviderId}
            onChange={(e) => setSelectedProviderId(e.target.value)}
            className="text-xs bg-zinc-900 border border-zinc-700 px-2 py-1 rounded"
          >
            {providers.map((p) => (
              <option key={p.id} value={p.id}>
                {p.name} — {p.model}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* messages */}
      <div className="flex-1 overflow-y-auto p-6">
        <div className="max-w-3xl mx-auto space-y-6">
          {messages.map((m) => (
            <MessageBubble key={m.id} message={m} onChipClick={(t) => void sendMessage(t)} />
          ))}

          {typing && <div className="text-zinc-500 text-sm">Thinking...</div>}

          <div ref={bottomRef} />
        </div>
      </div>

      {/* input */}
      <div className="border-t border-zinc-800 p-4">
        <div className="max-w-3xl mx-auto flex gap-2">
          <textarea
            ref={textareaRef}
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={onKeyDown}
            placeholder="Ask something..."
            className="flex-1 bg-zinc-900 text-sm p-3 rounded resize-none"
          />

          <button
            onClick={() => void sendMessage(input)}
            disabled={!input.trim() || typing}
            className="px-4 bg-zinc-100 text-zinc-900 rounded"
          >
            <Send className="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
