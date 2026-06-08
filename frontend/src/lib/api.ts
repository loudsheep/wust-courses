const BASE_URL =
  (import.meta.env.VITE_API_URL as string | undefined) ??
  "http://localhost:8000";

export type LLMProvider = "anthropic" | "openai" | "ollama" | "gemini";

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

export interface Document {
  id: string;
  original_filename: string;
  stored_filename: string;
  file_size: number;
  mime_type: string;
  status: "pending" | "indexing" | "indexed" | "failed";
  error_message: string | null;
  chunk_count: number | null;
  is_stale: boolean;
  created_at: string;
  updated_at: string;
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json", ...options?.headers },
    ...options,
  });
  if (!res.ok) {
    const body = await res.text().catch(() => "");
    throw new Error(
      `${res.status} ${res.statusText}${body ? `: ${body}` : ""}`,
    );
  }
  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

export const api = {
  providers: {
    list: () => request<LLMProviderConfig[]>("/api/v1/llm-providers"),
    create: (data: LLMProviderCreate) =>
      request<LLMProviderConfig>("/api/v1/llm-providers", {
        method: "POST",
        body: JSON.stringify(data),
      }),
    activate: (id: string) =>
      request<LLMProviderConfig>(`/api/v1/llm-providers/${id}/activate`, {
        method: "PATCH",
      }),
    delete: (id: string) =>
      request<void>(`/api/v1/llm-providers/${id}`, { method: "DELETE" }),
  },
  documents: {
    upload: async (file: File): Promise<Document> => {
      const formData = new FormData();
      formData.append("file", file);

      const res = await fetch(`${BASE_URL}/api/v1/documents`, {
        method: "POST",
        body: formData,
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(
          `${res.status} ${res.statusText}: ${JSON.stringify(data)}`,
        );
      }

      return data;
    },
    list: () => request<Document[]>("/api/v1/documents"),
    delete: (id: string) =>
      request<void>(`/api/v1/documents/${id}`, { method: "DELETE" }),
  },
};
