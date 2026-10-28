"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Notification = {
  notificationId?: number;
  title?: string;
  message?: string;
  read?: boolean;
  createdAt?: string;
};
export default function NotificationsPage() {
  const [items, setItems] = useState<Notification[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    fetch(`/api/bff/me/notifications?page=${page}&size=20`, {
      cache: "no-store",
    })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to view notifications."
              : d?.message || "Unable to load notifications.",
          );
        setItems(d.content || []);
        setTotalPages(d.totalPages || 1);
      })
      .catch((e) =>
        setError(
          e instanceof Error ? e.message : "Unable to load notifications.",
        ),
      )
      .finally(() => setLoading(false));
  }, [page]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading notifications...</div>
      </div>
    );
  if (error)
    return (
      <div className="page-container">
        <div role="alert" className="empty">
          <h1>Notifications unavailable</h1>
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
          <p className="eyebrow">Your account</p>
          <h1>Notifications</h1>
        </div>
      </div>
      {items.length ? (
        <div className="inventory-list">
          {items.map((item) => (
            <article className="inventory-product" key={item.notificationId}>
              <h2>{item.title || "Notification"}</h2>
              <p>{item.message}</p>
              <p className="muted">
                {item.createdAt || ""} · {item.read ? "Read" : "Unread"}
              </p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No notifications.</div>
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
          disabled={page + 1 >= totalPages}
          onClick={() => setPage((current) => current + 1)}
        >
          Next
        </button>
      </div>
    </div>
  );
}
