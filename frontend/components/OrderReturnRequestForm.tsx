"use client";
import { FormEvent, useState } from "react";
export default function OrderReturnRequestForm({
  orderId,
  orderItemIds,
  onCreated,
  onError,
}: {
  orderId: number;
  orderItemIds: number[];
  onCreated: (request: unknown) => void;
  onError: (message: string) => void;
}) {
  const [reason, setReason] = useState("");
  const [quantity, setQuantity] = useState<Record<number, string>>(
    Object.fromEntries(orderItemIds.map((id) => [id, "1"])),
  );
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !reason.trim()) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(`/api/orders/${orderId}/returns`, {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          reason: reason.trim(),
          items: orderItemIds
            .map((orderItemId) => ({
              orderItemId,
              quantity: Number(quantity[orderItemId]),
              reason: reason.trim(),
            }))
            .filter(
              (item) => Number.isInteger(item.quantity) && item.quantity > 0,
            ),
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to request a return."
            : data?.message || "Unable to request return.",
        );
      onCreated(data);
      setReason("");
    } catch (reasonValue) {
      onError(
        reasonValue instanceof Error
          ? reasonValue.message
          : "Unable to request return.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Request a return</h2>
      <label>
        Reason
        <textarea
          required
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          disabled={busy}
        />
      </label>
      {orderItemIds.map((id) => (
        <label key={id}>
          Order item #{id} quantity
          <input
            required
            min="1"
            type="number"
            value={quantity[id]}
            onChange={(e) => setQuantity({ ...quantity, [id]: e.target.value })}
            disabled={busy}
          />
        </label>
      ))}
      <button className="primary-button" disabled={busy || !reason.trim()}>
        {busy ? "Submitting..." : "Submit return request"}
      </button>
    </form>
  );
}
