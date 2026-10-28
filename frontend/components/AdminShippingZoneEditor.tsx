"use client";
import { FormEvent, useState } from "react";
export default function AdminShippingZoneEditor({
  zone,
  onUpdated,
  onError,
}: {
  zone: {
    zoneId: number;
    regionCodes?: string[];
    postalCodePrefixes?: string[];
    priority?: number;
    active?: boolean;
  };
  onUpdated: (zone: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    regions: (zone.regionCodes || []).join(", "),
    prefixes: (zone.postalCodePrefixes || []).join(", "),
    priority: String(zone.priority ?? 0),
    active: Boolean(zone.active),
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (saving) return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/shipping-zones/${zone.zoneId}`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({
            regionCodes: form.regions
              .split(",")
              .map((v) => v.trim())
              .filter(Boolean),
            postalCodePrefixes: form.prefixes
              .split(",")
              .map((v) => v.trim())
              .filter(Boolean),
            priority: Number(form.priority),
            active: form.active,
          }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update shipping zones."
            : data?.message || "Unable to update shipping zone.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to update shipping zone.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Region codes
        <input
          value={form.regions}
          onChange={(e) => setForm({ ...form, regions: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Postal-code prefixes
        <input
          value={form.prefixes}
          onChange={(e) => setForm({ ...form, prefixes: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Priority
        <input
          min="0"
          type="number"
          value={form.priority}
          onChange={(e) => setForm({ ...form, priority: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        <input
          type="checkbox"
          checked={form.active}
          onChange={(e) => setForm({ ...form, active: e.target.checked })}
          disabled={saving}
        />{" "}
        Active
      </label>
      <button className="primary-button" disabled={saving}>
        {saving ? "Saving..." : "Save zone"}
      </button>
    </form>
  );
}
