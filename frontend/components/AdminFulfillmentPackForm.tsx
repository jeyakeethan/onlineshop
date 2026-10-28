"use client";
import { FormEvent, useState } from "react";
export default function AdminFulfillmentPackForm({
  orderId,
  quantities,
  onUpdated,
  onError,
}: {
  orderId: number;
  quantities: Record<number, number>;
  onUpdated: (order: unknown) => void;
  onError: (message: string) => void;
}) {
  const [values, setValues] = useState<Record<number, string>>(
    Object.fromEntries(
      Object.entries(quantities).map(([id, quantity]) => [
        id,
        String(quantity),
      ]),
    ),
  );
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !window.confirm("Mark the full order as packed?")) return;
    setBusy(true);
    onError("");
    try {
      const packedQuantities = Object.fromEntries(
        Object.entries(values).map(([id, quantity]) => [id, Number(quantity)]),
      );
      const response = await fetch(
        `/api/bff/admin/orders/${orderId}/fulfillment/pack`,
        {
          method: "POST",
          headers: {
            "content-type": "application/json",
            "Idempotency-Key": `fulfillment-pack-${orderId}`,
          },
          body: JSON.stringify({ packedQuantities }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to pack this order."
            : data?.message || "Packing requires full order quantities.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to pack fulfillment.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Pack order</h2>
      {Object.entries(values).map(([id, value]) => (
        <label key={id}>
          Inventory #{id}
          <input
            required
            min="0"
            type="number"
            value={value}
            onChange={(e) => setValues({ ...values, [id]: e.target.value })}
            disabled={busy}
          />
        </label>
      ))}
      <button className="primary-button" disabled={busy}>
        {busy ? "Packing..." : "Mark packed"}
      </button>
    </form>
  );
}
