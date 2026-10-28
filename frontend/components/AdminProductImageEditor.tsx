"use client";
import { FormEvent, useState } from "react";
export default function AdminProductImageEditor({
  productId,
  image,
  onUpdated,
  onError,
}: {
  productId: number;
  image: {
    imageId: number;
    altText?: string;
    displayOrder?: number;
    primaryImage?: boolean;
  };
  onUpdated: (image: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    altText: image.altText || "",
    displayOrder: String(image.displayOrder ?? 0),
    primaryImage: Boolean(image.primaryImage),
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (saving) return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/products/${productId}/images/${image.imageId}`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            altText: form.altText.trim(),
            displayOrder: Number(form.displayOrder),
            primaryImage: form.primaryImage,
          }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update images."
            : data?.message || "Unable to update product image.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to update product image.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Alt text
        <input
          value={form.altText}
          onChange={(e) => setForm({ ...form, altText: e.target.value })}
          disabled={saving}
        />
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
        <input
          type="checkbox"
          checked={form.primaryImage}
          onChange={(e) => setForm({ ...form, primaryImage: e.target.checked })}
          disabled={saving}
        />{" "}
        Primary image
      </label>
      <button className="primary-button" disabled={saving}>
        {saving ? "Saving..." : "Save image details"}
      </button>
    </form>
  );
}
