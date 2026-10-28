"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Dashboard = {
  from?: string;
  to?: string;
  totalOrders?: number;
  paidOrderCount?: number;
  paidOrderTotal?: number;
  pendingOrderCount?: number;
  pendingOrderTotal?: number;
  lowStockItems?: Array<{ skuCode?: string; available?: number }>;
  recentEvents?: Array<{
    movementId?: number;
    quantityDelta?: number;
    reason?: string;
    occurredAt?: string;
  }>;
};
export default function AdminDashboardPage() {
  const [dashboard, setDashboard] = useState<Dashboard | null>(null);
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    const query = new URLSearchParams();
    if (from) query.set("from", from);
    if (to) query.set("to", to);
    fetch(`/api/bff/admin/dashboard?${query}`, { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access the dashboard."
              : r.status === 403
                ? "You do not have permission to view the dashboard."
                : d?.message || "Unable to load dashboard.",
          );
        setDashboard(d);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load dashboard."),
      )
      .finally(() => setLoading(false));
  }, [from, to]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading dashboard...</div>
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
          <p className="eyebrow">Operations overview</p>
          <h1>Dashboard</h1>
          <p className="muted">
            {dashboard?.from} to {dashboard?.to}
          </p>
        </div>
      </div>
      <div className="admin-form-row">
        <label>
          From
          <input
            type="date"
            value={from}
            onChange={(e) => setFrom(e.target.value)}
          />
        </label>
        <label>
          To
          <input
            type="date"
            value={to}
            onChange={(e) => setTo(e.target.value)}
          />
        </label>
      </div>
      <div className="inventory-list">
        <article className="inventory-product">
          <h2>Total orders</h2>
          <p>{dashboard?.totalOrders ?? 0}</p>
        </article>
        <article className="inventory-product">
          <h2>Paid</h2>
          <p>
            {dashboard?.paidOrderCount ?? 0} · {dashboard?.paidOrderTotal ?? 0}
          </p>
        </article>
        <article className="inventory-product">
          <h2>Pending</h2>
          <p>
            {dashboard?.pendingOrderCount ?? 0} ·{" "}
            {dashboard?.pendingOrderTotal ?? 0}
          </p>
        </article>
      </div>
      <section className="account-card mt-6">
        <h2>Low stock</h2>
        {dashboard?.lowStockItems?.length ? (
          dashboard.lowStockItems.map((item, index) => (
            <p className="muted" key={`${item.skuCode}-${index}`}>
              {item.skuCode}: {item.available}
            </p>
          ))
        ) : (
          <p className="muted">No low-stock items.</p>
        )}
      </section>
    </div>
  );
}
