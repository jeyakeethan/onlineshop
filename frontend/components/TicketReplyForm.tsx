"use client";
import { FormEvent, useState } from "react";
export default function TicketReplyForm({
  ticketId,
  onSent,
  onError,
}: {
  ticketId: number;
  onSent: (message: unknown) => void;
  onError: (message: string) => void;
}) {
  const [message, setMessage] = useState("");
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !message.trim()) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(`/api/bff/tickets/${ticketId}/messages`, {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ message: message.trim() }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to reply."
            : response.status === 403
              ? "You do not have access to this ticket."
              : data?.message || "Unable to send reply.",
        );
      onSent(data);
      setMessage("");
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to send reply.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Reply
        <textarea
          required
          maxLength={5000}
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          disabled={busy}
        />
      </label>
      <button className="primary-button" disabled={busy || !message.trim()}>
        {busy ? "Sending..." : "Send reply"}
      </button>
    </form>
  );
}
