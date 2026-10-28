"use client";
import { useState } from "react";
export default function AdminBannerArchive({
  bannerId,
  onArchived,
  onError,
}: {
  bannerId: number;
  onArchived: () => void;
  onError: (message: string) => void;
}) {
  const [busy, setBusy] = useState(false);
  async function archive() {
    if (busy || !window.confirm("Archive this banner?")) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(`/api/bff/admin/banners/${bannerId}`, {
        method: "DELETE",
      });
      if (!response.ok) {
        const data = await response.json().catch(() => ({}));
        throw new Error(
          response.status === 403
            ? "You do not have permission to archive banners."
            : data?.message || "Unable to archive banner.",
        );
      }
      onArchived();
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to archive banner.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <button
      type="button"
      className="back-link"
      onClick={archive}
      disabled={busy}
    >
      {busy ? "Archiving..." : "Archive banner"}
    </button>
  );
}
