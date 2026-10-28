"use client";
import { FormEvent, useState } from "react";
export default function AdminInventoryAdjustmentForm({
  variantId,
  onUpdated,
  onMessage,
  onError,
}: {
  variantId: number;
  onUpdated: (inventory: unknown) => void;
  onMessage: (message: string) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    quantityDelta: "",
    reason: "",
    reference: "",
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (saving || !form.quantityDelta || !form.reason.trim()) return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/inventory/${variantId}/adjustments`,
        {
          method: "POST",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            quantityDelta: Number(form.quantityDelta),
            reason: form.reason.trim(),
            reference: form.reference.trim() || null,
          }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to adjust inventory."
            : data?.message || "Unable to adjust inventory.",
        );
      onUpdated(data);
      setForm({ quantityDelta: "", reason: "", reference: "" });
      onMessage("Inventory adjusted successfully.");
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to adjust inventory.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Adjust stock</h2>
      <label>
        Quantity change
        <input
          required
          type="number"
          value={form.quantityDelta}
          onChange={(e) => setForm({ ...form, quantityDelta: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Reason
        <input
          required
          value={form.reason}
          onChange={(e) => setForm({ ...form, reason: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Reference
        <input
          value={form.reference}
          onChange={(e) => setForm({ ...form, reference: e.target.value })}
          disabled={saving}
        />
      </label>
      <button
        className="primary-button"
        disabled={saving || !form.quantityDelta || !form.reason.trim()}
      >
        {saving ? "Adjusting..." : "Adjust inventory"}
      </button>
    </form>
  );
}
