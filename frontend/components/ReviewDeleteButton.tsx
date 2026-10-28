"use client";
import { useState } from "react";
export default function ReviewDeleteButton({
  reviewId,
  onDeleted,
  onError,
}: {
  reviewId: number;
  onDeleted: () => void;
  onError: (message: string) => void;
}) {
  const [busy, setBusy] = useState(false);
  async function remove() {
    if (busy || !window.confirm("Delete your review?")) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(`/api/bff/reviews/${reviewId}`, {
        method: "DELETE",
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to delete this review."
            : response.status === 403
              ? "You can only delete your own review."
              : data?.message || "Unable to delete review.",
        );
      onDeleted();
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to delete review.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <button
      type="button"
      className="back-link"
      disabled={busy}
      onClick={remove}
    >
      {busy ? "Deleting..." : "Delete review"}
    </button>
  );
}
