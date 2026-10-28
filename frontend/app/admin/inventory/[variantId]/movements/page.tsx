"use client";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
type Movement = {
  movementId?: number;
  inventoryId?: number;
  variantId?: number;
  quantityDelta?: number;
  reason?: string;
  actorId?: string;
  reference?: string;
  occurredAt?: string;
};
type Page = { content?: Movement[]; number?: number; totalPages?: number };
export default function InventoryMovementsPage() {
  const { variantId } = useParams<{ variantId: string }>();
  const [data, setData] = useState<Page>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  useEffect(() => {
    if (!variantId) return;
    setLoading(true);
    fetch(
      `/api/bff/admin/inventory/${encodeURIComponent(variantId)}/movements?page=${page}&size=20`,
      { cache: "no-store" },
    )
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access movements."
              : r.status === 403
                ? "You do not have permission to view movements."
                : d?.message || "Unable to load movements.",
          );
        setData(d);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load movements."),
      )
      .finally(() => setLoading(false));
  }, [variantId, page]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading movements...</div>
      </div>
    );
  if (error)
    return (
      <div className="page-container">
        <div role="alert" className="empty">
          <h1>Movements unavailable</h1>
          <p>{error}</p>
          <Link href="/admin/inventory" className="primary-button">
            Back to inventory
          </Link>
        </div>
      </div>
    );
  const movements = data.content || [];
  return (
    <div className="page-container">
      <Link href="/admin/inventory" className="back-link">
        Back to inventory
      </Link>
      <div className="page-heading">
        <div>
          <p className="eyebrow">Inventory ledger</p>
          <h1>Variant #{variantId} movements</h1>
        </div>
      </div>
      {movements.length ? (
        <div className="inventory-list">
          {movements.map((movement) => (
            <article className="inventory-product" key={movement.movementId}>
              <h2>
                {movement.quantityDelta && movement.quantityDelta > 0
                  ? "+"
                  : ""}
                {movement.quantityDelta ?? 0} units
              </h2>
              <p className="muted">
                {movement.reason || "No reason"} ·{" "}
                {movement.occurredAt
                  ? new Date(movement.occurredAt).toLocaleString()
                  : "Unknown time"}
              </p>
              <p>
                Actor: {movement.actorId || "System"}
                {movement.reference
                  ? ` · Reference: ${movement.reference}`
                  : ""}
              </p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No movement history found.</div>
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
