"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

type Brand = { brandId?: number; name?: string; active?: boolean };

export default function AdminBrandsPage() {
  const [brands, setBrands] = useState<Brand[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [name, setName] = useState("");
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editName, setEditName] = useState("");
  const [updating, setUpdating] = useState(false);

  useEffect(() => {
    fetch("/api/bff/admin/brands", { cache: "no-store" })
      .then(async (response) => {
        const data = await response.json().catch(() => ({}));
        if (!response.ok)
          throw new Error(
            response.status === 401
              ? "Please sign in to access brand administration."
              : response.status === 403
                ? "You do not have permission to view brands."
                : data?.message || "Unable to load brands.",
          );
        setBrands(Array.isArray(data) ? data : []);
      })
      .catch((reason) =>
        setError(
          reason instanceof Error ? reason.message : "Unable to load brands.",
        ),
      )
      .finally(() => setLoading(false));
  }, []);

  async function createBrand(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const trimmed = name.trim();
    if (!trimmed || saving) return;
    setSaving(true);
    setMessage(null);
    setError(null);
    try {
      const response = await fetch("/api/bff/admin/brands", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ name: trimmed, active: true }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to create brands."
            : response.status === 403
              ? "You do not have permission to create brands."
              : data?.message || "Unable to create brand.",
        );
      setBrands((current) => [...current, data]);
      setName("");
      setMessage("Brand created successfully.");
    } catch (reason) {
      setError(
        reason instanceof Error ? reason.message : "Unable to create brand.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function updateBrand(
    event: React.FormEvent<HTMLFormElement>,
    brand: Brand,
  ) {
    event.preventDefault();
    const trimmed = editName.trim();
    if (!trimmed || !brand.brandId || updating) return;
    setUpdating(true);
    setMessage(null);
    setError(null);
    try {
      const response = await fetch(`/api/bff/admin/brands/${brand.brandId}`, {
        method: "PATCH",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ name: trimmed, active: brand.active }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to update brands."
            : response.status === 403
              ? "You do not have permission to update brands."
              : data?.message || "Unable to update brand.",
        );
      setBrands((current) =>
        current.map((item) => (item.brandId === brand.brandId ? data : item)),
      );
      setEditingId(null);
      setMessage("Brand updated successfully.");
    } catch (reason) {
      setError(
        reason instanceof Error ? reason.message : "Unable to update brand.",
      );
    } finally {
      setUpdating(false);
    }
  }

  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading brands...</div>
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
          <p className="eyebrow">Catalog administration</p>
          <h1>Brands</h1>
        </div>
        <Link href="/admin/products" className="back-link">
          Back to products
        </Link>
      </div>
      <form
        onSubmit={createBrand}
        className="auth-form"
        aria-label="Create brand"
      >
        <label htmlFor="brand-name">Brand name</label>
        <input
          id="brand-name"
          value={name}
          onChange={(event) => setName(event.target.value)}
          maxLength={120}
          required
          placeholder="Enter a unique brand name"
          disabled={saving}
        />
        <button
          className="primary-button"
          type="submit"
          disabled={saving || !name.trim()}
        >
          {saving ? "Creating..." : "Create brand"}
        </button>
        {message && (
          <p role="status" className="success-message">
            {message}
          </p>
        )}
        {error && (
          <p role="alert" className="error-message">
            {error}
          </p>
        )}
      </form>
      {brands.length ? (
        <div className="inventory-list">
          {brands.map((brand) => (
            <article className="inventory-product" key={brand.brandId}>
              {editingId === brand.brandId ? (
                <form onSubmit={(event) => updateBrand(event, brand)}>
                  <label htmlFor={`edit-brand-${brand.brandId}`}>
                    Brand name
                  </label>
                  <input
                    id={`edit-brand-${brand.brandId}`}
                    value={editName}
                    onChange={(event) => setEditName(event.target.value)}
                    maxLength={120}
                    required
                    disabled={updating}
                  />
                  <button
                    className="primary-button"
                    type="submit"
                    disabled={updating || !editName.trim()}
                  >
                    {updating ? "Saving..." : "Save"}
                  </button>
                  <button
                    className="back-link"
                    type="button"
                    onClick={() => setEditingId(null)}
                    disabled={updating}
                  >
                    Cancel
                  </button>
                </form>
              ) : (
                <>
                  <h2>{brand.name}</h2>
                  <p className="muted">
                    Brand #{brand.brandId} ·{" "}
                    {brand.active ? "Active" : "Inactive"}
                  </p>
                  <button
                    className="back-link"
                    type="button"
                    onClick={() => {
                      setEditingId(brand.brandId ?? null);
                      setEditName(brand.name ?? "");
                      setError(null);
                      setMessage(null);
                    }}
                  >
                    Edit
                  </button>
                </>
              )}
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No brands found.</div>
      )}
    </div>
  );
}
