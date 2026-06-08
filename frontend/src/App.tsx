import { useState } from 'react';
import { MessageSquare, FileText, Settings2 } from 'lucide-react';
import { cn } from '@/lib/utils';
import { ChatPage } from '@/pages/ChatPage';
import { ProvidersPage } from '@/pages/ProvidersPage';
import { DocumentsPage } from '@/pages/DocumentsPage';

type Page = 'chat' | 'documents' | 'providers';

const nav: { id: Page; label: string; icon: typeof MessageSquare }[] = [
  { id: 'chat', label: 'Chat', icon: MessageSquare },
  { id: 'documents', label: 'Documents', icon: FileText },
  { id: 'providers', label: 'LLM Providers', icon: Settings2 },
];

export default function App() {
  const [page, setPage] = useState<Page>('chat');

  return (
    <div className="flex h-screen bg-zinc-950 text-zinc-100 overflow-hidden">
      {/* Sidebar */}
      <aside className="w-52 shrink-0 border-r border-zinc-800 bg-zinc-950 flex flex-col">
        <div className="px-4 h-14 flex items-center border-b border-zinc-800/60">
          <span className="font-semibold text-sm text-zinc-200 tracking-tight">RAG Insights</span>
        </div>
        <nav className="flex-1 p-2 space-y-0.5 pt-3">
          {nav.map(({ id, label, icon: Icon }) => (
            <button
              key={id}
              onClick={() => setPage(id)}
              className={cn(
                'w-full flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm transition-colors text-left',
                page === id
                  ? 'bg-zinc-800 text-zinc-100 font-medium'
                  : 'text-zinc-500 hover:text-zinc-300 hover:bg-zinc-800/50',
              )}
            >
              <Icon className="h-4 w-4 shrink-0" />
              {label}
            </button>
          ))}
        </nav>
        <div className="p-4 border-t border-zinc-800/60">
          <div className="flex items-center gap-2">
            <div className="h-6 w-6 rounded-full bg-zinc-700 shrink-0" />
            <span className="text-xs text-zinc-500 truncate">Local instance</span>
          </div>
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 min-w-0 overflow-hidden flex flex-col">
        {page === 'chat' && <ChatPage />}
        {page === 'documents' && (
          <div className="flex-1 overflow-auto">
            <DocumentsPage />
          </div>
        )}
        {page === 'providers' && (
          <div className="flex-1 overflow-auto">
            <ProvidersPage />
          </div>
        )}
      </main>
    </div>
  );
}
