"use client";
import { FormEvent, useState } from "react";
export default function AdminReturnRejectForm({
  returnId,
  onUpdated,
  onError,
}: {
  returnId: number;
  onUpdated: (request: unknown) => void;
  onError: (message: string) => void;
}) {
  const [reason, setReason] = useState("");
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (
      busy ||
      !reason.trim() ||
      !window.confirm("Reject this return request?")
    )
      return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/returns/${returnId}/reject`,
        {
          method: "POST",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({ reason: reason.trim() }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to reject returns."
            : data?.message || "Unable to reject return.",
        );
      onUpdated(data);
    } catch (reasonValue) {
      onError(
        reasonValue instanceof Error
          ? reasonValue.message
          : "Unable to reject return.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Rejection reason
        <input
          required
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          disabled={busy}
        />
      </label>
      <button className="primary-button" disabled={busy || !reason.trim()}>
        {busy ? "Rejecting..." : "Reject return"}
      </button>
    </form>
  );
}
