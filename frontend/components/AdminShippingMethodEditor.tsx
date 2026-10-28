"use client";
import { FormEvent, useState } from "react";
export default function AdminShippingMethodEditor({
  method,
  onUpdated,
  onError,
}: {
  method: {
    methodId: number;
    fee?: number;
    freeShippingThreshold?: number;
    estimate?: string;
    active?: boolean;
  };
  onUpdated: (method: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    fee: String(method.fee ?? 0),
    freeShippingThreshold: String(method.freeShippingThreshold ?? 0),
    estimate: method.estimate || "",
    active: Boolean(method.active),
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (
      saving ||
      Number(form.fee) < 0 ||
      Number(form.freeShippingThreshold) < 0
    )
      return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/shipping-methods/${method.methodId}`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            fee: Number(form.fee),
            freeShippingThreshold: Number(form.freeShippingThreshold),
            estimate: form.estimate.trim(),
            active: form.active,
          }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update shipping methods."
            : data?.message || "Unable to update shipping method.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to update shipping method.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Fee
        <input
          min="0"
          step="0.01"
          type="number"
          value={form.fee}
          onChange={(e) => setForm({ ...form, fee: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Free-shipping threshold
        <input
          min="0"
          step="0.01"
          type="number"
          value={form.freeShippingThreshold}
          onChange={(e) =>
            setForm({ ...form, freeShippingThreshold: e.target.value })
          }
          disabled={saving}
        />
      </label>
      <label>
        Estimate
        <input
          value={form.estimate}
          onChange={(e) => setForm({ ...form, estimate: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        <input
          type="checkbox"
          checked={form.active}
          onChange={(e) => setForm({ ...form, active: e.target.checked })}
          disabled={saving}
        />{" "}
        Active
      </label>
      <button className="primary-button" disabled={saving}>
        {saving ? "Saving..." : "Save method"}
      </button>
    </form>
  );
}
