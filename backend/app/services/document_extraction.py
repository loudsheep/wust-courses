from pathlib import Path

_SIGNATURES: dict[bytes, tuple[str, str]] = {
    b"%PDF": (".pdf", "application/pdf"),
    b"PK\x03\x04": (
        ".docx",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ),
}

_TEXT_MIME_TYPES = {".txt": "text/plain", ".md": "text/markdown"}


def detect_mime_type(file_path: str | Path) -> str | None:
    path = Path(file_path)
    extension = path.suffix.lower()

    with open(path, "rb") as f:
        header = f.read(8)

    for signature, (expected_extension, mime_type) in _SIGNATURES.items():
        if header.startswith(signature):
            return mime_type if extension == expected_extension else None

    if extension in _TEXT_MIME_TYPES:
        try:
            with open(path, encoding="utf-8") as f:
                f.read(4096)
        except UnicodeDecodeError:
            return None
        return _TEXT_MIME_TYPES[extension]

    return None


def _extract_pdf(file_path: str) -> str:
    from pypdf import PdfReader

    reader = PdfReader(file_path)
    return "\n\n".join(page.extract_text() or "" for page in reader.pages)


def _extract_docx(file_path: str) -> str:
    from docx import Document as DocxDocument

    doc = DocxDocument(file_path)
    return "\n".join(paragraph.text for paragraph in doc.paragraphs)


def _extract_plain_text(file_path: str) -> str:
    with open(file_path, encoding="utf-8", errors="replace") as f:
        return f.read()


_EXTRACTORS = {
    ".pdf": _extract_pdf,
    ".docx": _extract_docx,
    ".txt": _extract_plain_text,
    ".md": _extract_plain_text,
}


def extract_text(file_path: str, mime_type: str) -> str:
    extension = Path(file_path).suffix.lower()
    extractor = _EXTRACTORS.get(extension)
    if extractor is None:
        raise ValueError(f"Unsupported file type: {extension}")
    return extractor(file_path)
