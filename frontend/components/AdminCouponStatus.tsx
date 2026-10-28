"use client";
import { useState } from "react";
export default function AdminCouponStatus({
  couponId,
  active,
  onUpdated,
  onError,
}: {
  couponId: number;
  active?: boolean;
  onUpdated: (coupon: unknown) => void;
  onError: (message: string) => void;
}) {
  const [busy, setBusy] = useState(false);
  async function change(status: string) {
    if (busy) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/coupons/${couponId}/status`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({ status }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update coupon status."
            : data?.message || "Unable to update coupon status.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to update coupon status.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <label className="muted">
      Status
      <select
        value={active ? "ACTIVE" : "INACTIVE"}
        onChange={(e) => change(e.target.value)}
        disabled={busy}
      >
        <option value="ACTIVE">Active</option>
        <option value="INACTIVE">Inactive</option>
      </select>
    </label>
  );
}
