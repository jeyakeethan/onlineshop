"use client";
import { FormEvent, useState } from "react";
export default function AdminTicketEditor({
  ticket,
  onUpdated,
  onError,
}: {
  ticket: {
    ticketId: number;
    priority?: string;
    status?: string;
    assignedTo?: string;
  };
  onUpdated: (ticket: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    priority: ticket.priority || "NORMAL",
    status: ticket.status || "OPEN",
    assignedTo: ticket.assignedTo || "",
  });
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/tickets/${ticket.ticketId}`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            ...form,
            assignedTo: form.assignedTo.trim() || null,
          }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update tickets."
            : data?.message || "Unable to update ticket.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to update ticket.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Status
        <select
          value={form.status}
          onChange={(e) => setForm({ ...form, status: e.target.value })}
          disabled={busy}
        >
          <option>OPEN</option>
          <option>IN_PROGRESS</option>
          <option>RESOLVED</option>
          <option>CLOSED</option>
        </select>
      </label>
      <label>
        Priority
        <select
          value={form.priority}
          onChange={(e) => setForm({ ...form, priority: e.target.value })}
          disabled={busy}
        >
          <option>LOW</option>
          <option>NORMAL</option>
          <option>HIGH</option>
        </select>
      </label>
      <label>
        Assigned to
        <input
          maxLength={255}
          value={form.assignedTo}
          onChange={(e) => setForm({ ...form, assignedTo: e.target.value })}
          disabled={busy}
        />
      </label>
      <button className="primary-button" disabled={busy}>
        {busy ? "Saving..." : "Save ticket"}
      </button>
    </form>
  );
}
