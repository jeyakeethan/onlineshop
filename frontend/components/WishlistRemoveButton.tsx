"use client";
import { useState } from "react";
export default function WishlistRemoveButton({
  variantId,
  onRemoved,
}: {
  variantId: number;
  onRemoved: () => void;
}) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  async function remove() {
    if (busy || !window.confirm("Remove this item from your wishlist?")) return;
    setBusy(true);
    setError(null);
    try {
      const response = await fetch(`/api/bff/wishlist/items/${variantId}`, {
        method: "DELETE",
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to update your wishlist."
            : data?.message || "Unable to remove wishlist item.",
        );
      onRemoved();
    } catch (reason) {
      setError(
        reason instanceof Error
          ? reason.message
          : "Unable to remove wishlist item.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <span>
      <button
        type="button"
        className="back-link"
        disabled={busy}
        onClick={remove}
      >
        {busy ? "Removing..." : "Remove"}
      </button>
      {error && (
        <small role="alert" className="error-message">
          {error}
        </small>
      )}
    </span>
  );
}
