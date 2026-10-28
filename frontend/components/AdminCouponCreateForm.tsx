"use client";
import { FormEvent, useState } from "react";
export default function AdminCouponCreateForm({
  onCreated,
  onMessage,
  onError,
}: {
  onCreated: (coupon: unknown) => void;
  onMessage: (message: string) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    code: "",
    description: "",
    type: "PERCENTAGE",
    value: "",
    minSpend: "0",
    maxDiscount: "0",
    usageLimit: "1",
    startAt: "",
    endAt: "",
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    const value = Number(form.value);
    if (saving || !form.code.trim() || !Number.isFinite(value) || value <= 0)
      return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch("/api/bff/admin/coupons", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          ...form,
          code: form.code.trim().toUpperCase(),
          value,
          minSpend: Number(form.minSpend),
          maxDiscount: Number(form.maxDiscount),
          usageLimit: Number(form.usageLimit),
          active: false,
          stackable: false,
          startAt: form.startAt || null,
          endAt: form.endAt || null,
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to create coupons."
            : data?.message || "Unable to create coupon.",
        );
      onCreated(data);
      onMessage("Coupon created successfully.");
      setForm({
        code: "",
        description: "",
        type: "PERCENTAGE",
        value: "",
        minSpend: "0",
        maxDiscount: "0",
        usageLimit: "1",
        startAt: "",
        endAt: "",
      });
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to create coupon.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Create coupon</h2>
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
        Description
        <input
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Type
        <select
          value={form.type}
          onChange={(e) => setForm({ ...form, type: e.target.value })}
          disabled={saving}
        >
          <option value="PERCENTAGE">Percentage</option>
          <option value="FIXED">Fixed amount</option>
        </select>
      </label>
      <label>
        Value
        <input
          required
          min="0"
          step="0.01"
          type="number"
          value={form.value}
          onChange={(e) => setForm({ ...form, value: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Minimum spend
        <input
          min="0"
          step="0.01"
          type="number"
          value={form.minSpend}
          onChange={(e) => setForm({ ...form, minSpend: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Maximum discount
        <input
          min="0"
          step="0.01"
          type="number"
          value={form.maxDiscount}
          onChange={(e) => setForm({ ...form, maxDiscount: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Usage limit
        <input
          required
          min="1"
          step="1"
          type="number"
          value={form.usageLimit}
          onChange={(e) => setForm({ ...form, usageLimit: e.target.value })}
          disabled={saving}
        />
      </label>
      <button
        className="primary-button"
        disabled={saving || !form.code.trim() || !form.value}
      >
        {saving ? "Creating..." : "Create coupon"}
      </button>
    </form>
  );
}
