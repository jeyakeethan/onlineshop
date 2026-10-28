"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

type Brand = { name?: string; slug?: string; active?: boolean };

export default function BrandsPage() {
  const [brands, setBrands] = useState<Brand[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch("/api/bff/brands", { cache: "no-store" })
      .then(async (response) => {
        const data = await response.json().catch(() => []);
        if (!response.ok)
          throw new Error(data?.message || "Unable to load brands.");
        setBrands(Array.isArray(data) ? data : []);
      })
      .catch((reason) =>
        setError(
          reason instanceof Error ? reason.message : "Unable to load brands.",
        ),
      )
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">The collection</p>
          <h1>Brands</h1>
        </div>
      </div>
      {loading ? (
        <div className="empty">Loading brands...</div>
      ) : error ? (
        <div role="alert" className="empty">
          {error}
        </div>
      ) : brands.length === 0 ? (
        <div className="empty">No active brands are available.</div>
      ) : (
        <div className="category-grid">
          {brands.map((brand) => (
            <Link
              key={brand.slug || brand.name}
              href={`/products?brand=${encodeURIComponent(brand.slug || brand.name || "")}`}
              className="category-card"
            >
              <span>{brand.name || brand.slug}</span>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
