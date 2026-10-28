"use client";
import { useState } from "react";
export default function AdminFulfillmentDeliverButton({
  orderId,
  onUpdated,
  onError,
}: {
  orderId: number;
  onUpdated: (order: unknown) => void;
  onError: (message: string) => void;
}) {
  const [busy, setBusy] = useState(false);
  async function deliver() {
    if (busy || !window.confirm("Mark this order as delivered?")) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/orders/${orderId}/fulfillment/deliver`,
        {
          method: "POST",
          headers: { "Idempotency-Key": `fulfillment-deliver-${orderId}` },
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to mark orders delivered."
            : data?.message || "Order is not ready for delivery completion.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to mark order delivered.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <button
      type="button"
      className="primary-button"
      disabled={busy}
      onClick={deliver}
    >
      {busy ? "Updating..." : "Mark delivered"}
    </button>
  );
}
