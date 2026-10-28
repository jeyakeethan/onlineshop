"use client";
import { FormEvent, useState } from "react";
export default function AdminReturnApproveForm({
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
    if (busy) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/returns/${returnId}/approve`,
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
            ? "You do not have permission to approve returns."
            : data?.message || "Unable to approve return.",
        );
      onUpdated(data);
    } catch (reasonValue) {
      onError(
        reasonValue instanceof Error
          ? reasonValue.message
          : "Unable to approve return.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Approval reason
        <input
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          disabled={busy}
        />
      </label>
      <button className="primary-button" disabled={busy}>
        {busy ? "Approving..." : "Approve return"}
      </button>
    </form>
  );
}
