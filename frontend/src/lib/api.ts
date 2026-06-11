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

export interface Citation {
  document_id: string;
  document_name: string;
  chunk_index: number;
  page_number: number | null;
  excerpt: string;
  score: number;
}

export interface CitationGroup {
  type: 'citation_group';
  citations: Citation[];
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

export type UIComponent = SuggestionChips | CitationGroup | ActionButtons | CodeBlock | RetrievalPanel;

export interface ChatResponse {
  content: string;
  components?: UIComponent[];
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json', ...options?.headers },
    ...options,
  });
  if (!res.ok) {
    const body = await res.text().catch(() => '');
    throw new Error(`${res.status} ${res.statusText}${body ? `: ${body}` : ''}`);
  }
  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

export const api = {
  providers: {
    list: () => request<LLMProviderConfig[]>('/api/v1/llm-providers'),
    create: (data: LLMProviderCreate) =>
      request<LLMProviderConfig>('/api/v1/llm-providers', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    activate: (id: string) =>
      request<ActivateProviderResponse>(`/api/v1/llm-providers/${id}/activate`, {
        method: 'POST',
      }),
    delete: (id: string) => request<void>(`/api/v1/llm-providers/${id}`, { method: 'DELETE' }),
  },
  chat: {
    send: (body: ChatRequest) =>
      fetch(`${BASE_URL}/api/v1/chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      }),
  },
  documents: {
    upload: async (file: File): Promise<Document> => {
      const formData = new FormData();
      formData.append('file', file);

      const res = await fetch(`${BASE_URL}/api/v1/documents`, {
        method: 'POST',
        body: formData,
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(`${res.status} ${res.statusText}: ${JSON.stringify(data)}`);
      }

      return data;
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
