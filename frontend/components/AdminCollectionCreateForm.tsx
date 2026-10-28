"use client";
import { FormEvent, useState } from "react";
export default function AdminCollectionCreateForm({
  onCreated,
  onMessage,
  onError,
}: {
  onCreated: (collection: any) => void;
  onMessage: (message: string) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    name: "",
    slug: "",
    displayOrder: "0",
    productIds: "",
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (saving || !form.name.trim() || !form.slug.trim()) return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch("/api/bff/admin/collections", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          name: form.name.trim(),
          slug: form.slug.trim(),
          active: false,
          displayOrder: Number(form.displayOrder),
          productIds: form.productIds
            .split(",")
            .map((value) => Number(value.trim()))
            .filter((value) => Number.isInteger(value) && value > 0),
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to create collections."
            : data?.message || "Unable to create collection.",
        );
      onCreated(data);
      setForm({ name: "", slug: "", displayOrder: "0", productIds: "" });
      onMessage("Collection created successfully.");
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to create collection.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Create collection</h2>
      <label>
        Name
        <input
          required
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Slug
        <input
          required
          value={form.slug}
          onChange={(e) => setForm({ ...form, slug: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Display order
        <input
          type="number"
          min="0"
          value={form.displayOrder}
          onChange={(e) => setForm({ ...form, displayOrder: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Product IDs
        <input
          placeholder="1, 2, 3"
          value={form.productIds}
          onChange={(e) => setForm({ ...form, productIds: e.target.value })}
          disabled={saving}
        />
      </label>
      <button
        className="primary-button"
        disabled={saving || !form.name.trim() || !form.slug.trim()}
      >
        {saving ? "Creating..." : "Create collection"}
      </button>
    </form>
  );
}
