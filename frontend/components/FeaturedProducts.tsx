"use client";

import { useEffect, useState } from "react";
import { ProductDTO } from "../types/api";
import { ProductGrid } from "./storefront";

export function FeaturedProducts() {
  const [products, setProducts] = useState<ProductDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("/api/bff/homepage", { cache: "no-store" })
      .then(async (response) => {
        const data = await response.json().catch(() => ({}));
        if (response.ok)
          setProducts(
            Array.isArray(data?.featuredProducts) ? data.featuredProducts : [],
          );
      })
      .catch(() => setProducts([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="muted">Loading featured products...</p>;
  if (!products.length)
    return (
      <div className="empty">
        <p>No featured products are available yet.</p>
      </div>
    );
  return <ProductGrid products={products} />;
}
