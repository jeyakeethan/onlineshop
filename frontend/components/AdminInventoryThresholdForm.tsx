"use client";
import { FormEvent, useState } from "react";
export default function AdminInventoryThresholdForm({
  variantId,
  threshold,
  onUpdated,
  onMessage,
  onError,
}: {
  variantId: number;
  threshold?: number;
  onUpdated: (inventory: unknown) => void;
  onMessage: (message: string) => void;
  onError: (message: string) => void;
}) {
  const [value, setValue] = useState(String(threshold ?? 0));
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    const number = Number(value);
    if (saving || !Number.isInteger(number) || number < 0) return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/inventory/${variantId}/threshold`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({ lowStockThreshold: number }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update thresholds."
            : data?.message || "Unable to update stock threshold.",
        );
      onUpdated(data);
      onMessage("Low-stock threshold updated.");
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to update stock threshold.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Low-stock threshold
        <input
          required
          min="0"
          step="1"
          type="number"
          value={value}
          onChange={(e) => setValue(e.target.value)}
          disabled={saving}
        />
      </label>
      <button className="primary-button" disabled={saving || !value.trim()}>
        {saving ? "Saving..." : "Save threshold"}
      </button>
    </form>
  );
}
