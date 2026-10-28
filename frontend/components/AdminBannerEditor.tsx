"use client";
import { FormEvent, useState } from "react";
type Banner = {
  bannerId: number;
  title?: string;
  mediaUrl?: string;
  linkUrl?: string;
  displayOrder?: number;
  active?: boolean;
  startsAt?: string;
  endsAt?: string;
};
export default function AdminBannerEditor({
  banner,
  onUpdated,
  onError,
}: {
  banner: Banner;
  onUpdated: (banner: Banner) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    title: banner.title || "",
    mediaUrl: banner.mediaUrl || "",
    linkUrl: banner.linkUrl || "",
    displayOrder: String(banner.displayOrder ?? 0),
    active: Boolean(banner.active),
    startsAt: banner.startsAt || "",
    endsAt: banner.endsAt || "",
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (saving || !form.title.trim() || !form.mediaUrl.trim()) return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/banners/${banner.bannerId}`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            ...form,
            title: form.title.trim(),
            mediaUrl: form.mediaUrl.trim(),
            linkUrl: form.linkUrl.trim(),
            displayOrder: Number(form.displayOrder),
            startsAt: form.startsAt || null,
            endsAt: form.endsAt || null,
          }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update banners."
            : data?.message || "Unable to update banner.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to update banner.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Title
        <input
          required
          value={form.title}
          onChange={(e) => setForm({ ...form, title: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Media URL
        <input
          required
          type="url"
          value={form.mediaUrl}
          onChange={(e) => setForm({ ...form, mediaUrl: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Link URL
        <input
          type="url"
          value={form.linkUrl}
          onChange={(e) => setForm({ ...form, linkUrl: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Display order
        <input
          type="number"
          min="0"
          value={form.displayOrder}
          onChange={(e) => setForm({ ...form, displayOrder: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        <input
          type="checkbox"
          checked={form.active}
          onChange={(e) => setForm({ ...form, active: e.target.checked })}
          disabled={saving}
        />{" "}
        Active
      </label>
      <button className="primary-button" disabled={saving}>
        {saving ? "Saving..." : "Save banner"}
      </button>
    </form>
  );
}
