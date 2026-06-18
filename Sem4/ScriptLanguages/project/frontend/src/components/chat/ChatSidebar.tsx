import { Plus, MessageSquare, Trash2 } from 'lucide-react';
import { cn } from '@/lib/utils';
import { type ConversationSummary } from '@/lib/api';

interface ChatSidebarProps {
  conversations: ConversationSummary[];
  conversationId?: string;
  sidebarOpen: boolean;
  onNewChat: () => void;
  onSelectConversation: (id: string) => void;
  onDeleteConversation: (e: React.MouseEvent, id: string) => void;
}

export function ChatSidebar({
  conversations,
  conversationId,
  sidebarOpen,
  onNewChat,
  onSelectConversation,
  onDeleteConversation,
}: ChatSidebarProps) {
  return (
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
              onClick={onNewChat}
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
                onClick={() => onSelectConversation(c.id)}
                className={cn(
                  'group flex items-center gap-3 px-3 py-2 rounded-lg cursor-pointer transition-colors text-sm',
                  conversationId === c.id
                    ? 'bg-zinc-800 text-zinc-100'
                    : 'text-zinc-500 hover:bg-zinc-900 hover:text-zinc-300',
                )}
              >
                <MessageSquare className="h-4 w-4 shrink-0" />
                <span className="truncate flex-1">{c.title || 'Untitled Chat'}</span>
                <button
                  onClick={(e) => onDeleteConversation(e, c.id)}
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
  );
}
