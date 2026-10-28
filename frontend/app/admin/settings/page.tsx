"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Settings = {
  settingsId?: number;
  storeName?: string;
  logoUrl?: string;
  contactEmail?: string;
  contactPhone?: string;
  currency?: string;
  shippingPolicy?: string;
  returnPolicy?: string;
  privacyPolicy?: string;
  termsPolicy?: string;
};
export default function AdminSettingsPage() {
  const [settings, setSettings] = useState<Settings | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    fetch("/api/bff/admin/settings", { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access settings."
              : r.status === 403
                ? "You do not have permission to view settings."
                : d?.message || "Unable to load settings.",
          );
        setSettings(d);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load settings."),
      )
      .finally(() => setLoading(false));
  }, []);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading settings...</div>
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
          <p className="eyebrow">Store administration</p>
          <h1>Settings</h1>
        </div>
      </div>
      <section className="account-card">
        <p>Store: {settings?.storeName || "Not configured"}</p>
        <p>Currency: {settings?.currency || "Not configured"}</p>
        <p>
          Contact: {settings?.contactEmail || "Not configured"} ·{" "}
          {settings?.contactPhone || "Not configured"}
        </p>
        <p>Logo: {settings?.logoUrl || "Not configured"}</p>
      </section>
      <section className="inventory-list mt-6">
        <article className="inventory-product">
          <h2>Policies</h2>
          <p>Shipping: {settings?.shippingPolicy || "Not configured"}</p>
          <p>Returns: {settings?.returnPolicy || "Not configured"}</p>
          <p>Privacy: {settings?.privacyPolicy || "Not configured"}</p>
          <p>Terms: {settings?.termsPolicy || "Not configured"}</p>
        </article>
      </section>
    </div>
  );
}
