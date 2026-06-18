import { api, type Document } from '@/lib/api';
import { useCallback, useEffect, useRef, useState } from 'react';
import {
  AlertCircle,
  CheckCircle2,
  Clock,
  FileUp,
  FileText,
  Loader2,
  RefreshCw,
  Trash2,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { cn, formatFileSize } from '@/lib/utils';

const STATUS_META: Record<
  Document['status'],
  { label: string; variant: 'default' | 'secondary' | 'success' | 'destructive'; icon: typeof Clock }
> = {
  pending: { label: 'Pending', variant: 'secondary', icon: Clock },
  indexing: { label: 'Indexing…', variant: 'default', icon: Loader2 },
  indexed: { label: 'Indexed', variant: 'success', icon: CheckCircle2 },
  failed: { label: 'Failed', variant: 'destructive', icon: AlertCircle },
};

function StatusBadge({ status }: { status: Document['status'] }) {
  const meta = STATUS_META[status];
  const Icon = meta.icon;

  return (
    <Badge variant={meta.variant} className="gap-1">
      <Icon className={cn('h-3 w-3', status === 'indexing' && 'animate-spin')} />
      {meta.label}
    </Badge>
  );
}

export function DocumentsPage() {
  const [dragging, setDragging] = useState(false);
  const [documents, setDocuments] = useState<Document[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [pendingDelete, setPendingDelete] = useState<Document | null>(null);
  const [reindexingIds, setReindexingIds] = useState<Set<string>>(new Set());
  const [selectedDoc, setSelectedDoc] = useState<Document | null>(null);

  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const load = useCallback(async () => {
    try {
      setDocuments(await api.documents.list());
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

  async function handleUpload(file: File) {
    try {
      await api.documents.upload(file);
      setError(null);
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to upload document');
    }
  }

  async function handleDelete(doc: Document) {
    try {
      await api.documents.delete(doc.id);
      setDocuments((prev) => prev.filter((d) => d.id !== doc.id));
    } catch {
      setError('Failed to delete document');
    } finally {
      setPendingDelete(null);
    }
  }

  async function handleReindex(doc: Document) {
    setReindexingIds((prev) => new Set(prev).add(doc.id));
    try {
      const updated = await api.documents.reindex(doc.id);
      setDocuments((prev) => prev.map((d) => (d.id === doc.id ? updated : d)));
    } catch {
      setError('Failed to start reindexing');
    } finally {
      setReindexingIds((prev) => {
        const next = new Set(prev);
        next.delete(doc.id);
        return next;
      });
    }
  }

  const staleCount = documents.filter((d) => d.is_stale).length;

  return (
    <div className="p-8">
      <div className="flex items-start justify-between mb-8">
        <div>
          <h1 className="text-xl font-semibold text-zinc-100">Documents</h1>
          <p className="text-sm text-zinc-500 mt-1">
            Upload and manage your knowledge base documents.
          </p>
        </div>

        <div className="flex items-center gap-2">
          {staleCount > 0 && (
            <Button
              variant="outline"
              onClick={() => {
                api.documents
                  .reindexStale()
                  .then(() => load())
                  .catch(() => setError('Failed to start reindexing'));
              }}
            >
              <RefreshCw className="h-4 w-4" />
              Reindex {staleCount} stale
            </Button>
          )}

          <Button variant="outline" size="icon" onClick={() => void load()} title="Refresh">
            <RefreshCw className="h-4 w-4" />
          </Button>

          <Button onClick={() => fileInputRef.current?.click()}>Upload</Button>
        </div>

        <input
          ref={fileInputRef}
          type="file"
          className="hidden"
          onChange={(e) => {
            const file = e.target.files?.[0];
            if (file) void handleUpload(file);
            e.target.value = '';
          }}
        />
      </div>

      {error && (
        <div className="mb-6 px-4 py-3 rounded-lg bg-red-950/50 border border-red-900 text-red-400 text-sm">
          {error}
        </div>
      )}

      {/* Drop zone */}
      <div
        className={cn(
          'rounded-xl border-2 border-dashed p-16 text-center transition-all',
          dragging
            ? 'border-zinc-500 bg-zinc-800/30'
            : 'border-zinc-800 bg-zinc-900/30 hover:border-zinc-700',
        )}
        onDragOver={(e) => {
          e.preventDefault();
          setDragging(true);
        }}
        onDragLeave={() => setDragging(false)}
        onDrop={(e) => {
          e.preventDefault();
          setDragging(false);

          const file = e.dataTransfer.files?.[0];
          if (file) void handleUpload(file);
        }}
      >
        <div className="flex justify-center mb-4">
          <div className="p-4 rounded-full bg-zinc-800">
            <FileUp className="h-6 w-6 text-zinc-500" />
          </div>
        </div>

        <p className="font-medium text-zinc-300 mb-1">Drag & drop files here</p>
        <p className="text-sm text-zinc-600 mb-5">PDF, DOCX, and Markdown supported</p>

        <Button variant="outline" size="sm" onClick={() => fileInputRef.current?.click()}>
          Browse files
        </Button>
      </div>

      {/* Document list */}
      <div className="mt-8">
        <h2 className="text-sm font-medium text-zinc-400 mb-3">Documents</h2>

        {loading ? (
          <div className="text-sm text-zinc-600">Loading…</div>
        ) : documents.length === 0 ? (
          <div className="rounded-xl border border-zinc-800 bg-zinc-900/30 py-14 text-center">
            <FileText className="h-6 w-6 mx-auto mb-2 text-zinc-700" />
            <p className="text-sm text-zinc-600">No documents yet</p>
          </div>
        ) : (
          <div className="space-y-2">
            {documents.map((doc) => (
              <div
                key={doc.id}
                onClick={() => setSelectedDoc(doc)}
                className="flex items-center justify-between gap-4 rounded-lg border border-zinc-800 bg-zinc-900/30 p-3 cursor-pointer hover:border-zinc-700 transition"
              >
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-2 flex-wrap">
                    <p className="text-sm text-zinc-200 truncate">{doc.original_filename}</p>
                    <StatusBadge status={doc.status} />
                    {doc.is_stale && (
                      <Badge variant="secondary" className="gap-1 text-amber-400 border border-amber-900 bg-amber-950">
                        Needs reindex
                      </Badge>
                    )}
                  </div>

                  <p className="text-xs text-zinc-600 mt-0.5">
                    {doc.mime_type} · {formatFileSize(doc.file_size)}
                    {doc.status === 'indexed' && doc.chunk_count != null && (
                      <> · {doc.chunk_count} chunks</>
                    )}
                  </p>

                  {doc.status === 'failed' && doc.error_message && (
                    <p className="text-xs text-red-400 mt-1">{doc.error_message}</p>
                  )}
                </div>

                <div className="flex items-center gap-1 shrink-0">
                  {(doc.status === 'failed' || doc.is_stale) && (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={(e) => {
                        e.stopPropagation();
                        void handleReindex(doc);
                      }}
                      disabled={reindexingIds.has(doc.id) || doc.status === 'indexing'}
                    >
                      <RefreshCw
                        className={cn('h-3.5 w-3.5', reindexingIds.has(doc.id) && 'animate-spin')}
                      />
                      Reindex
                    </Button>
                  )}

                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      setPendingDelete(doc);
                    }}
                    className="p-2 rounded-md hover:bg-zinc-800 transition"
                    title="Delete document"
                  >
                    <Trash2 className="h-4 w-4 text-zinc-500 hover:text-red-400" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Detail dialog */}
      <Dialog open={!!selectedDoc} onOpenChange={(open) => !open && setSelectedDoc(null)}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle className="truncate">{selectedDoc?.original_filename}</DialogTitle>
          </DialogHeader>
          {selectedDoc && (
            <div className="space-y-3 text-sm">
              <div className="flex items-center gap-2">
                <StatusBadge status={selectedDoc.status} />
                {selectedDoc.is_stale && (
                  <Badge variant="secondary" className="gap-1 text-amber-400 border border-amber-900 bg-amber-950">
                    Needs reindex
                  </Badge>
                )}
              </div>

              <dl className="grid grid-cols-[120px_1fr] gap-x-3 gap-y-1.5 text-xs">
                <dt className="text-zinc-500">ID</dt>
                <dd className="text-zinc-300 font-mono truncate" title={selectedDoc.id}>{selectedDoc.id}</dd>

                <dt className="text-zinc-500">Stored as</dt>
                <dd className="text-zinc-300 font-mono truncate" title={selectedDoc.stored_filename}>{selectedDoc.stored_filename}</dd>

                <dt className="text-zinc-500">File path</dt>
                <dd className="text-zinc-300 font-mono truncate" title={selectedDoc.file_path}>{selectedDoc.file_path}</dd>

                <dt className="text-zinc-500">File size</dt>
                <dd className="text-zinc-300">{formatFileSize(selectedDoc.file_size)}</dd>

                <dt className="text-zinc-500">MIME type</dt>
                <dd className="text-zinc-300">{selectedDoc.mime_type}</dd>

                <dt className="text-zinc-500">Chunks</dt>
                <dd className="text-zinc-300">{selectedDoc.chunk_count ?? '—'}</dd>

                <dt className="text-zinc-500">Chunk size</dt>
                <dd className="text-zinc-300">{selectedDoc.chunk_size ?? '—'}</dd>

                <dt className="text-zinc-500">Chunk overlap</dt>
                <dd className="text-zinc-300">{selectedDoc.chunk_overlap ?? '—'}</dd>

                <dt className="text-zinc-500">Created</dt>
                <dd className="text-zinc-300">{new Date(selectedDoc.created_at).toLocaleString()}</dd>

                <dt className="text-zinc-500">Updated</dt>
                <dd className="text-zinc-300">{new Date(selectedDoc.updated_at).toLocaleString()}</dd>
              </dl>

              {selectedDoc.status === 'failed' && selectedDoc.error_message && (
                <p className="text-xs text-red-400">{selectedDoc.error_message}</p>
              )}
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Confirm modal */}
      {pendingDelete && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center">
          <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-5 w-[360px]">
            <p className="text-zinc-200 text-sm mb-4">
              Delete <span className="font-medium">{pendingDelete.original_filename}</span>?
            </p>

            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setPendingDelete(null)}>
                Cancel
              </Button>

              <Button
                onClick={() => void handleDelete(pendingDelete)}
                className="bg-red-600 hover:bg-red-700 text-white"
              >
                Delete
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
