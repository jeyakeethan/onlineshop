"use client";
import { useState } from "react";
export default function WishlistButton({
  inventoryId,
  variantId,
  onAdded,
}: {
  inventoryId?: number;
  variantId?: number;
  onAdded?: () => void;
}) {
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  async function add() {
    const id = inventoryId || variantId;
    if (!id || saving) return;
    setSaving(true);
    setMessage(null);
    try {
      const response = await fetch("/api/bff/wishlist", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ inventoryId, variantId }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to save items."
            : data?.message || "Unable to save item.",
        );
      setMessage("Saved");
      onAdded?.();
    } catch (reason) {
      setMessage(
        reason instanceof Error ? reason.message : "Unable to save item.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <span>
      <button
        type="button"
        className="back-link"
        onClick={add}
        disabled={saving}
      >
        {saving ? "Saving..." : "♡ Save"}
      </button>
      {message && <small className="muted"> {message}</small>}
    </span>
  );
}
