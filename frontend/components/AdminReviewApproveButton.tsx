"use client";
import { useState } from "react";
export default function AdminReviewApproveButton({
  reviewId,
  onUpdated,
  onError,
}: {
  reviewId: number;
  onUpdated: (review: unknown) => void;
  onError: (message: string) => void;
}) {
  const [busy, setBusy] = useState(false);
  async function approve() {
    if (busy || !window.confirm("Approve this review for publication?")) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/reviews/${reviewId}/approve`,
        { method: "POST" },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to approve reviews."
            : data?.message || "Unable to approve review.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to approve review.",
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
      onClick={approve}
    >
      {busy ? "Approving..." : "Approve review"}
    </button>
  );
}
