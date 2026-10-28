"use client";
import { FormEvent, useState } from "react";
export default function AdminFulfillmentShipForm({
  orderId,
  onUpdated,
  onError,
}: {
  orderId: number;
  onUpdated: (order: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({ carrier: "", trackingNumber: "" });
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (
      busy ||
      !form.carrier.trim() ||
      !form.trackingNumber.trim() ||
      !window.confirm("Mark this order as shipped?")
    )
      return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/orders/${orderId}/fulfillment/ship`,
        {
          method: "POST",
          headers: {
            "content-type": "application/json",
            "Idempotency-Key": `fulfillment-ship-${orderId}`,
          },
          body: JSON.stringify({
            carrier: form.carrier.trim(),
            trackingNumber: form.trackingNumber.trim(),
          }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to ship this order."
            : data?.message || "Order is not ready to ship.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to ship fulfillment.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Ship order</h2>
      <label>
        Carrier
        <input
          required
          value={form.carrier}
          onChange={(e) => setForm({ ...form, carrier: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Tracking number
        <input
          required
          value={form.trackingNumber}
          onChange={(e) => setForm({ ...form, trackingNumber: e.target.value })}
          disabled={busy}
        />
      </label>
      <button
        className="primary-button"
        disabled={busy || !form.carrier.trim() || !form.trackingNumber.trim()}
      >
        {busy ? "Shipping..." : "Mark shipped"}
      </button>
    </form>
  );
}
