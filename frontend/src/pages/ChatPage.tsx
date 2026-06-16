import { useState, useRef, useEffect, useCallback } from 'react';
import { Loader2 } from 'lucide-react';
import { api } from '@/lib/api';
import type { LLMProviderConfig, ToolCall, ConversationSummary, Message } from '@/lib/api';
import { ChatSidebar } from '@/components/chat/ChatSidebar';
import { ChatHeader } from '@/components/chat/ChatHeader';
import { ChatInput } from '@/components/chat/ChatInput';
import { MessageBubble } from '@/components/chat/MessageBubble';

function uid() {
  return Math.random().toString(36).slice(2);
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

  return (
    <div className="flex h-full overflow-hidden">
      <ChatSidebar
        conversations={conversations}
        conversationId={conversationId}
        sidebarOpen={sidebarOpen}
        onNewChat={startNewChat}
        onSelectConversation={selectConversation}
        onDeleteConversation={deleteConversation}
      />

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0 bg-zinc-950">
        <ChatHeader
          sidebarOpen={sidebarOpen}
          onToggleSidebar={() => setSidebarOpen((s) => !s)}
          conversationId={conversationId}
          conversations={conversations}
          providers={providers}
          selectedProviderId={selectedProviderId}
          onProviderChange={setSelectedProviderId}
        />

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

        <ChatInput
          input={input}
          setInput={setInput}
          onSendMessage={sendMessage}
          typing={typing}
        />
      </div>
    </div>
  );
}
