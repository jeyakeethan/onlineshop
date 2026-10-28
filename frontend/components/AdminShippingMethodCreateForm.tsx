"use client";
import { FormEvent, useState } from "react";
export default function AdminShippingMethodCreateForm({
  onCreated,
  onMessage,
  onError,
}: {
  onCreated: (method: unknown) => void;
  onMessage: (message: string) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    code: "",
    label: "",
    fee: "",
    freeShippingThreshold: "0",
    estimate: "",
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (
      saving ||
      !form.code.trim() ||
      !form.label.trim() ||
      Number(form.fee) < 0
    )
      return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch("/api/bff/admin/shipping-methods", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          ...form,
          code: form.code.trim().toUpperCase(),
          label: form.label.trim(),
          fee: Number(form.fee),
          freeShippingThreshold: Number(form.freeShippingThreshold),
          active: false,
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to create shipping methods."
            : data?.message || "Unable to create shipping method.",
        );
      onCreated(data);
      onMessage("Shipping method created successfully.");
      setForm({
        code: "",
        label: "",
        fee: "",
        freeShippingThreshold: "0",
        estimate: "",
      });
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to create shipping method.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Create shipping method</h2>
      <label>
        Code
        <input
          required
          value={form.code}
          onChange={(e) => setForm({ ...form, code: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Label
        <input
          required
          value={form.label}
          onChange={(e) => setForm({ ...form, label: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Fee
        <input
          required
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
      <button
        className="primary-button"
        disabled={
          saving || !form.code.trim() || !form.label.trim() || !form.fee
        }
      >
        {saving ? "Creating..." : "Create method"}
      </button>
    </form>
  );
}
