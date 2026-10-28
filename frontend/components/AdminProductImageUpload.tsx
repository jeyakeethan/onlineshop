"use client";
import { FormEvent, useState } from "react";
export default function AdminProductImageUpload({
  productId,
  onUploaded,
  onError,
}: {
  productId: number;
  onUploaded: (image: unknown) => void;
  onError: (message: string) => void;
}) {
  const [file, setFile] = useState<File | null>(null);
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (!file || saving) return;
    if (!file.type.startsWith("image/") || file.size > 5 * 1024 * 1024) {
      onError("Choose an image file up to 5 MB.");
      return;
    }
    setSaving(true);
    onError("");
    try {
      const body = new FormData();
      body.append("file", file);
      const response = await fetch(
        `/api/bff/admin/products/${productId}/images`,
        { method: "POST", body },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to upload images."
            : data?.message || "Unable to upload product image.",
        );
      onUploaded(data);
      setFile(null);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to upload product image.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Product image</h2>
      <label htmlFor="product-image">Image file</label>
      <input
        id="product-image"
        type="file"
        accept="image/*"
        onChange={(event) => setFile(event.target.files?.[0] || null)}
        disabled={saving}
      />
      <button className="primary-button" disabled={!file || saving}>
        {saving ? "Uploading..." : "Upload image"}
      </button>
    </form>
  );
}
