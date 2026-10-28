"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type ReturnRequest = {
  returnRequestId?: number;
  orderId?: number;
  status?: string;
  reason?: string;
  requestedAt?: string;
  refundDecision?: string;
  refundAmount?: number;
  items?: unknown[];
};
export default function AdminReturnsPage() {
  const [returns, setReturns] = useState<ReturnRequest[]>([]);
  const [status, setStatus] = useState("");
  const [eligible, setEligible] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    const query = new URLSearchParams();
    if (status) query.set("status", status);
    if (eligible) query.set("eligible", eligible);
    fetch(`/api/bff/admin/returns?${query}`, { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access returns."
              : r.status === 403
                ? "You do not have permission to view returns."
                : d?.message || "Unable to load returns.",
          );
        setReturns(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load returns."),
      )
      .finally(() => setLoading(false));
  }, [status, eligible]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading returns...</div>
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
          <p className="eyebrow">Operations</p>
          <h1>Returns</h1>
        </div>
        <Link href="/admin/orders" className="back-link">
          Orders
        </Link>
      </div>
      <div className="admin-form-row">
        <label>
          Status
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">All</option>
            <option value="REQUESTED">Requested</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
            <option value="RECEIVED">Received</option>
            <option value="RESOLVED">Resolved</option>
          </select>
        </label>
        <label>
          Eligible
          <select
            value={eligible}
            onChange={(e) => setEligible(e.target.value)}
          >
            <option value="">All</option>
            <option value="true">Eligible</option>
            <option value="false">Ineligible</option>
          </select>
        </label>
      </div>
      {returns.length ? (
        <div className="inventory-list">
          {returns.map((item) => (
            <article className="inventory-product" key={item.returnRequestId}>
              <h2>
                Return #{item.returnRequestId} · Order #{item.orderId}
              </h2>
              <p className="muted">
                {item.status} · {item.requestedAt || "Unknown date"} ·{" "}
                {item.items?.length || 0} items
              </p>
              <p>{item.reason}</p>
              {item.refundDecision && (
                <p>
                  Refund: {item.refundDecision} {item.refundAmount ?? 0}
                </p>
              )}
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No returns found.</div>
      )}
    </div>
  );
}
