"use client";
import { useState } from "react";
export default function MarkAllNotificationsReadButton({
  onUpdated,
}: {
  onUpdated: () => void;
}) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  async function markAll() {
    if (busy) return;
    setBusy(true);
    setError(null);
    try {
      const response = await fetch("/api/bff/me/notifications/mark-all-read", {
        method: "POST",
      });
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to update notifications."
            : "Unable to mark notifications read.",
        );
      onUpdated();
    } catch (reason) {
      setError(
        reason instanceof Error
          ? reason.message
          : "Unable to mark notifications read.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <span>
      <button
        type="button"
        className="back-link"
        disabled={busy}
        onClick={markAll}
      >
        {busy ? "Updating..." : "Mark all read"}
      </button>
      {error && (
        <small role="alert" className="error-message">
          {error}
        </small>
      )}
    </span>
  );
}
