import { Menu, X, Bot } from 'lucide-react';
import { type LLMProviderConfig, type ConversationSummary } from '@/lib/api';

interface ChatHeaderProps {
  sidebarOpen: boolean;
  onToggleSidebar: () => void;
  conversationId?: string;
  conversations: ConversationSummary[];
  providers: LLMProviderConfig[];
  selectedProviderId: string;
  onProviderChange: (id: string) => void;
}

export function ChatHeader({
  sidebarOpen,
  onToggleSidebar,
  conversationId,
  conversations,
  providers,
  selectedProviderId,
  onProviderChange,
}: ChatHeaderProps) {
  const currentTitle = conversationId
    ? conversations.find((c) => c.id === conversationId)?.title || 'Chat'
    : 'New Chat';

  return (
    <div className="h-14 border-b border-zinc-800 flex items-center px-4 md:px-6">
      <button onClick={onToggleSidebar} className="p-2 hover:bg-zinc-900 rounded-lg mr-2">
        {sidebarOpen ? <X className="h-4 w-4" /> : <Menu className="h-4 w-4" />}
      </button>

      <Bot className="h-4 w-4 text-zinc-500 shrink-0" />
      <span className="ml-2 text-sm font-medium truncate mr-4">{currentTitle}</span>

      <div className="ml-auto flex items-center gap-2">
        <span className="text-[10px] uppercase tracking-wider text-zinc-600 font-bold hidden sm:inline">
          Model:
        </span>
        <select
          value={selectedProviderId}
          onChange={(e) => onProviderChange(e.target.value)}
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
  );
}
