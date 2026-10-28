"use client";
import { useState } from "react";
export default function AdminInventoryReportButton() {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  async function download() {
    if (busy) return;
    setBusy(true);
    setError(null);
    try {
      const response = await fetch("/api/bff/admin/reports/inventory", {
        cache: "no-store",
      });
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to download reports."
            : "Unable to download inventory report.",
        );
      const blob = await response.blob();
      const url = URL.createObjectURL(blob);
      const anchor = document.createElement("a");
      anchor.href = url;
      anchor.download = "inventory.csv";
      anchor.click();
      URL.revokeObjectURL(url);
    } catch (reason) {
      setError(
        reason instanceof Error
          ? reason.message
          : "Unable to download inventory report.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <span>
      <button
        type="button"
        className="primary-button"
        onClick={download}
        disabled={busy}
      >
        {busy ? "Downloading..." : "Download inventory CSV"}
      </button>
      {error && (
        <small role="alert" className="error-message">
          {error}
        </small>
      )}
    </span>
  );
}
