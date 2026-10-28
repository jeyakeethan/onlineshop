"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

type Category = { category?: string; subcategory?: string; slug?: string };

export function CategoryGrid() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("/api/bff/categories", { cache: "no-store" })
      .then(async (response) => {
        if (response.ok) setCategories(await response.json());
      })
      .catch(() => setCategories([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading)
    return (
      <div className="category-grid" aria-busy="true">
        <p className="muted">Loading categories...</p>
      </div>
    );
  if (!categories.length)
    return (
      <div className="empty">
        <p>No categories are available yet.</p>
      </div>
    );

  return (
    <div className="category-grid">
      {categories.map((item) => (
        <Link
          key={item.slug || `${item.category}-${item.subcategory}`}
          href={
            item.slug
              ? `/categories/${encodeURIComponent(item.slug)}`
              : `/products?category=${encodeURIComponent(item.category || "")}`
          }
          className="category-card"
        >
          <span>{item.subcategory || item.category}</span>
        </Link>
      ))}
    </div>
  );
}
