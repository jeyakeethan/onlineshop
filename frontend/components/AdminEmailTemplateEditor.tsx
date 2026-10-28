"use client";
import { FormEvent, useState } from "react";
export default function AdminEmailTemplateEditor({
  template,
  onUpdated,
  onError,
}: {
  template: {
    templateId: number;
    subject?: string;
    body?: string;
    allowedVariables?: string;
    active?: boolean;
  };
  onUpdated: (template: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    subject: template.subject || "",
    body: template.body || "",
    allowedVariables: template.allowedVariables || "",
    active: Boolean(template.active),
  });
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !form.subject.trim()) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/email-templates/${template.templateId}`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify(form),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update email templates."
            : data?.message || "Unable to update email template.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to update email template.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Subject
        <input
          required
          maxLength={200}
          value={form.subject}
          onChange={(e) => setForm({ ...form, subject: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Body
        <textarea
          maxLength={100000}
          value={form.body}
          onChange={(e) => setForm({ ...form, body: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Allowed variables
        <input
          maxLength={2000}
          value={form.allowedVariables}
          onChange={(e) =>
            setForm({ ...form, allowedVariables: e.target.value })
          }
          disabled={busy}
        />
      </label>
      <label>
        <input
          type="checkbox"
          checked={form.active}
          onChange={(e) => setForm({ ...form, active: e.target.checked })}
          disabled={busy}
        />{" "}
        Active
      </label>
      <button
        className="primary-button"
        disabled={busy || !form.subject.trim()}
      >
        {busy ? "Saving..." : "Save template"}
      </button>
    </form>
  );
}
