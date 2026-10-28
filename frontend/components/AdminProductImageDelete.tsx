"use client";
import { useState } from "react";
export default function AdminProductImageDelete({
  productId,
  imageId,
  onDeleted,
  onError,
}: {
  productId: number;
  imageId: number;
  onDeleted: () => void;
  onError: (message: string) => void;
}) {
  const [deleting, setDeleting] = useState(false);
  async function remove() {
    if (
      deleting ||
      !window.confirm("Delete this product image? This cannot be undone.")
    )
      return;
    setDeleting(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/products/${productId}/images/${imageId}`,
        { method: "DELETE" },
      );
      if (!response.ok) {
        const data = await response.json().catch(() => ({}));
        throw new Error(
          response.status === 403
            ? "You do not have permission to delete images."
            : data?.message || "Unable to delete product image.",
        );
      }
      onDeleted();
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to delete product image.",
      );
    } finally {
      setDeleting(false);
    }
  }
  return (
    <button
      type="button"
      className="back-link"
      onClick={remove}
      disabled={deleting}
    >
      {deleting ? "Deleting..." : "Delete image"}
    </button>
  );
}
