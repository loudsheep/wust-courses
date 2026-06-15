import { useState, useEffect, useCallback } from 'react';
import { Plus, Trash2, Zap } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { api, type LLMProviderConfig, type LLMProvider } from '@/lib/api';

const PROVIDER_LABELS: Record<LLMProvider, string> = {
  anthropic: 'Anthropic',
  openai: 'OpenAI',
  ollama: 'Ollama',
  gemini: 'Gemini',
  openrouter: 'OpenRouter',
  custom: 'Custom',
};

const PROVIDER_COLORS: Record<LLMProvider, string> = {
  anthropic: 'bg-purple-950 text-purple-400 border border-purple-900',
  openai: 'bg-emerald-950 text-emerald-400 border border-emerald-900',
  ollama: 'bg-orange-950 text-orange-400 border border-orange-900',
  gemini: 'bg-blue-950 text-blue-400 border border-blue-900',
  openrouter: 'bg-zinc-950 text-zinc-400 border border-zinc-900',
  custom: 'bg-slate-950 text-slate-400 border border-slate-900',
};

const DEFAULT_MODELS: Record<LLMProvider, string> = {
  anthropic: 'claude-sonnet-4-5',
  openai: 'gpt-4o',
  ollama: 'llama3.2',
  gemini: 'gemini-1.5-pro',
  openrouter: 'google/gemini-2.0-flash-001',
  custom: 'my-model',
};

const EMPTY_FORM = {
  name: '',
  provider: 'anthropic' as LLMProvider,
  model: DEFAULT_MODELS.anthropic,
  api_key: '',
  base_url: '',
};

export function ProvidersPage() {
  const [providers, setProviders] = useState<LLMProviderConfig[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);
  const [saving, setSaving] = useState(false);

  const load = useCallback(async () => {
    try {
      setProviders(await api.providers.list());
      setError(null);
    } catch {
      setError('Could not reach the backend. Is it running on :8000?');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  function handleProviderChange(provider: LLMProvider) {
    setForm((f) => ({ ...f, provider, model: DEFAULT_MODELS[provider] }));
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    try {
      await api.providers.create({
        name: form.name,
        provider: form.provider,
        model: form.model,
        api_key: form.api_key || undefined,
        base_url: form.base_url || undefined,
      });
      setOpen(false);
      setForm(EMPTY_FORM);
      void load();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create provider');
    } finally {
      setSaving(false);
    }
  }

  async function handleToggleActive(id: string) {
    try {
      const res = await api.providers.toggleActive(id);

      if (!res.success) {
        setError(res.message);
        return;
      }

      setError(null);
      void load();
    } catch (err: any) {
      setError(err?.response?.data?.detail ?? err?.message ?? 'Failed to toggle provider status');
    }
  }

  async function handleDelete(id: string) {
    try {
      await api.providers.delete(id);
      void load();
    } catch {
      setError('Failed to delete provider');
    }
  }

  return (
    <div className="p-8">
      <div className="flex items-start justify-between mb-8">
        <div>
          <h1 className="text-xl font-semibold text-zinc-100">LLM Providers</h1>
          <p className="text-sm text-zinc-500 mt-1">
            Configure the AI providers used for chat responses.
          </p>
        </div>
        <Button onClick={() => setOpen(true)}>
          <Plus className="h-4 w-4" />
          Add Provider
        </Button>
      </div>

      {error && (
        <div className="mb-6 px-4 py-3 rounded-lg bg-red-950/50 border border-red-900 text-red-400 text-sm">
          {error}
        </div>
      )}

      {loading ? (
        <div className="text-sm text-zinc-600">Loading…</div>
      ) : providers.length === 0 ? (
        <div className="text-center py-24">
          <p className="font-medium text-zinc-400 mb-1">No providers configured</p>
          <p className="text-sm text-zinc-600">
            Add a provider to start chatting with your documents.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {providers.map((p) => (
            <Card key={p.id} className={p.is_active ? 'border-zinc-600 ring-1 ring-zinc-700' : ''}>
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between gap-2">
                  <CardTitle className="text-sm">{p.name}</CardTitle>
                  {p.is_active && (
                    <span className="shrink-0 text-xs font-medium text-emerald-400 bg-emerald-950 border border-emerald-900 px-2 py-0.5 rounded-full">
                      Active
                    </span>
                  )}
                </div>
                <span
                  className={`inline-block text-xs font-medium px-2 py-0.5 rounded-full w-fit mt-1 ${PROVIDER_COLORS[p.provider]}`}
                >
                  {PROVIDER_LABELS[p.provider]}
                </span>
              </CardHeader>
              <CardContent>
                <p className="text-xs text-zinc-500 font-mono mb-4 truncate">{p.model}</p>
                <div className="flex items-center gap-2">
                  <Button
                    size="sm"
                    variant={p.is_active ? 'outline' : 'default'}
                    onClick={() => void handleToggleActive(p.id)}
                    className={p.is_active ? 'border-red-900/50 text-red-400 hover:bg-red-950/20' : ''}
                  >
                    <Zap className="h-3.5 w-3.5" />
                    {p.is_active ? 'Deactivate' : 'Activate'}
                  </Button>
                  <Button
                    size="sm"
                    variant="ghost"
                    onClick={() => void handleDelete(p.id)}
                    className="ml-auto text-zinc-600 hover:text-red-400 hover:bg-red-950/40"
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add Provider</DialogTitle>
          </DialogHeader>
          <form onSubmit={(e) => void handleSubmit(e)} className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="name">Name</Label>
              <Input
                id="name"
                placeholder="My Anthropic"
                value={form.name}
                onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                required
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="provider">Provider</Label>
              <select
                id="provider"
                className="w-full h-9 rounded-md border border-zinc-700 bg-zinc-800 px-3 text-sm text-zinc-100 focus:outline-none focus:ring-1 focus:ring-zinc-400"
                value={form.provider}
                onChange={(e) => handleProviderChange(e.target.value as LLMProvider)}
              >
                <option value="anthropic">Anthropic</option>
                <option value="openai">OpenAI</option>
                <option value="ollama">Ollama</option>
                <option value="gemini">Gemini</option>
                <option value="openrouter">OpenRouter</option>
                <option value="custom">Custom</option>
              </select>
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="model">Model</Label>
              <Input
                id="model"
                placeholder={DEFAULT_MODELS[form.provider]}
                value={form.model}
                onChange={(e) => setForm((f) => ({ ...f, model: e.target.value }))}
                required
              />
            </div>

            {form.provider !== 'ollama' && (
              <div className="space-y-1.5">
                <Label htmlFor="api_key">
                  API Key <span className="text-zinc-600 font-normal">(optional)</span>
                </Label>
                <Input
                  id="api_key"
                  type="password"
                  placeholder="sk-…"
                  value={form.api_key}
                  onChange={(e) => setForm((f) => ({ ...f, api_key: e.target.value }))}
                />
              </div>
            )}

            {(form.provider === 'ollama' || form.provider === 'openrouter' || form.provider === 'custom') && (
              <div className="space-y-1.5">
                <Label htmlFor="base_url">
                  Base URL <span className="text-zinc-600 font-normal">(optional)</span>
                </Label>
                <Input
                  id="base_url"
                  placeholder={form.provider === 'ollama' ? 'http://localhost:11434' : 'https://openrouter.ai/api/v1'}
                  value={form.base_url}
                  onChange={(e) => setForm((f) => ({ ...f, base_url: e.target.value }))}
                />
              </div>
            )}

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="ghost" onClick={() => setOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" disabled={saving}>
                {saving ? 'Saving…' : 'Add Provider'}
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
