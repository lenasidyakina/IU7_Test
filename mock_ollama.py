from flask import Flask, Response, request
import json
import time
from datetime import datetime, timezone

app = Flask(__name__)

def now_iso():
    return datetime.now(timezone.utc).isoformat()

@app.route("/api/generate", methods=["POST"])
def generate():
    data = request.json

    model = data.get("model", "gemma2:2b")
    prompt = data.get("prompt", "")

    # Простейшая логика генерации тегов
    if "swimming" in prompt:
        tag = "swimming"
    elif "sleeping" in prompt:
        tag = "sleeping"
    else:
        tag = "mock_tag"

    def stream():

        # ---------- Первая часть (done = false) ----------
        first_chunk = {
            "model": model,
            "created_at": now_iso(),
            "response": tag,        # ключевой контент
            "done": False
        }

        yield json.dumps(first_chunk) + "\n"
        time.sleep(0.05)

        # ---------- Финальная часть (done = true) ----------
        final_chunk = {
            "model": model,
            "created_at": now_iso(),
            "response": "",
            "done": True,
            "total_duration": 50000,
            "load_duration": 10000,
            "prompt_eval_count": len(prompt),
            "eval_count": 1
        }

        yield json.dumps(final_chunk) + "\n"

    return Response(stream(), mimetype="application/x-ndjson")


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
