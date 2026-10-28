"use client";
import { FormEvent, useState } from "react";
export default function AdminTicketReplyForm({
  ticketId,
  onSent,
  onError,
}: {
  ticketId: number;
  onSent: (message: unknown) => void;
  onError: (message: string) => void;
}) {
  const [message, setMessage] = useState("");
  const [internalNote, setInternalNote] = useState(false);
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !message.trim()) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/tickets/${ticketId}/messages`,
        {
          method: "POST",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({ message: message.trim(), internalNote }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to reply to tickets."
            : data?.message || "Unable to send staff message.",
        );
      onSent(data);
      setMessage("");
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to send staff message.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Staff message
        <textarea
          required
          maxLength={5000}
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          disabled={busy}
        />
      </label>
      <label>
        <input
          type="checkbox"
          checked={internalNote}
          onChange={(e) => setInternalNote(e.target.checked)}
          disabled={busy}
        />{" "}
        Internal note (not customer-visible)
      </label>
      <button className="primary-button" disabled={busy || !message.trim()}>
        {busy ? "Sending..." : "Send message"}
      </button>
    </form>
  );
}
