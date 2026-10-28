"use client";

import { FormEvent, useEffect, useState } from "react";

type Address = {
  id?: number;
  street: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
};
const emptyAddress: Address = {
  street: "",
  city: "",
  state: "",
  country: "",
  postalCode: "",
};

export function AddressBook() {
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [form, setForm] = useState<Address>(emptyAddress);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  async function load() {
    try {
      const response = await fetch("/api/bff/addresses", { cache: "no-store" });
      const data = await response.json().catch(() => []);
      if (!response.ok)
        throw new Error(data?.message || "Unable to load addresses.");
      setAddresses(Array.isArray(data) ? data : []);
    } catch (error) {
      setMessage(
        error instanceof Error ? error.message : "Unable to load addresses.",
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function save(event: FormEvent) {
    event.preventDefault();
    setSaving(true);
    setMessage(null);
    try {
      const response = await fetch(
        editingId ? `/api/bff/addresses/${editingId}` : "/api/bff/addresses",
        {
          method: editingId ? "PATCH" : "POST",
          headers: { "content-type": "application/json" },
          body: JSON.stringify(form),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(data?.message || "Unable to save address.");
      setForm(emptyAddress);
      setEditingId(null);
      await load();
      setMessage("Address saved.");
    } catch (error) {
      setMessage(
        error instanceof Error ? error.message : "Unable to save address.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function remove(id: number) {
    if (!window.confirm("Delete this address?")) return;
    const response = await fetch(`/api/bff/addresses/${id}`, {
      method: "DELETE",
    });
    if (response.ok) {
      setAddresses((items) => items.filter((item) => item.id !== id));
      setMessage("Address deleted.");
    } else setMessage("Unable to delete address.");
  }

  async function makeDefault(id: number) {
    const response = await fetch(`/api/bff/addresses/${id}`, { method: "PUT" });
    if (response.ok) setMessage("Default address updated.");
    else setMessage("Unable to set the default address.");
  }

  return (
    <section className="form-shell mt-6">
      <p className="eyebrow">Delivery</p>
      <h2>Saved addresses</h2>
      {loading ? (
        <p className="muted">Loading addresses...</p>
      ) : addresses.length === 0 ? (
        <p className="muted">No saved addresses yet.</p>
      ) : (
        <div className="space-y-3">
          {addresses.map((address) => (
            <div key={address.id} className="border rounded p-3">
              <p>
                {address.street}, {address.city}
              </p>
              <p className="muted">
                {address.state}, {address.country} {address.postalCode}
              </p>
              <div className="mt-2 flex gap-3">
                <button
                  type="button"
                  className="text-button"
                  onClick={() => {
                    setEditingId(address.id || null);
                    setForm(address);
                  }}
                >
                  Edit
                </button>
                <button
                  type="button"
                  className="text-button"
                  onClick={() => address.id && makeDefault(address.id)}
                >
                  Set default
                </button>
                <button
                  type="button"
                  className="text-button text-red-700"
                  onClick={() => address.id && remove(address.id)}
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
      <form onSubmit={save} className="mt-4 space-y-3">
        <input
          aria-label="Street"
          placeholder="Street"
          value={form.street}
          onChange={(e) => setForm({ ...form, street: e.target.value })}
          className="block w-full border rounded px-3 py-2"
          required
        />
        <div className="grid grid-cols-2 gap-3">
          <input
            aria-label="City"
            placeholder="City"
            value={form.city}
            onChange={(e) => setForm({ ...form, city: e.target.value })}
            className="border rounded px-3 py-2"
            required
          />
          <input
            aria-label="State"
            placeholder="State"
            value={form.state}
            onChange={(e) => setForm({ ...form, state: e.target.value })}
            className="border rounded px-3 py-2"
            required
          />
          <input
            aria-label="Country"
            placeholder="Country"
            value={form.country}
            onChange={(e) => setForm({ ...form, country: e.target.value })}
            className="border rounded px-3 py-2"
            required
          />
          <input
            aria-label="Postal code"
            placeholder="Postal code"
            value={form.postalCode}
            onChange={(e) => setForm({ ...form, postalCode: e.target.value })}
            className="border rounded px-3 py-2"
            required
          />
        </div>
        <button type="submit" className="primary-button" disabled={saving}>
          {saving ? "Saving..." : editingId ? "Update address" : "Add address"}
        </button>
      </form>
      {message && (
        <p role="status" className="text-sm mt-3">
          {message}
        </p>
      )}
    </section>
  );
}
