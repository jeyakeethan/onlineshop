"use client";
import { FormEvent, useState } from "react";
export default function AdminCouponEditor({
  coupon,
  onUpdated,
  onError,
}: {
  coupon: {
    couponId: number;
    description?: string;
    value?: number;
    minSpend?: number;
    maxDiscount?: number;
    usageLimit?: number;
    active?: boolean;
  };
  onUpdated: (coupon: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    description: coupon.description || "",
    value: String(coupon.value ?? 0),
    minSpend: String(coupon.minSpend ?? 0),
    maxDiscount: String(coupon.maxDiscount ?? 0),
    usageLimit: String(coupon.usageLimit ?? 1),
    active: Boolean(coupon.active),
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (saving) return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/coupons/${coupon.couponId}`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            ...form,
            value: Number(form.value),
            minSpend: Number(form.minSpend),
            maxDiscount: Number(form.maxDiscount),
            usageLimit: Number(form.usageLimit),
          }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update coupons."
            : data?.message || "Unable to update coupon.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to update coupon.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Description
        <input
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
          disabled={saving}
        />
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
        Usage limit
        <input
          min="1"
          type="number"
          value={form.usageLimit}
          onChange={(e) => setForm({ ...form, usageLimit: e.target.value })}
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
        {saving ? "Saving..." : "Save coupon"}
      </button>
    </form>
  );
}
