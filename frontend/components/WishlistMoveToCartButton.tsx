"use client";
import { useState } from "react";
export default function WishlistMoveToCartButton({
  variantId,
  onMoved,
}: {
  variantId: number;
  onMoved: () => void;
}) {
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  async function move() {
    if (busy) return;
    setBusy(true);
    setMessage(null);
    try {
      const response = await fetch(
        `/api/bff/wishlist/items/${variantId}/move-to-cart`,
        {
          method: "POST",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({ quantity: 1 }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to move items to your cart."
            : data?.message || "Item is no longer available.",
        );
      onMoved();
    } catch (reason) {
      setMessage(
        reason instanceof Error
          ? reason.message
          : "Unable to move item to cart.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <span>
      <button
        type="button"
        className="primary-button"
        onClick={move}
        disabled={busy}
      >
        {busy ? "Moving..." : "Move to cart"}
      </button>
      {message && (
        <small role="alert" className="error-message">
          {message}
        </small>
      )}
    </span>
  );
}
