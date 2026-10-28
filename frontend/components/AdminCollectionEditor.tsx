"use client";
import { FormEvent, useState } from "react";
export default function AdminCollectionEditor({
  collection,
  onUpdated,
  onError,
}: {
  collection: {
    collectionId: number;
    active?: boolean;
    displayOrder?: number;
    productIds?: number[];
  };
  onUpdated: (collection: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    active: Boolean(collection.active),
    displayOrder: String(collection.displayOrder ?? 0),
    productIds: (collection.productIds || []).join(", "),
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (saving) return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/collections/${collection.collectionId}`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            active: form.active,
            displayOrder: Number(form.displayOrder),
            productIds: form.productIds
              .split(",")
              .map((v) => Number(v.trim()))
              .filter((v) => Number.isInteger(v) && v > 0),
          }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update collections."
            : data?.message || "Unable to update collection.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to update collection.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        <input
          type="checkbox"
          checked={form.active}
          onChange={(e) => setForm({ ...form, active: e.target.checked })}
          disabled={saving}
        />{" "}
        Active
      </label>
      <label>
        Display order
        <input
          type="number"
          min="0"
          value={form.displayOrder}
          onChange={(e) => setForm({ ...form, displayOrder: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Product IDs
        <input
          value={form.productIds}
          onChange={(e) => setForm({ ...form, productIds: e.target.value })}
          disabled={saving}
        />
      </label>
      <button className="primary-button" disabled={saving}>
        {saving ? "Saving..." : "Save collection"}
      </button>
    </form>
  );
}
