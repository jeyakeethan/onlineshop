"use client";
import { FormEvent, useState } from "react";
export default function AdminSettingsEditor({
  settings,
  onUpdated,
  onError,
}: {
  settings: Record<string, string | undefined>;
  onUpdated: (settings: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    storeName: settings.storeName || "",
    logoUrl: settings.logoUrl || "",
    contactEmail: settings.contactEmail || "",
    contactPhone: settings.contactPhone || "",
    currency: settings.currency || "USD",
    shippingPolicy: settings.shippingPolicy || "",
    returnPolicy: settings.returnPolicy || "",
    privacyPolicy: settings.privacyPolicy || "",
    termsPolicy: settings.termsPolicy || "",
  });
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch("/api/bff/admin/settings", {
        method: "PATCH",
        headers: { "content-type": "application/json" },
        body: JSON.stringify(form),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update settings."
            : data?.message || "Unable to update settings.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to update settings.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Store name
        <input
          maxLength={255}
          value={form.storeName}
          onChange={(e) => setForm({ ...form, storeName: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Logo URL
        <input
          maxLength={255}
          value={form.logoUrl}
          onChange={(e) => setForm({ ...form, logoUrl: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Contact email
        <input
          type="email"
          value={form.contactEmail}
          onChange={(e) => setForm({ ...form, contactEmail: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Currency
        <input
          pattern="[A-Za-z]{3}"
          value={form.currency}
          onChange={(e) =>
            setForm({ ...form, currency: e.target.value.toUpperCase() })
          }
          disabled={busy}
        />
      </label>
      <label>
        Shipping policy
        <textarea
          value={form.shippingPolicy}
          onChange={(e) => setForm({ ...form, shippingPolicy: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Return policy
        <textarea
          value={form.returnPolicy}
          onChange={(e) => setForm({ ...form, returnPolicy: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Privacy policy
        <textarea
          value={form.privacyPolicy}
          onChange={(e) => setForm({ ...form, privacyPolicy: e.target.value })}
          disabled={busy}
        />
      </label>
      <label>
        Terms policy
        <textarea
          value={form.termsPolicy}
          onChange={(e) => setForm({ ...form, termsPolicy: e.target.value })}
          disabled={busy}
        />
      </label>
      <button className="primary-button" disabled={busy}>
        {busy ? "Saving..." : "Save settings"}
      </button>
    </form>
  );
}
