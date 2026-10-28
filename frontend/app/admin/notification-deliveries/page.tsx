"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Delivery = {
  deliveryId?: number;
  templateKey?: string;
  recipientMasked?: string;
  status?: string;
  attempts?: number;
  lastError?: string;
  nextAttemptAt?: string;
  createdAt?: string;
};
type Page = { content?: Delivery[]; totalPages?: number };
export default function AdminNotificationDeliveriesPage() {
  const [data, setData] = useState<Page>({});
  const [status, setStatus] = useState("");
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    const query = new URLSearchParams({ page: String(page), size: "20" });
    if (status) query.set("status", status);
    fetch(`/api/bff/admin/notification-deliveries?${query}`, {
      cache: "no-store",
    })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access deliveries."
              : r.status === 403
                ? "You do not have permission to view deliveries."
                : d?.message || "Unable to load deliveries.",
          );
        setData(d);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load deliveries."),
      )
      .finally(() => setLoading(false));
  }, [page, status]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading notification deliveries...</div>
      </div>
    );
  if (error)
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
  const items = data.content || [];
  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Communications operations</p>
          <h1>Notification deliveries</h1>
        </div>
      </div>
      <label>
        Status
        <select
          value={status}
          onChange={(e) => {
            setPage(0);
            setStatus(e.target.value);
          }}
        >
          <option value="">All</option>
          <option>FAILED</option>
          <option>PENDING</option>
          <option>SENT</option>
        </select>
      </label>
      {items.length ? (
        <div className="inventory-list">
          {items.map((item) => (
            <article className="inventory-product" key={item.deliveryId}>
              <h2>{item.templateKey}</h2>
              <p className="muted">
                {item.recipientMasked} · {item.status} · Attempts{" "}
                {item.attempts ?? 0}
              </p>
              {item.lastError && <p>{item.lastError}</p>}
              <p className="muted">
                Created {item.createdAt || ""} · Next attempt{" "}
                {item.nextAttemptAt || "—"}
              </p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No notification deliveries found.</div>
      )}
      <div className="page-actions">
        <button
          className="back-link"
          disabled={page === 0}
          onClick={() => setPage((current) => current - 1)}
        >
          Previous
        </button>
        <span>Page {page + 1}</span>
        <button
          className="back-link"
          disabled={page + 1 >= (data.totalPages || 1)}
          onClick={() => setPage((current) => current + 1)}
        >
          Next
        </button>
      </div>
    </div>
  );
}
