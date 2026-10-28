"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Inventory = {
  inventoryId?: number;
  variantId?: number;
  skuCode?: string;
  onHand?: number;
  reserved?: number;
  available?: number;
  lowStockThreshold?: number;
  lowStock?: boolean;
};
export default function AdminInventoryPage() {
  const [items, setItems] = useState<Inventory[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    fetch("/api/bff/admin/inventory", { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access inventory."
              : r.status === 403
                ? "You do not have permission to view inventory."
                : d?.message || "Unable to load inventory.",
          );
        setItems(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load inventory."),
      )
      .finally(() => setLoading(false));
  }, []);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading inventory...</div>
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
          <p className="eyebrow">Inventory administration</p>
          <h1>Inventory</h1>
        </div>
        <Link href="/admin/products" className="back-link">
          Back to catalog
        </Link>
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
              <div className="inventory-row">
                <span>On hand: {item.onHand ?? 0}</span>
                <span>Reserved: {item.reserved ?? 0}</span>
                <span>Available: {item.available ?? 0}</span>
                <strong>{item.lowStock ? "Low stock" : "Healthy"}</strong>
              </div>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No inventory records found.</div>
      )}
    </div>
  );
}
