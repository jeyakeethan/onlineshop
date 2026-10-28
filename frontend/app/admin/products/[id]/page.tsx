"use client";
import Link from "next/link";
import { useParams } from "next/navigation";
import { FormEvent, useEffect, useState } from "react";
import { ProductDTO } from "../../../../types/api";
import AdminVariantCreateForm from "../../../../components/AdminVariantCreateForm";
import AdminVariantStatus from "../../../../components/AdminVariantStatus";
import AdminProductImageUpload from "../../../../components/AdminProductImageUpload";
export default function AdminProductDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [product, setProduct] = useState<ProductDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  useEffect(() => {
    if (!id) return;
    fetch(`/api/bff/admin/products/${encodeURIComponent(id)}`, {
      cache: "no-store",
    })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 403
              ? "You do not have permission to view this product."
              : d?.message || "Product not found.",
          );
        setProduct(d);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load product."),
      )
      .finally(() => setLoading(false));
  }, [id]);
  async function save(e: FormEvent) {
    e.preventDefault();
    if (!product) return;
    setSaving(true);
    setError(null);
    setMessage(null);
    try {
      const r = await fetch(
        `/api/bff/admin/products/${encodeURIComponent(id)}`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            name: product.name,
            description: product.description,
            slug: product.slug,
          }),
        },
      );
      const d = await r.json().catch(() => ({}));
      if (!r.ok) throw new Error(d?.message || "Unable to update product.");
      setProduct(d);
      setMessage("Product updated successfully.");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to update product.");
    } finally {
      setSaving(false);
    }
  }
  async function updateStatus(status: string) {
    if (!product) return;
    try {
      const r = await fetch(
        `/api/bff/admin/products/${encodeURIComponent(id)}`,
        {
          method: "PUT",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({ status }),
        },
      );
      const d = await r.json().catch(() => ({}));
      if (!r.ok)
        throw new Error(d?.message || "Unable to update product status.");
      setProduct(d);
      setMessage("Product status updated.");
    } catch (e) {
      setError(
        e instanceof Error ? e.message : "Unable to update product status.",
      );
    }
  }
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading product...</div>
      </div>
    );
  if (!product)
    return (
      <div className="page-container">
        <div role="alert" className="empty">
          <h1>Product unavailable</h1>
          <p>{error || "Product not found."}</p>
          <Link href="/admin/products" className="primary-button">
            Back to catalog
          </Link>
        </div>
      </div>
    );
  return (
    <div className="page-container">
      <Link href="/admin/products" className="back-link">
        Back to catalog
      </Link>
      <div className="page-heading mt-6">
        <div>
          <p className="eyebrow">Admin product detail</p>
          <h1>{product.name}</h1>
          <p className="muted">Product #{product.productId}</p>
        </div>
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
      <section className="form-shell">
        <label>
          Status
          <select
            value={
              product.approved
                ? product.isAvailable
                  ? "published"
                  : "unpublished"
                : "draft"
            }
            onChange={(e) => updateStatus(e.target.value)}
          >
            <option value="draft">Draft</option>
            <option value="published">Published</option>
            <option value="unpublished">Unpublished</option>
            <option value="archived">Archived</option>
          </select>
        </label>
        <form onSubmit={save} className="stack-form">
          <label>
            Name
            <input
              required
              value={product.name || ""}
              onChange={(e) => setProduct({ ...product, name: e.target.value })}
            />
          </label>
          <label>
            Description
            <textarea
              required
              value={product.description || ""}
              onChange={(e) =>
                setProduct({ ...product, description: e.target.value })
              }
            />
          </label>
          <label>
            Slug
            <input
              value={product.slug || ""}
              onChange={(e) => setProduct({ ...product, slug: e.target.value })}
            />
          </label>
          <button className="primary-button" disabled={saving}>
            {saving ? "Saving..." : "Save changes"}
          </button>
        </form>
      </section>
      <section className="form-shell mt-6">
        <AdminVariantCreateForm
          productId={product.productId || Number(id)}
          onCreated={(v) =>
            setProduct({ ...product, skuDTOs: [...(product.skuDTOs || []), v] })
          }
          onMessage={setMessage}
          onError={setError}
        />
      </section>
      <section className="inventory-list mt-6">
        {product.skuDTOs?.map((sku, index) => {
          const variantId = sku.inventoryDTOS?.[0]?.skuId;
          return (
            <article
              className="inventory-product"
              key={`${sku.skuCode || "sku"}-${index}`}
            >
              <h2>{sku.skuCode || "SKU"}</h2>
              <p className="muted">
                {sku.size || "No size"}
                {sku.color ? ` · ${sku.color}` : ""}
              </p>
              {variantId && (
                <AdminVariantStatus
                  productId={product.productId || Number(id)}
                  variantId={variantId}
                  active={sku.active}
                  onUpdated={(v) =>
                    setProduct({
                      ...product,
                      skuDTOs: product.skuDTOs?.map((item, i) =>
                        i === index ? v : item,
                      ),
                    })
                  }
                  onError={setError}
                />
              )}
              {sku.inventoryDTOS?.map((inventory) => (
                <div className="inventory-row" key={inventory.inventoryId}>
                  <span>Inventory #{inventory.inventoryId}</span>
                  <span>Stock: {inventory.stockAvailable ?? 0}</span>
                  <span>Sell: {inventory.sellingPrice ?? 0}</span>
                  <span>Cost: {inventory.costPrice ?? 0}</span>
                </div>
              ))}
            </article>
          );
        })}
      </section>
    </div>
  );
}
