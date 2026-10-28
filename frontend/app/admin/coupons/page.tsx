"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Coupon = {
  couponId?: number;
  code?: string;
  description?: string;
  type?: string;
  value?: number;
  minSpend?: number;
  usageLimit?: number;
  usedCount?: number;
  active?: boolean;
  startAt?: string;
  endAt?: string;
};
export default function AdminCouponsPage() {
  const [coupons, setCoupons] = useState<Coupon[]>([]);
  const [status, setStatus] = useState("");
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    const query = new URLSearchParams();
    if (status) query.set("status", status);
    if (search) query.set("search", search);
    fetch(`/api/bff/admin/coupons?${query}`, { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access coupons."
              : r.status === 403
                ? "You do not have permission to view coupons."
                : d?.message || "Unable to load coupons.",
          );
        setCoupons(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load coupons."),
      )
      .finally(() => setLoading(false));
  }, [status, search]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading coupons...</div>
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
          <p className="eyebrow">Pricing administration</p>
          <h1>Coupons</h1>
        </div>
        <Link href="/admin/products" className="back-link">
          Back to catalog
        </Link>
      </div>
      <div className="admin-form-row">
        <label>
          Search
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Code or description"
          />
        </label>
        <label>
          Status
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">All</option>
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">Inactive</option>
            <option value="EXPIRED">Expired</option>
          </select>
        </label>
      </div>
      {coupons.length ? (
        <div className="inventory-list">
          {coupons.map((coupon) => (
            <article className="inventory-product" key={coupon.couponId}>
              <h2>{coupon.code}</h2>
              <p>{coupon.description}</p>
              <p className="muted">
                {coupon.type} {coupon.value} · Used {coupon.usedCount ?? 0}/
                {coupon.usageLimit ?? 0} ·{" "}
                {coupon.active ? "Active" : "Inactive"}
              </p>
              <p>Minimum spend: {coupon.minSpend ?? 0}</p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No coupons found.</div>
      )}
    </div>
  );
}
