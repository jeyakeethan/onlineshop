"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
import AdminCollectionCreateForm from "../../../components/AdminCollectionCreateForm";
type Collection = {
  collectionId?: number;
  name?: string;
  slug?: string;
  active?: boolean;
  displayOrder?: number;
  productIds?: number[];
};
export default function AdminCollectionsPage() {
  const [collections, setCollections] = useState<Collection[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  useEffect(() => {
    fetch("/api/bff/admin/collections", { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access collections."
              : r.status === 403
                ? "You do not have permission to view collections."
                : d?.message || "Unable to load collections.",
          );
        setCollections(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(
          e instanceof Error ? e.message : "Unable to load collections.",
        ),
      )
      .finally(() => setLoading(false));
  }, []);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading collections...</div>
      </div>
    );
  if (error && !collections.length)
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
          <p className="eyebrow">Content administration</p>
          <h1>Featured collections</h1>
        </div>
        <Link href="/admin/products" className="back-link">
          Back to catalog
        </Link>
      </div>
      <section className="form-shell">
        <AdminCollectionCreateForm
          onCreated={(collection) =>
            setCollections((current) => [...current, collection])
          }
          onMessage={setMessage}
          onError={setError}
        />
      </section>
      {message && (
        <p role="status" className="status">
          {message}
        </p>
      )}
      {error && (
        <p role="alert" className="form-error">
          {error}
        </p>
      )}
      {collections.length ? (
        <div className="inventory-list">
          {collections.map((collection) => (
            <article
              className="inventory-product"
              key={collection.collectionId}
            >
              <h2>{collection.name}</h2>
              <p className="muted">
                {collection.slug} · Order {collection.displayOrder ?? 0} ·{" "}
                {collection.active ? "Active" : "Inactive"}
              </p>
              <p>Products: {collection.productIds?.length ?? 0}</p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No featured collections found.</div>
      )}
    </div>
  );
}
