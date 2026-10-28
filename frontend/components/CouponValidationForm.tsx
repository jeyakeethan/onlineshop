"use client";
import { FormEvent, useState } from "react";
export default function CouponValidationForm({
  subtotal,
  onValidated,
}: {
  subtotal: number;
  onValidated?: (result: unknown) => void;
}) {
  const [code, setCode] = useState("");
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !code.trim()) return;
    setBusy(true);
    setMessage(null);
    try {
      const response = await fetch("/api/coupons/validate", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          couponCode: code.trim(),
          subtotal,
          currency: "USD",
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(data?.message || "Unable to validate coupon.");
      setMessage(
        data.valid
          ? `Coupon applied: discount ${data.discountAmount}`
          : data.message || "Coupon is not valid.",
      );
      onValidated?.(data);
    } catch (reason) {
      setMessage(
        reason instanceof Error ? reason.message : "Unable to validate coupon.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Coupon code
        <input
          value={code}
          onChange={(e) => setCode(e.target.value)}
          disabled={busy}
        />
      </label>
      <button className="primary-button" disabled={busy || !code.trim()}>
        {busy ? "Checking..." : "Apply coupon"}
      </button>
      {message && (
        <p role="status" className="muted">
          {message}
        </p>
      )}
    </form>
  );
}
