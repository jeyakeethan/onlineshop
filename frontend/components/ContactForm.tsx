"use client";
import { FormEvent, useState } from "react";
export default function ContactForm() {
  const [form, setForm] = useState({
    name: "",
    email: "",
    message: "",
    website: "",
  });
  const [busy, setBusy] = useState(false);
  const [status, setStatus] = useState<string | null>(null);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy) return;
    setBusy(true);
    setStatus(null);
    try {
      const response = await fetch("/api/contact", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify(form),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(data?.message || "Unable to submit contact request.");
      setStatus("Your message has been received.");
      setForm({ name: "", email: "", message: "", website: "" });
    } catch (reason) {
      setStatus(
        reason instanceof Error
          ? reason.message
          : "Unable to submit contact request.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Name
        <input
          required
          maxLength={120}
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Email
        <input
          required
          type="email"
          maxLength={255}
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Message
        <textarea
          required
          minLength={10}
          maxLength={5000}
          value={form.message}
          onChange={(e) => setForm({ ...form, message: e.target.value })}
          disabled={busy}
        />
      </label>
      <input
        aria-hidden="true"
        tabIndex={-1}
        autoComplete="off"
        value={form.website}
        onChange={(e) => setForm({ ...form, website: e.target.value })}
        className="sr-only"
      />
      <button className="primary-button" disabled={busy}>
        {busy ? "Sending..." : "Send message"}
      </button>
      {status && (
        <p role="status" className="muted">
          {status}
        </p>
      )}
    </form>
  );
}
