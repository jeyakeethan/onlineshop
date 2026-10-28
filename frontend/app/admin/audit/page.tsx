"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Audit = {
  auditId?: string;
  action?: string;
  resource?: string;
  actor?: string;
  occurredAt?: string;
  summary?: string;
};
type Page = { content?: Audit[]; totalPages?: number };
export default function AdminAuditPage() {
  const [data, setData] = useState<Page>({});
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    fetch(`/api/bff/admin/audit?page=${page}&size=20`, { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access audit events."
              : r.status === 403
                ? "You do not have permission to view audit events."
                : d?.message || "Unable to load audit events.",
          );
        setData(d);
      })
      .catch((e) =>
        setError(
          e instanceof Error ? e.message : "Unable to load audit events.",
        ),
      )
      .finally(() => setLoading(false));
  }, [page]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading audit events...</div>
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
  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Security administration</p>
          <h1>Audit log</h1>
        </div>
      </div>
      {data.content?.length ? (
        <div className="inventory-list">
          {data.content.map((event) => (
            <article className="inventory-product" key={event.auditId}>
              <h2>{event.action}</h2>
              <p>{event.summary}</p>
              <p className="muted">
                {event.resource} · {event.actor} · {event.occurredAt}
              </p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No audit events found.</div>
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
