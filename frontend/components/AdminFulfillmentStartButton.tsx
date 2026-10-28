"use client";
import { useState } from "react";
export default function AdminFulfillmentStartButton({
  orderId,
  onUpdated,
  onError,
}: {
  orderId: number;
  onUpdated: (order: unknown) => void;
  onError: (message: string) => void;
}) {
  const [busy, setBusy] = useState(false);
  async function start() {
    if (busy || !window.confirm("Start fulfillment for this order?")) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/orders/${orderId}/fulfillment/start`,
        {
          method: "POST",
          headers: { "Idempotency-Key": `fulfillment-start-${orderId}` },
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to start fulfillment."
            : data?.message || "Order is not eligible for fulfillment.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to start fulfillment.",
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
      onClick={start}
    >
      {busy ? "Starting..." : "Start fulfillment"}
    </button>
  );
}
