"use client";
import { FormEvent, useState } from "react";
export default function TicketCreateForm({
  onCreated,
  onError,
}: {
  onCreated: (ticket: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    subject: "",
    description: "",
    priority: "NORMAL",
  });
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !form.subject.trim() || form.description.trim().length < 10)
      return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch("/api/bff/tickets", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          ...form,
          subject: form.subject.trim(),
          description: form.description.trim(),
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to create a ticket."
            : data?.message || "Unable to create ticket.",
        );
      onCreated(data);
      setForm({ subject: "", description: "", priority: "NORMAL" });
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to create ticket.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Contact support</h2>
      <label>
        Subject
        <input
          required
          maxLength={160}
          value={form.subject}
          onChange={(e) => setForm({ ...form, subject: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Description
        <textarea
          required
          minLength={10}
          maxLength={5000}
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Priority
        <select
          value={form.priority}
          onChange={(e) => setForm({ ...form, priority: e.target.value })}
          disabled={busy}
        >
          <option>NORMAL</option>
          <option>LOW</option>
          <option>HIGH</option>
        </select>
      </label>
      <button
        className="primary-button"
        disabled={
          busy || !form.subject.trim() || form.description.trim().length < 10
        }
      >
        {busy ? "Submitting..." : "Create ticket"}
      </button>
    </form>
  );
}
