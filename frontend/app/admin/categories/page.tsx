"use client";

import Link from "next/link";
import { FormEvent, useEffect, useState } from "react";
import { CategoryDTO } from "../../../types/api";

export default function AdminCategoriesPage() {
  const [categories, setCategories] = useState<CategoryDTO[]>([]);
  const [category, setCategory] = useState("");
  const [subcategory, setSubcategory] = useState("");
  const [editing, setEditing] = useState<CategoryDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  async function load() {
    const response = await fetch("/api/bff/admin/categories", {
      cache: "no-store",
    });
    const data = await response.json().catch(() => ({}));
    if (!response.ok)
      throw new Error(
        response.status === 401
          ? "Please sign in to access category administration."
          : response.status === 403
            ? "You do not have permission to view categories."
            : data?.message || "Unable to load categories.",
      );
    setCategories(Array.isArray(data) ? data : []);
  }

  useEffect(() => {
    load()
      .catch((reason) =>
        setError(
          reason instanceof Error
            ? reason.message
            : "Unable to load categories.",
        ),
      )
      .finally(() => setLoading(false));
  }, []);

  async function createCategory(event: FormEvent) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    setMessage(null);
    try {
      const response = await fetch("/api/bff/admin/categories", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          category: category.trim(),
          subcategory: subcategory.trim() || null,
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to create categories."
            : data?.message || "Unable to create category.",
        );
      setCategory("");
      setSubcategory("");
      setMessage("Category created successfully.");
      await load();
    } catch (reason) {
      setError(
        reason instanceof Error ? reason.message : "Unable to create category.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function updateCategory(event: FormEvent) {
    event.preventDefault();
    if (!editing?.category) return;
    setSaving(true);
    setError(null);
    setMessage(null);
    const path = [
      editing.category,
      ...(editing.subcategory ? [editing.subcategory] : []),
    ]
      .map(encodeURIComponent)
      .join("/");
    try {
      const response = await fetch(`/api/bff/admin/categories/${path}`, {
        method: "PATCH",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          category: editing.category,
          subcategory: editing.subcategory || null,
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update categories."
            : data?.message || "Unable to update category.",
        );
      setEditing(null);
      setMessage("Category updated successfully.");
      await load();
    } catch (reason) {
      setError(
        reason instanceof Error ? reason.message : "Unable to update category.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function deleteCategory(item: CategoryDTO) {
    if (
      !item.category ||
      !window.confirm(`Delete ${item.subcategory || item.category}?`)
    )
      return;
    setSaving(true);
    setError(null);
    setMessage(null);
    const path = [
      item.category,
      ...(item.subcategory ? [item.subcategory] : []),
    ]
      .map(encodeURIComponent)
      .join("/");
    try {
      const response = await fetch(`/api/bff/admin/categories/${path}`, {
        method: "DELETE",
      });
      const text = await response.text();
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to delete categories."
            : text || "Unable to delete category.",
        );
      setMessage("Category deleted successfully.");
      await load();
    } catch (reason) {
      setError(
        reason instanceof Error ? reason.message : "Unable to delete category.",
      );
    } finally {
      setSaving(false);
    }
  }

  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading categories...</div>
      </div>
    );
  if (error && !categories.length)
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
          <p className="eyebrow">Catalog administration</p>
          <h1>Categories</h1>
        </div>
        <Link href="/admin/products" className="back-link">
          Back to products
        </Link>
      </div>
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
      <section className="form-shell mb-6">
        <form onSubmit={createCategory} className="admin-form-row">
          <label>
            Category
            <input
              required
              value={category}
              onChange={(event) => setCategory(event.target.value)}
            />
          </label>
          <label>
            Subcategory
            <input
              value={subcategory}
              onChange={(event) => setSubcategory(event.target.value)}
            />
          </label>
          <button type="submit" className="primary-button" disabled={saving}>
            {saving ? "Creating..." : "Create category"}
          </button>
        </form>
      </section>
      {editing && (
        <section className="form-shell mb-6">
          <form onSubmit={updateCategory} className="admin-form-row">
            <label>
              Category
              <input
                required
                value={editing.category || ""}
                onChange={(event) =>
                  setEditing({ ...editing, category: event.target.value })
                }
              />
            </label>
            <label>
              Subcategory
              <input
                value={editing.subcategory || ""}
                onChange={(event) =>
                  setEditing({ ...editing, subcategory: event.target.value })
                }
              />
            </label>
            <button type="submit" className="primary-button" disabled={saving}>
              {saving ? "Saving..." : "Save category"}
            </button>
            <button
              type="button"
              className="secondary-button"
              onClick={() => setEditing(null)}
            >
              Cancel
            </button>
          </form>
        </section>
      )}
      {categories.length ? (
        <div className="inventory-list">
          {categories.map((item) => (
            <article
              className="inventory-product"
              key={item.slug || `${item.category}-${item.subcategory}`}
            >
              <h2>{item.category}</h2>
              {item.subcategory && <p>{item.subcategory}</p>}
              <p className="muted">Slug: {item.slug || "—"}</p>
              <button
                type="button"
                className="text-button"
                onClick={() => setEditing(item)}
              >
                Edit
              </button>
              <button
                type="button"
                className="text-button text-red-700"
                disabled={saving}
                onClick={() => deleteCategory(item)}
              >
                Delete
              </button>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No categories found.</div>
      )}
    </div>
  );
}
