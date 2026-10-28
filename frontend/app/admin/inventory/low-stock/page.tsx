"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Item = {
  inventoryId?: number;
  variantId?: number;
  skuCode?: string;
  available?: number;
  lowStockThreshold?: number;
  lowStock?: boolean;
};
type Page = { content?: Item[]; totalPages?: number };
export default function LowStockPage() {
  const [data, setData] = useState<Page>({});
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    fetch(`/api/bff/admin/inventory/low-stock?page=${page}&size=20`, {
      cache: "no-store",
    })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access low-stock inventory."
              : r.status === 403
                ? "You do not have permission to view low-stock inventory."
                : d?.message || "Unable to load low-stock inventory.",
          );
        setData(d);
      })
      .catch((e) =>
        setError(
          e instanceof Error
            ? e.message
            : "Unable to load low-stock inventory.",
        ),
      )
      .finally(() => setLoading(false));
  }, [page]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading low-stock inventory...</div>
      </div>
    );
  if (error)
    return (
      <div className="page-container">
        <div role="alert" className="empty">
          <h1>Low-stock inventory unavailable</h1>
          <p>{error}</p>
          <Link href="/admin/inventory" className="primary-button">
            Back to inventory
          </Link>
        </div>
      </div>
    );
  const items = data.content || [];
  return (
    <div className="page-container">
      <Link href="/admin/inventory" className="back-link">
        Back to inventory
      </Link>
      <div className="page-heading">
        <div>
          <p className="eyebrow">Inventory administration</p>
          <h1>Low stock</h1>
        </div>
      </div>
      {items.length ? (
        <div className="inventory-list">
          {items.map((item) => (
            <article className="inventory-product" key={item.inventoryId}>
              <h2>{item.skuCode || `Inventory #${item.inventoryId}`}</h2>
              <p className="muted">
                Variant #{item.variantId} · Threshold{" "}
                {item.lowStockThreshold ?? 0}
              </p>
              <strong>Available: {item.available ?? 0}</strong>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No low-stock inventory found.</div>
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
