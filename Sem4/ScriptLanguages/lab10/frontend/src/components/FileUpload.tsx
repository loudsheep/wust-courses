import React, { useState } from 'react';

export const FileUpload: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error', text: string } | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setFile(e.target.files[0]);
      setMessage(null);
    }
  };

  const handleUpload = async () => {
    if (!file) return;

    setUploading(true);
    setMessage(null);

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch('http://localhost:8000/upload', {
        method: 'POST',
        body: formData,
      });

      const data = await response.json();

      if (response.ok) {
        setMessage({ type: 'success', text: 'Plik wgrany pomyślnie!' });
        setFile(null);
        // Optional: clear file input
        const input = document.getElementById('gtfs-upload') as HTMLInputElement;
        if (input) input.value = '';
      } else {
        setMessage({ type: 'error', text: data.detail || 'Błąd podczas wgrywania pliku.' });
      }
    } catch (error) {
      setMessage({ type: 'error', text: 'Błąd połączenia z serwerem.' });
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="flex flex-col gap-3 mt-4">
      <h2 className="text-sm font-medium text-white/90">Wgraj dane GTFS (.zip)</h2>
      <div className="flex flex-col gap-2">
        <input
          id="gtfs-upload"
          type="file"
          accept=".zip"
          onChange={handleFileChange}
          className="block w-full text-sm text-gray-400
            file:mr-4 file:py-2 file:px-4
            file:rounded-md file:border-0
            file:text-sm file:font-semibold
            file:bg-blue-600 file:text-white
            hover:file:bg-blue-700
            cursor-pointer"
        />
        <button
          onClick={handleUpload}
          disabled={!file || uploading}
          className={`px-4 py-2 rounded-md text-sm font-semibold text-white transition-colors
            ${!file || uploading ? 'bg-gray-600 cursor-not-allowed' : 'bg-green-600 hover:bg-green-700'}`}
        >
          {uploading ? 'Wgrywanie...' : 'Wgraj plik'}
        </button>
      </div>
      {message && (
        <p className={`text-xs font-medium ${message.type === 'success' ? 'text-green-400' : 'text-red-400'}`}>
          {message.text}
        </p>
      )}
    </div>
  );
};
