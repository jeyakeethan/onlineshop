"use client";
import { FormEvent, useState } from "react";
export default function AdminReturnResolveForm({
  returnId,
  onUpdated,
  onError,
}: {
  returnId: number;
  onUpdated: (request: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    refundDecision: "APPROVE_REFUND",
    refundAmount: "",
    reason: "",
  });
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (
      busy ||
      !form.reason.trim() ||
      !window.confirm(
        "Resolve this return? This records the decision but does not execute a gateway refund.",
      )
    )
      return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/returns/${returnId}/resolve`,
        {
          method: "POST",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            refundDecision: form.refundDecision,
            refundAmount: Number(form.refundAmount || 0),
            reason: form.reason.trim(),
          }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to resolve returns."
            : data?.message || "Unable to resolve return.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to resolve return.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Refund decision
        <select
          value={form.refundDecision}
          onChange={(e) => setForm({ ...form, refundDecision: e.target.value })}
          disabled={busy}
        >
          <option value="APPROVE_REFUND">Approve refund</option>
          <option value="DENY_REFUND">Deny refund</option>
        </select>
      </label>
      <label>
        Refund amount
        <input
          min="0"
          step="0.01"
          type="number"
          value={form.refundAmount}
          onChange={(e) => setForm({ ...form, refundAmount: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Resolution reason
        <textarea
          required
          value={form.reason}
          onChange={(e) => setForm({ ...form, reason: e.target.value })}
          disabled={busy}
        />
      </label>
      <button className="primary-button" disabled={busy || !form.reason.trim()}>
        {busy ? "Resolving..." : "Resolve return"}
      </button>
    </form>
  );
}
