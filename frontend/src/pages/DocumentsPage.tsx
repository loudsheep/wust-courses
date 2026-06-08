import { api } from '@/lib/api';
import { useEffect, useRef, useState } from 'react';
import { FileUp, FileText, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

type Document = {
  id: string;
  original_filename: string;
  file_size: number;
  mime_type: string;
};

async function uploadDocument(file: File) {
  const data = await api.documents.upload(file);
  console.log(data);
  return data;
}

async function fetchDocuments() {
  return await api.documents.list();
}

export function DocumentsPage() {
  const [dragging, setDragging] = useState(false);
  const [documents, setDocuments] = useState<Document[]>([]);
  const [pendingDelete, setPendingDelete] = useState<Document | null>(null);

  const fileInputRef = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    fetchDocuments().then(setDocuments).catch(console.error);
  }, []);

  async function handleUpload(file: File) {
    try {
      const doc = await uploadDocument(file);
      setDocuments((prev) => [doc, ...prev]);
    } catch (e) {
      console.error(e);
    }
  }

  async function handleDelete(doc: Document) {
    try {
      await api.documents.delete(doc.id);
      setDocuments((prev) => prev.filter((d) => d.id !== doc.id));
    } catch (e) {
      console.error(e);
    } finally {
      setPendingDelete(null);
    }
  }

  return (
    <div className="p-8">
      <div className="flex items-start justify-between mb-8">
        <div>
          <h1 className="text-xl font-semibold text-zinc-100">Documents</h1>
          <p className="text-sm text-zinc-500 mt-1">
            Upload and manage your knowledge base documents.
          </p>
        </div>

        <Button onClick={() => fileInputRef.current?.click()}>Upload</Button>

        <input
          ref={fileInputRef}
          type="file"
          className="hidden"
          onChange={(e) => {
            const file = e.target.files?.[0];
            if (file) handleUpload(file);
          }}
        />
      </div>

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
          if (file) handleUpload(file);
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
        <h2 className="text-sm font-medium text-zinc-400 mb-3">Indexed documents</h2>

        {documents.length === 0 ? (
          <div className="rounded-xl border border-zinc-800 bg-zinc-900/30 py-14 text-center">
            <FileText className="h-6 w-6 mx-auto mb-2 text-zinc-700" />
            <p className="text-sm text-zinc-600">No documents yet</p>
          </div>
        ) : (
          <div className="space-y-2">
            {documents.map((doc) => (
              <div
                key={doc.id}
                className="flex items-center justify-between rounded-lg border border-zinc-800 bg-zinc-900/30 p-3"
              >
                <div>
                  <p className="text-sm text-zinc-200">{doc.original_filename}</p>
                  <p className="text-xs text-zinc-600">{doc.mime_type}</p>
                </div>

                <button
                  onClick={() => setPendingDelete(doc)}
                  className="p-2 rounded-md hover:bg-zinc-800 transition"
                  title="Delete document"
                >
                  <Trash2 className="h-4 w-4 text-zinc-500 hover:text-red-400" />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

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
                onClick={() => handleDelete(pendingDelete)}
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
