"use client";

import Link from "next/link";
import { useEffect, useState } from "react";

type Attribute = {
  id?: number;
  category?: string;
  subcategory?: string;
  name?: string;
  dataType?: string;
  allowedValues?: string[];
  filterable?: boolean;
};

export default function AdminAttributesPage() {
  const [attributes, setAttributes] = useState<Attribute[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [category, setCategory] = useState("");
  const [name, setName] = useState("");
  const [dataType, setDataType] = useState("TEXT");
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    fetch("/api/bff/admin/attributes", { cache: "no-store" })
      .then(async (response) => {
        const data = await response.json().catch(() => ({}));
        if (!response.ok)
          throw new Error(
            response.status === 401
              ? "Please sign in to access attributes."
              : response.status === 403
                ? "You do not have permission to view attributes."
                : data?.message || "Unable to load attributes.",
          );
        setAttributes(Array.isArray(data) ? data : []);
      })
      .catch((reason) =>
        setError(
          reason instanceof Error
            ? reason.message
            : "Unable to load attributes.",
        ),
      )
      .finally(() => setLoading(false));
  }, []);

  async function createAttribute(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const categoryValue = category.trim();
    const nameValue = name.trim();
    if (!categoryValue || !nameValue || saving) return;
    setSaving(true);
    setError(null);
    setMessage(null);
    try {
      const response = await fetch("/api/bff/admin/attributes", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          category: categoryValue,
          name: nameValue,
          dataType,
          allowedValues: [],
          filterable: false,
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to create attributes."
            : response.status === 403
              ? "You do not have permission to create attributes."
              : data?.message || "Unable to create attribute.",
        );
      setAttributes((current) => [...current, data]);
      setCategory("");
      setName("");
      setMessage("Attribute created successfully.");
    } catch (reason) {
      setError(
        reason instanceof Error
          ? reason.message
          : "Unable to create attribute.",
      );
    } finally {
      setSaving(false);
    }
  }

  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading attributes...</div>
      </div>
    );
  if (error && !attributes.length)
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
          <h1>Attributes</h1>
        </div>
        <Link href="/admin/products" className="back-link">
          Back to products
        </Link>
      </div>
      <form
        onSubmit={createAttribute}
        className="auth-form"
        aria-label="Create attribute"
      >
        <label htmlFor="attribute-category">Category</label>
        <input
          id="attribute-category"
          value={category}
          onChange={(event) => setCategory(event.target.value)}
          required
          disabled={saving}
        />
        <label htmlFor="attribute-name">Attribute name</label>
        <input
          id="attribute-name"
          value={name}
          onChange={(event) => setName(event.target.value)}
          required
          disabled={saving}
        />
        <label htmlFor="attribute-type">Data type</label>
        <select
          id="attribute-type"
          value={dataType}
          onChange={(event) => setDataType(event.target.value)}
          disabled={saving}
        >
          <option value="TEXT">Text</option>
          <option value="NUMBER">Number</option>
          <option value="BOOLEAN">Boolean</option>
          <option value="ENUM">Enum</option>
        </select>
        <button
          className="primary-button"
          type="submit"
          disabled={saving || !category.trim() || !name.trim()}
        >
          {saving ? "Creating..." : "Create attribute"}
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
      {attributes.length ? (
        <div className="inventory-list">
          {attributes.map((attribute) => (
            <article className="inventory-product" key={attribute.id}>
              <h2>{attribute.name}</h2>
              <p className="muted">
                {attribute.category}
                {attribute.subcategory
                  ? ` / ${attribute.subcategory}`
                  : ""} · {attribute.dataType || "Unspecified"} ·{" "}
                {attribute.filterable ? "Filterable" : "Not filterable"}
              </p>
              {attribute.allowedValues?.length ? (
                <p>Allowed values: {attribute.allowedValues.join(", ")}</p>
              ) : null}
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No attribute definitions found.</div>
      )}
    </div>
  );
}
