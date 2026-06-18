const BASE_URL = (import.meta.env.VITE_API_URL as string | undefined) ?? 'http://localhost:8000';

export type LLMProvider = 'anthropic' | 'openai' | 'ollama' | 'gemini' | 'openrouter' | 'custom';

export interface LLMProviderConfig {
  id: string;
  name: string;
  provider: LLMProvider;
  model: string;
  base_url: string | null;
  is_active: boolean;
  created_at: string;
  updated_at: string;
}

export interface LLMProviderCreate {
  name: string;
  provider: LLMProvider;
  model: string;
  api_key?: string;
  base_url?: string;
}

export interface ActivateProviderResponse {
  success: boolean;
  message: string;
}

export interface Document {
  id: string;
  original_filename: string;
  stored_filename: string;
  file_path: string;
  file_size: number;
  mime_type: string;
  status: 'pending' | 'indexing' | 'indexed' | 'failed';
  error_message: string | null;
  chunk_count: number | null;
  chunk_size: number | null;
  chunk_overlap: number | null;
  is_stale: boolean;
  deleted_at: string | null;
  created_at: string;
  updated_at: string;
}

export interface ReindexResponse {
  enqueued: string[];
  skipped: string[];
}

export interface ChatRequest {
  message: string;
  provider_id?: string;
  conversation_id?: string;
}

export interface SuggestionChips {
  type: 'suggestion_chips';
  chips: string[];
}

export interface RetrievalChunk {
  document_name: string;
  excerpt: string;
  score: number;
  chunk_index: number;
}

export interface RetrievalPanel {
  type: 'retrieval_panel';
  chunks: RetrievalChunk[];
}

export interface ActionButton {
  label: string;
  primary?: boolean;
}

export interface ActionButtons {
  type: 'action_buttons';
  buttons: ActionButton[];
}

export interface CodeBlock {
  type: 'code_block';
  language: string;
  code: string;
}

export interface ToolCall {
  type: 'tool_call';
  id: string;
  tool: string;
  status: 'running' | 'done' | 'error';
  args: Record<string, unknown>;
  result_summary?: string;
}

export type UIComponent = SuggestionChips | ActionButtons | CodeBlock | RetrievalPanel | ToolCall;

export interface ChatResponse {
  content: string;
  components?: UIComponent[];
}

export async function extractDetail(res: Response): Promise<string | null> {
  try {
    const data = await res.json();
    if (typeof data?.detail === 'string') return data.detail;
  } catch {
    // body wasn't JSON (or already consumed) — fall through
  }
  return null;
}

export function uploadError(status: number, detail: string | null): string {
  if (status === 415) {
    if (detail?.toLowerCase().startsWith('unsupported file type')) {
      const ext = detail.split(':').pop()?.trim() || 'this type';
      return `Can't upload ${ext} files. Supported types: PDF, DOCX, TXT, MD.`;
    }
    if (detail?.toLowerCase().includes('does not match')) {
      return "This file's content doesn't match its extension — make sure it's a real PDF, DOCX, TXT, or MD file.";
    }
    return detail ?? 'This file type is not supported. Supported types: PDF, DOCX, TXT, MD.';
  }
  return detail ?? `Upload failed (${status}). Please try again.`;
}

export function chatError(status: number, detail: string | null): string {
  if (status === 400 && detail?.toLowerCase().includes('no active provider')) {
    return 'No active LLM provider. Add one and activate it on the Providers page before chatting.';
  }
  return detail ?? `Could not send message (${status}). Please try again.`;
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  let res: Response;
  try {
    res = await fetch(`${BASE_URL}${path}`, {
      headers: { 'Content-Type': 'application/json', ...options?.headers },
      ...options,
    });
  } catch {
    throw new Error('Could not reach the backend. Is it running?');
  }
  if (!res.ok) {
    const detail = await extractDetail(res);
    throw new Error(detail ?? `${res.status} ${res.statusText}`);
  }
  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

export interface ConversationSummary {
  id: string;
  title: string | null;
  created_at: string;
  message_count: number;
}

export interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  components?: UIComponent[];
  created_at: string;
}

export const api = {
  providers: {
    list: (activeOnly = false) =>
      request<LLMProviderConfig[]>(`/api/v1/llm-providers${activeOnly ? '?active_only=true' : ''}`),
    create: (data: LLMProviderCreate) =>
      request<LLMProviderConfig>('/api/v1/llm-providers', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    toggleActive: (id: string) =>
      request<ActivateProviderResponse>(`/api/v1/llm-providers/${id}/toggle-active`, {
        method: 'POST',
      }),
    delete: (id: string) => request<void>(`/api/v1/llm-providers/${id}`, { method: 'DELETE' }),
  },
  chat: {
    send: async (body: ChatRequest) => {
      try {
        return await fetch(`${BASE_URL}/api/v1/chat`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(body),
        });
      } catch {
        throw new Error('Could not reach the backend. Is it running?');
      }
    },
    getConfig: () => request<{ history_limit: number }>('/api/v1/chat/config'),
    listConversations: () => request<ConversationSummary[]>('/api/v1/chat/conversations'),
    getMessages: (conversationId: string) =>
      request<Message[]>(`/api/v1/chat/conversations/${conversationId}/messages`),
    deleteConversation: (id: string) =>
      request<void>(`/api/v1/chat/conversations/${id}`, { method: 'DELETE' }),
  },
  documents: {
    upload: async (file: File): Promise<Document> => {
      const formData = new FormData();
      formData.append('file', file);

      let res: Response;
      try {
        res = await fetch(`${BASE_URL}/api/v1/documents`, {
          method: 'POST',
          body: formData,
        });
      } catch {
        throw new Error('Could not reach the backend. Is it running?');
      }

      if (!res.ok) {
        const detail = await extractDetail(res);
        throw new Error(uploadError(res.status, detail));
      }

      return res.json();
    },
    list: () => request<Document[]>('/api/v1/documents'),
    get: (id: string) => request<Document>(`/api/v1/documents/${id}`),
    delete: (id: string) => request<void>(`/api/v1/documents/${id}`, { method: 'DELETE' }),
    reindex: (id: string) =>
      request<Document>(`/api/v1/documents/${id}/reindex`, { method: 'POST' }),
    reindexStale: () =>
      request<ReindexResponse>('/api/v1/documents/reindex', {
        method: 'POST',
        body: JSON.stringify({ document_ids: null }),
      }),
  },
};
