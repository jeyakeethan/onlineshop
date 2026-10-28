"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Zone = {
  zoneId?: number;
  name?: string;
  regionCodes?: string[];
  postalCodePrefixes?: string[];
  priority?: number;
  active?: boolean;
};
export default function AdminShippingZonesPage() {
  const [zones, setZones] = useState<Zone[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    fetch("/api/bff/admin/shipping-zones", { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access shipping zones."
              : r.status === 403
                ? "You do not have permission to view shipping zones."
                : d?.message || "Unable to load shipping zones.",
          );
        setZones(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(
          e instanceof Error ? e.message : "Unable to load shipping zones.",
        ),
      )
      .finally(() => setLoading(false));
  }, []);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading shipping zones...</div>
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
          <p className="eyebrow">Shipping administration</p>
          <h1>Shipping zones</h1>
        </div>
        <Link href="/admin/products" className="back-link">
          Back to catalog
        </Link>
      </div>
      {zones.length ? (
        <div className="inventory-list">
          {zones.map((zone) => (
            <article className="inventory-product" key={zone.zoneId}>
              <h2>{zone.name}</h2>
              <p className="muted">
                Priority {zone.priority ?? 0} ·{" "}
                {zone.active ? "Active" : "Inactive"}
              </p>
              <p>Regions: {zone.regionCodes?.join(", ") || "Any"}</p>
              <p>
                Postal prefixes: {zone.postalCodePrefixes?.join(", ") || "Any"}
              </p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No shipping zones found.</div>
      )}
    </div>
  );
}
