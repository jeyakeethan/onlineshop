"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Template = {
  templateId?: number;
  templateKey?: string;
  subject?: string;
  body?: string;
  allowedVariables?: string;
  active?: boolean;
  lastUpdatedBy?: string;
  lastUpdatedAt?: string;
};
export default function AdminEmailTemplatesPage() {
  const [templates, setTemplates] = useState<Template[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    fetch(`/api/bff/admin/email-templates?page=${page}&size=20`, {
      cache: "no-store",
    })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access email templates."
              : r.status === 403
                ? "You do not have permission to view email templates."
                : d?.message || "Unable to load email templates.",
          );
        setTemplates(d.content || []);
        setTotalPages(d.totalPages || 1);
      })
      .catch((e) =>
        setError(
          e instanceof Error ? e.message : "Unable to load email templates.",
        ),
      )
      .finally(() => setLoading(false));
  }, [page]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading email templates...</div>
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
          <p className="eyebrow">Communications administration</p>
          <h1>Email templates</h1>
        </div>
      </div>
      {templates.length ? (
        <div className="inventory-list">
          {templates.map((template) => (
            <article className="inventory-product" key={template.templateId}>
              <h2>{template.templateKey}</h2>
              <p>{template.subject}</p>
              <p className="muted">
                {template.active ? "Active" : "Inactive"} · Updated{" "}
                {template.lastUpdatedAt || "Unknown"}
              </p>
              <p>Allowed variables: {template.allowedVariables || "None"}</p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No email templates found.</div>
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
