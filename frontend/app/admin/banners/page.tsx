"use client";
import Link from "next/link";
import { FormEvent, useEffect, useState } from "react";
type Banner = {
  bannerId?: number;
  title?: string;
  mediaUrl?: string;
  linkUrl?: string;
  displayOrder?: number;
  active?: boolean;
  startsAt?: string;
  endsAt?: string;
  archived?: boolean;
};
export default function AdminBannersPage() {
  const [banners, setBanners] = useState<Banner[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [form, setForm] = useState({
    title: "",
    mediaUrl: "",
    linkUrl: "",
    displayOrder: "0",
    startsAt: "",
    endsAt: "",
  });
  useEffect(() => {
    fetch("/api/bff/admin/banners", { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access banners."
              : r.status === 403
                ? "You do not have permission to view banners."
                : d?.message || "Unable to load banners.",
          );
        setBanners(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load banners."),
      )
      .finally(() => setLoading(false));
  }, []);
  async function create(event: FormEvent) {
    event.preventDefault();
    if (saving || !form.title.trim() || !form.mediaUrl.trim()) return;
    setSaving(true);
    setError(null);
    setMessage(null);
    try {
      const r = await fetch("/api/bff/admin/banners", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          ...form,
          title: form.title.trim(),
          mediaUrl: form.mediaUrl.trim(),
          linkUrl: form.linkUrl.trim(),
          displayOrder: Number(form.displayOrder),
          active: false,
          startsAt: form.startsAt || null,
          endsAt: form.endsAt || null,
        }),
      });
      const d = await r.json().catch(() => ({}));
      if (!r.ok)
        throw new Error(
          r.status === 403
            ? "You do not have permission to create banners."
            : d?.message || "Unable to create banner.",
        );
      setBanners((current) => [...current, d]);
      setForm({
        title: "",
        mediaUrl: "",
        linkUrl: "",
        displayOrder: "0",
        startsAt: "",
        endsAt: "",
      });
      setMessage("Banner created successfully.");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to create banner.");
    } finally {
      setSaving(false);
    }
  }
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading banners...</div>
      </div>
    );
  if (error && !banners.length)
    return (
      <div className="page-container">
        <div role="alert" className="empty">
          <h1>Access restricted</h1>
          <p>{error}</p>
          <Link href="/auth/login" className="primary-button">
            Sign in
          </Link>
        </div>
      </div>
    );
  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Content administration</p>
          <h1>Banners</h1>
        </div>
        <Link href="/admin/products" className="back-link">
          Back to catalog
        </Link>
      </div>
      <form onSubmit={create} className="auth-form" aria-label="Create banner">
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
          Starts at
          <input
            type="datetime-local"
            value={form.startsAt}
            onChange={(e) => setForm({ ...form, startsAt: e.target.value })}
            disabled={saving}
          />
        </label>
        <label>
          Ends at
          <input
            type="datetime-local"
            value={form.endsAt}
            onChange={(e) => setForm({ ...form, endsAt: e.target.value })}
            disabled={saving}
          />
        </label>
        <button
          className="primary-button"
          disabled={saving || !form.title.trim() || !form.mediaUrl.trim()}
        >
          {saving ? "Creating..." : "Create banner"}
        </button>
        {message && (
          <p role="status" className="success-message">
            {message}
          </p>
        )}
        {error && (
          <p role="alert" className="error-message">
            {error}
          </p>
        )}
      </form>
      {banners.length ? (
        <div className="inventory-list">
          {banners.map((banner) => (
            <article className="inventory-product" key={banner.bannerId}>
              <h2>{banner.title}</h2>
              <p className="muted">
                Order {banner.displayOrder ?? 0} ·{" "}
                {banner.archived
                  ? "Archived"
                  : banner.active
                    ? "Active"
                    : "Inactive"}
              </p>
              <p>{banner.mediaUrl}</p>
              {banner.linkUrl && <p>Link: {banner.linkUrl}</p>}
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No banners found.</div>
      )}
    </div>
  );
}
