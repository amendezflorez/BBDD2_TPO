import os
import tempfile
from fastapi import FastAPI, UploadFile, File, HTTPException
from faster_whisper import WhisperModel

app = FastAPI()

MODEL_SIZE = os.getenv("WHISPER_MODEL", "medium")
model = WhisperModel(MODEL_SIZE, device="cpu", compute_type="int8")


@app.post("/transcribir")
async def transcribir(file: UploadFile = File(...)):
    audio_bytes = await file.read()
    if not audio_bytes:
        raise HTTPException(status_code=400, detail="Archivo vacío")

    # faster-whisper necesita un path, no un stream
    suffix = os.path.splitext(file.filename or "audio.webm")[1] or ".webm"
    with tempfile.NamedTemporaryFile(suffix=suffix, delete=False) as tmp:
        tmp.write(audio_bytes)
        tmp_path = tmp.name

    try:
        segments, _ = model.transcribe(tmp_path, language="es")
        texto = " ".join(s.text.strip() for s in segments)
    finally:
        os.unlink(tmp_path)

    return {"texto": texto}
