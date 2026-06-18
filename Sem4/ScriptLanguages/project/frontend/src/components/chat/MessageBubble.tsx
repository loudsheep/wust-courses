import { useState } from 'react';
import { Loader2, AlertCircle, CheckCircle2, Bot } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { cn } from '@/lib/utils';
import { type ToolCall, type Message } from '@/lib/api';

function MarkdownContent({ content }: { content: string }) {
  return (
    <ReactMarkdown
      remarkPlugins={[remarkGfm]}
      components={{
        p: ({ children }) => <p className="mb-3 last:mb-0">{children}</p>,
        strong: ({ children }) => <strong className="font-semibold text-zinc-100">{children}</strong>,
        a: ({ children, href }) => (
          <a href={href} className="text-zinc-100 underline underline-offset-4 hover:text-white" target="_blank" rel="noreferrer">
            {children}
          </a>
        ),
        ul: ({ children }) => <ul className="list-disc ml-4 mb-3 space-y-1">{children}</ul>,
        ol: ({ children }) => <ol className="list-decimal ml-4 mb-3 space-y-1">{children}</ol>,
        li: ({ children }) => <li className="text-zinc-300">{children}</li>,
        h1: ({ children }) => <h1 className="text-xl font-bold text-zinc-100 mt-6 mb-3 first:mt-0">{children}</h1>,
        h2: ({ children }) => <h2 className="text-lg font-bold text-zinc-100 mt-5 mb-2 first:mt-0">{children}</h2>,
        h3: ({ children }) => <h3 className="text-base font-bold text-zinc-100 mt-4 mb-2 first:mt-0">{children}</h3>,
        code: ({ children, className, ...props }) => {
          const match = /language-(\w+)/.exec(className || '');
          const isInline = !className;
          
          if (!isInline && match) {
            return (
              <CodeBlockWidget 
                language={match[1]} 
                code={String(children).replace(/\n$/, '')} 
              />
            );
          }

          return isInline ? (
            <code className="bg-zinc-800 text-zinc-200 px-1.5 py-0.5 rounded text-[13px] font-mono border border-zinc-700" {...props}>
              {children}
            </code>
          ) : (
            <code className={className} {...props}>{children}</code>
          );
        },
        pre: ({ children }) => <>{children}</>,
        blockquote: ({ children }) => (
          <blockquote className="border-l-2 border-zinc-700 pl-4 italic my-4 text-zinc-400">
            {children}
          </blockquote>
        ),
      }}
    >
      {content}
    </ReactMarkdown>
  );
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
  get_document_metadata: 'Fetching document metadata',
  keyword_search: 'Searching for keywords',
  search_documents_filtered: 'Searching documents with filters',
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

interface MessageBubbleProps {
  message: Message | (Message & { streaming?: boolean });
  onChipClick: (text: string) => void;
}

export function MessageBubble({ message, onChipClick }: MessageBubbleProps) {
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
          <MarkdownContent content={message.content} />
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
