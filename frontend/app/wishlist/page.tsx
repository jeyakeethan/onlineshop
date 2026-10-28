"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Item = {
  inventoryId?: number;
  skuId?: number;
  skuCode?: string;
  sellingPrice?: number;
  stockAvailable?: number;
};
export default function WishlistPage() {
  const [items, setItems] = useState<Item[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    fetch("/api/bff/wishlist", { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to view your wishlist."
              : d?.message || "Unable to load wishlist.",
          );
        setItems(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load wishlist."),
      )
      .finally(() => setLoading(false));
  }, []);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading wishlist...</div>
      </div>
    );
  if (error)
    return (
      <div className="page-container">
        <div role="alert" className="empty">
          <h1>Wishlist unavailable</h1>
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
          <p className="eyebrow">Saved items</p>
          <h1>Wishlist</h1>
        </div>
      </div>
      {items.length ? (
        <div className="inventory-list">
          {items.map((item) => (
            <article
              className="inventory-product"
              key={item.skuId || item.inventoryId}
            >
              <h2>{item.skuCode || `Inventory #${item.inventoryId}`}</h2>
              <p className="muted">
                Variant #{item.skuId || "—"} · Price {item.sellingPrice ?? 0}
              </p>
              <p>
                {item.stockAvailable && item.stockAvailable > 0
                  ? "Available"
                  : "Currently unavailable"}
              </p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">
          <h2>Your wishlist is empty.</h2>
          <Link href="/products" className="primary-button">
            Browse products
          </Link>
        </div>
      )}
    </div>
  );
}
