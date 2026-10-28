"use client";
import { FormEvent, useState } from "react";
export default function AdminReturnReceiveForm({
  returnId,
  onUpdated,
  onError,
}: {
  returnId: number;
  onUpdated: (request: unknown) => void;
  onError: (message: string) => void;
}) {
  const [notes, setNotes] = useState("");
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !window.confirm("Record receipt of this return?")) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/returns/${returnId}/receive`,
        {
          method: "POST",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({ inspectionNotes: notes.trim() }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to receive returns."
            : data?.message || "Unable to receive return.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to receive return.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Inspection notes
        <textarea
          value={notes}
          onChange={(e) => setNotes(e.target.value)}
          disabled={busy}
        />
      </label>
      <button className="primary-button" disabled={busy}>
        {busy ? "Recording..." : "Mark received"}
      </button>
    </form>
  );
}
