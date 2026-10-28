"use client";

import React, { useEffect, useState } from "react";
import { ProductDTO } from "../../types/api";
import { ProductGrid } from "../../components/storefront";

type FilterOption = { category?: string; subcategory?: string; slug?: string };
type BrandOption = { name?: string; slug?: string };

export default function ProductsPage() {
  const [products, setProducts] = useState<ProductDTO[]>([]);
  const [categories, setCategories] = useState<FilterOption[]>([]);
  const [brands, setBrands] = useState<BrandOption[]>([]);
  const [keyword, setKeyword] = useState("");
  const [category, setCategory] = useState("");
  const [brand, setBrand] = useState("");
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function loadProducts(
    nextKeyword = keyword,
    nextCategory = category,
    nextBrand = brand,
  ) {
    setLoading(true);
    setError(null);
    try {
      const normalizedKeyword = nextKeyword.trim();
      const params = new URLSearchParams();
      if (nextCategory) params.set("category", nextCategory);
      if (nextBrand) params.set("brand", nextBrand);
      params.set("sort", "featured");
      let url = `/api/bff/products?${params.toString()}`;
      if (normalizedKeyword)
        url = `/api/bff/search?q=${encodeURIComponent(normalizedKeyword)}`;
      const response = await fetch(url, { cache: "no-store" });
      const data = await response.json().catch(() => []);
      if (!response.ok)
        throw new Error(data?.message || "Unable to load products.");
      setProducts(Array.isArray(data) ? data : []);
    } catch (reason) {
      setProducts([]);
      setError(
        reason instanceof Error ? reason.message : "Unable to load products.",
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const initialCategory = params.get("category") || "";
    const initialBrand = params.get("brand") || "";
    setCategory(initialCategory);
    setBrand(initialBrand);
    Promise.all([
      fetch("/api/bff/categories", { cache: "no-store" }).then((response) =>
        response.ok ? response.json() : [],
      ),
      fetch("/api/bff/brands", { cache: "no-store" }).then((response) =>
        response.ok ? response.json() : [],
      ),
    ])
      .then(([categoryData, brandData]) => {
        setCategories(Array.isArray(categoryData) ? categoryData : []);
        setBrands(Array.isArray(brandData) ? brandData : []);
      })
      .catch(() => undefined);
    loadProducts("", initialCategory, initialBrand);
  }, []);

  useEffect(() => {
    const timeout = setTimeout(
      () => loadProducts(keyword, category, brand),
      250,
    );
    return () => clearTimeout(timeout);
  }, [keyword, category, brand]);

  useEffect(() => {
    const query = keyword.trim();
    if (query.length < 2) {
      setSuggestions([]);
      return;
    }
    const timeout = setTimeout(() => {
      fetch(`/api/bff/search/suggestions?query=${encodeURIComponent(query)}`, {
        cache: "no-store",
      })
        .then(async (response) => {
          const data = await response.json().catch(() => []);
          setSuggestions(response.ok && Array.isArray(data) ? data : []);
        })
        .catch(() => setSuggestions([]));
    }, 250);
    return () => clearTimeout(timeout);
  }, [keyword]);

  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Arc / Form collection</p>
          <h1>Products</h1>
        </div>
      </div>
      <div className="catalog-tools">
        <label className="search">
          <span aria-hidden="true">⌕</span>
          <input
            list="product-search-suggestions"
            aria-label="Search products"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder="Search products"
          />
          <datalist id="product-search-suggestions">
            {suggestions.map((suggestion) => (
              <option key={suggestion} value={suggestion} />
            ))}
          </datalist>
        </label>
        <div className="filters">
          <select
            aria-label="Filter by category"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
          >
            <option value="">All categories</option>
            {categories.map((item) => (
              <option
                key={item.slug || `${item.category}-${item.subcategory}`}
                value={item.subcategory || item.category || ""}
              >
                {item.subcategory || item.category}
              </option>
            ))}
          </select>
          <select
            aria-label="Filter by brand"
            value={brand}
            onChange={(e) => setBrand(e.target.value)}
          >
            <option value="">All brands</option>
            {brands.map((item) => (
              <option
                key={item.slug || item.name}
                value={item.name || item.slug || ""}
              >
                {item.name || item.slug}
              </option>
            ))}
          </select>
        </div>
      </div>
      <p className="muted" style={{ marginBottom: "1rem" }}>
        {loading
          ? "Loading products..."
          : `${products.length} item${products.length === 1 ? "" : "s"} found`}
      </p>
      {error ? (
        <div role="alert" className="empty">
          <h2>Products unavailable</h2>
          <p>{error}</p>
        </div>
      ) : products.length ? (
        <ProductGrid products={products} />
      ) : !loading ? (
        <div className="empty">
          <h2>No products match your filters.</h2>
        </div>
      ) : null}
    </div>
  );
}
