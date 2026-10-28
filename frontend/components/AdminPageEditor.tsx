"use client";
import { FormEvent, useState } from "react";
export default function AdminPageEditor({
  page,
  onUpdated,
  onError,
}: {
  page: {
    pageId: number;
    slug?: string;
    title?: string;
    content?: string;
    published?: boolean;
  };
  onUpdated: (page: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    slug: page.slug || "",
    title: page.title || "",
    content: page.content || "",
    published: Boolean(page.published),
  });
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !form.title.trim() || !form.slug.trim()) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(`/api/bff/admin/pages/${page.pageId}`, {
        method: "PATCH",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          ...form,
          slug: form.slug.trim(),
          title: form.title.trim(),
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update pages."
            : data?.message || "Unable to update page.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to update page.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Slug
        <input
          required
          maxLength={160}
          value={form.slug}
          onChange={(e) => setForm({ ...form, slug: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Title
        <input
          required
          maxLength={200}
          value={form.title}
          onChange={(e) => setForm({ ...form, title: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Content
        <textarea
          maxLength={100000}
          value={form.content}
          onChange={(e) => setForm({ ...form, content: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        <input
          type="checkbox"
          checked={form.published}
          onChange={(e) => setForm({ ...form, published: e.target.checked })}
          disabled={busy}
        />{" "}
        Published
      </label>
      <button
        className="primary-button"
        disabled={busy || !form.slug.trim() || !form.title.trim()}
      >
        {busy ? "Saving..." : "Save page"}
      </button>
    </form>
  );
}
