"use client";
import React, { useEffect, useState } from "react";
import { LogoutButton } from "../../components/LogoutButton";
import { AccountActions } from "../../components/AccountActions";
import { AddressBook } from "../../components/AddressBook";

export default function ProfilePage() {
  const [user, setUser] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [status, setStatus] = useState<string | null>(null);

  useEffect(() => {
    fetch("/api/bff/users", { cache: "no-store" })
      .then(async (response) => {
        const data = await response.json().catch(() => ({}));
        if (response.ok) setUser(data);
        else
          setLoadError(
            response.status === 401 || response.status === 403
              ? "Please sign in to view your profile."
              : data?.message || "Unable to load your profile.",
          );
        setLoading(false);
      })
      .catch(() => {
        setLoadError("Network error while loading your profile.");
        setLoading(false);
      });
  }, []);

  async function save(e: React.FormEvent) {
    e.preventDefault();
    setStatus(null);
    setSaving(true);
    try {
      const res = await fetch("/api/bff/users", {
        method: "PATCH",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          email: user.email,
          firstName: user.firstName,
          lastName: user.lastName,
          dob: user.dob,
        }),
      });
      if (res.ok) setStatus("Profile updated");
      else {
        const data = await res.json().catch(() => ({}));
        setStatus(data?.message || "Unable to update your profile.");
      }
    } catch (e) {
      setStatus("Network error");
    } finally {
      setSaving(false);
    }
  }

  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading profile...</div>
      </div>
    );
  if (!user)
    return (
      <div className="page-container">
        <div role="alert" className="empty">
          {loadError || "No profile data is available."}
        </div>
      </div>
    );

  const canManageProducts = user.roles?.some((role: string) =>
    ["ADMIN", "MANAGER", "ROLE_ADMIN", "ROLE_MANAGER"].includes(role),
  );

  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Your account</p>
          <h1>Profile</h1>
        </div>
      </div>
      <AddressBook />
      <div className="account-grid">
        <section className="form-shell">
          <form onSubmit={save} className="space-y-4">
            <div>
              <label className="block text-sm">Email</label>
              <input
                value={user.email || ""}
                onChange={(e) => setUser({ ...user, email: e.target.value })}
                className="mt-1 block w-full border rounded px-3 py-2"
              />
            </div>
            <div>
              <label className="block text-sm">First name</label>
              <input
                value={user.firstName || ""}
                onChange={(e) =>
                  setUser({ ...user, firstName: e.target.value })
                }
                className="mt-1 block w-full border rounded px-3 py-2"
              />
            </div>
            <div>
              <label className="block text-sm">Last name</label>
              <input
                value={user.lastName || ""}
                onChange={(e) => setUser({ ...user, lastName: e.target.value })}
                className="mt-1 block w-full border rounded px-3 py-2"
              />
            </div>
            <div>
              <button
                type="submit"
                disabled={saving}
                className="w-full bg-green-600 text-white px-4 py-2 rounded"
              >
                {saving ? "Saving..." : "Save"}
              </button>
            </div>
            {status && <div className="text-sm mt-2">{status}</div>}
            <LogoutButton />
            <AccountActions />
          </form>
        </section>
        <nav className="account-links" aria-label="Account navigation">
          <a href="/orders">
            <span>
              <strong>Order history</strong>
              <small>Track current and past orders</small>
            </span>
            <span aria-hidden="true">→</span>
          </a>
          <a href="/cart">
            <span>
              <strong>Shopping cart</strong>
              <small>Review saved items</small>
            </span>
            <span aria-hidden="true">→</span>
          </a>
          {canManageProducts && (
            <>
              <a href="/admin/products">
                <span>
                  <strong>Manage products</strong>
                  <small>Update catalog and inventory</small>
                </span>
                <span aria-hidden="true">→</span>
              </a>
              <a href="/admin/orders">
                <span>
                  <strong>Manage orders</strong>
                  <small>Review and update order status</small>
                </span>
                <span aria-hidden="true">→</span>
              </a>
            </>
          )}
        </nav>
      </div>
    </div>
  );
}
