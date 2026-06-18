import { useRef, useEffect } from 'react';
import { Send } from 'lucide-react';
import { cn } from '@/lib/utils';

interface ChatInputProps {
  input: string;
  setInput: (value: string) => void;
  onSendMessage: (text: string) => void;
  typing: boolean;
}

export function ChatInput({ input, setInput, onSendMessage, typing }: ChatInputProps) {
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    const el = textareaRef.current;
    if (!el) return;
    el.style.height = 'auto';
    el.style.height = Math.min(el.scrollHeight, 160) + 'px';
  }, [input]);

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      onSendMessage(input);
    }
  };

  return (
    <div className="border-t border-zinc-800 p-4 bg-zinc-950/80 backdrop-blur-sm">
      <div className="max-w-3xl mx-auto flex gap-2">
        <textarea
          ref={textareaRef}
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Ask something..."
          className="flex-1 bg-zinc-900 text-sm p-3 rounded-xl border border-zinc-800 focus:border-zinc-700 focus:outline-none resize-none transition-colors"
          rows={1}
        />

        <button
          onClick={() => onSendMessage(input)}
          disabled={!input.trim() || typing}
          className={cn(
            'px-4 rounded-xl transition-all',
            !input.trim() || typing
              ? 'bg-zinc-800 text-zinc-500'
              : 'bg-zinc-100 text-zinc-900 hover:bg-white',
          )}
        >
          <Send className="h-4 w-4" />
        </button>
      </div>
      <p className="text-[10px] text-zinc-600 text-center mt-3">
        AI can make mistakes. Check important info.
      </p>
    </div>
  );
}
