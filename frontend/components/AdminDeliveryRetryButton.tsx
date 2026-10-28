"use client";
import { useState } from "react";
export default function AdminDeliveryRetryButton({
  deliveryId,
  onRetried,
  onError,
}: {
  deliveryId: number;
  onRetried: (delivery: unknown) => void;
  onError: (message: string) => void;
}) {
  const [busy, setBusy] = useState(false);
  async function retry() {
    if (busy || !window.confirm("Retry this notification delivery?")) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/notification-deliveries/${deliveryId}/retry`,
        { method: "POST" },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to retry deliveries."
            : data?.message ||
                "Retry limit reached or delivery is not eligible.",
        );
      onRetried(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to retry notification delivery.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <button type="button" className="back-link" disabled={busy} onClick={retry}>
      {busy ? "Retrying..." : "Retry delivery"}
    </button>
  );
}
