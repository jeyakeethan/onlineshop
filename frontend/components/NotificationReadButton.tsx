"use client";
import { useState } from "react";
export default function NotificationReadButton({
  notificationId,
  onRead,
}: {
  notificationId: number;
  onRead: () => void;
}) {
  const [busy, setBusy] = useState(false);
  async function markRead() {
    if (busy) return;
    setBusy(true);
    try {
      const response = await fetch(
        `/api/bff/me/notifications/${notificationId}/read`,
        { method: "PATCH" },
      );
      if (!response.ok) throw new Error();
      onRead();
    } finally {
      setBusy(false);
    }
  }
  return (
    <button
      type="button"
      className="back-link"
      disabled={busy}
      onClick={markRead}
    >
      {busy ? "Updating..." : "Mark read"}
    </button>
  );
}
