"use client";

import { useEffect, useState } from "react";
import { ProductDTO } from "../types/api";
import { ProductGrid } from "./storefront";

export function RelatedProducts({ productId }: { productId?: number }) {
  const [products, setProducts] = useState<ProductDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!productId) {
      setLoading(false);
      return;
    }
    fetch(`/api/bff/products/${productId}/related`, { cache: "no-store" })
      .then(async (response) => {
        const data = await response.json().catch(() => []);
        if (response.ok) setProducts(Array.isArray(data) ? data : []);
      })
      .catch(() => setProducts([]))
      .finally(() => setLoading(false));
  }, [productId]);

  if (loading)
    return (
      <section className="mt-12">
        <p className="muted">Loading related products...</p>
      </section>
    );
  if (!products.length) return null;
  return (
    <section className="mt-12">
      <p className="eyebrow">You may also like</p>
      <h2>Related products</h2>
      <ProductGrid products={products} />
    </section>
  );
}
