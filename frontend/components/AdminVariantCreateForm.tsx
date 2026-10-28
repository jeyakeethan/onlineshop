"use client";
import { FormEvent, useState } from "react";
type Props = {
  productId: number;
  onCreated: (variant: any) => void;
  onMessage: (message: string) => void;
  onError: (message: string) => void;
};
export default function AdminVariantCreateForm({
  productId,
  onCreated,
  onMessage,
  onError,
}: Props) {
  const [form, setForm] = useState({
    skuCode: "",
    size: "",
    color: "",
    sellingPrice: "",
    costPrice: "0",
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (saving) return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/products/${productId}/variants`,
        {
          method: "POST",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            ...form,
            sellingPrice: Number(form.sellingPrice),
            costPrice: Number(form.costPrice),
          }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to create variants."
            : data?.message || "Unable to create variant.",
        );
      onCreated(data);
      setForm({
        skuCode: "",
        size: "",
        color: "",
        sellingPrice: "",
        costPrice: "0",
      });
      onMessage("Variant created successfully.");
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to create variant.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Add variant</h2>
      <label>
        SKU code
        <input
          required
          value={form.skuCode}
          onChange={(e) => setForm({ ...form, skuCode: e.target.value })}
        />
      </label>
      <label>
        Size
        <input
          required
          value={form.size}
          onChange={(e) => setForm({ ...form, size: e.target.value })}
        />
      </label>
      <label>
        Color
        <input
          required
          value={form.color}
          onChange={(e) => setForm({ ...form, color: e.target.value })}
        />
      </label>
      <label>
        Selling price
        <input
          required
          min="0"
          step="0.01"
          type="number"
          value={form.sellingPrice}
          onChange={(e) => setForm({ ...form, sellingPrice: e.target.value })}
        />
      </label>
      <label>
        Cost price
        <input
          min="0"
          step="0.01"
          type="number"
          value={form.costPrice}
          onChange={(e) => setForm({ ...form, costPrice: e.target.value })}
        />
      </label>
      <button className="primary-button" disabled={saving}>
        {saving ? "Creating..." : "Create variant"}
      </button>
    </form>
  );
}
