"use client";
import { FormEvent, useState } from "react";
export default function AdminReviewHideForm({
  reviewId,
  onUpdated,
  onError,
}: {
  reviewId: number;
  onUpdated: (review: unknown) => void;
  onError: (message: string) => void;
}) {
  const [reason, setReason] = useState("");
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !window.confirm("Hide this review from public aggregates?"))
      return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(`/api/bff/admin/reviews/${reviewId}/hide`, {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ reason: reason.trim() }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to hide reviews."
            : data?.message || "Unable to hide review.",
        );
      onUpdated(data);
    } catch (reasonValue) {
      onError(
        reasonValue instanceof Error
          ? reasonValue.message
          : "Unable to hide review.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Hide reason
        <textarea
          maxLength={500}
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          disabled={busy}
        />
      </label>
      <button className="primary-button" disabled={busy}>
        {busy ? "Hiding..." : "Hide review"}
      </button>
    </form>
  );
}
