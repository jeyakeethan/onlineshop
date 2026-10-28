"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { CategoryDTO } from "../../../types/api";

export default function CategoryPage() {
  const params = useParams<{ slug: string }>();
  const [category, setCategory] = useState<CategoryDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!params.slug) return;
    fetch(`/api/bff/categories/${encodeURIComponent(params.slug)}`, {
      cache: "no-store",
    })
      .then(async (response) => {
        const data = await response.json().catch(() => ({}));
        if (!response.ok)
          throw new Error(data?.message || "Category not found.");
        setCategory(data);
      })
      .catch((reason) =>
        setError(
          reason instanceof Error ? reason.message : "Unable to load category.",
        ),
      )
      .finally(() => setLoading(false));
  }, [params.slug]);

  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading category...</div>
      </div>
    );
  if (error || !category)
    return (
      <div className="page-container">
        <div role="alert" className="empty">
          <h1>Category unavailable</h1>
          <p>{error || "This category could not be found."}</p>
          <Link href="/products" className="primary-button">
            Browse products
          </Link>
        </div>
      </div>
    );

  return (
    <div className="page-container">
      <Link href="/products" className="back-link">
        Back to products
      </Link>
      <p className="eyebrow mt-6">Category</p>
      <h1>{category.subcategory || category.category}</h1>
      {category.breadcrumb?.length ? (
        <p className="muted">{category.breadcrumb.join(" / ")}</p>
      ) : null}
      <div className="mt-8">
        <Link
          href={`/products?category=${encodeURIComponent(category.category || "")}`}
          className="primary-button"
        >
          Browse this category
        </Link>
      </div>
    </div>
  );
}
