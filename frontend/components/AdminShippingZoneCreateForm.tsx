"use client";
import { FormEvent, useState } from "react";
export default function AdminShippingZoneCreateForm({
  onCreated,
  onMessage,
  onError,
}: {
  onCreated: (zone: unknown) => void;
  onMessage: (message: string) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    name: "",
    regions: "",
    prefixes: "",
    priority: "0",
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (saving || !form.name.trim()) return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch("/api/bff/admin/shipping-zones", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          name: form.name.trim(),
          regionCodes: form.regions
            .split(",")
            .map((v) => v.trim())
            .filter(Boolean),
          postalCodePrefixes: form.prefixes
            .split(",")
            .map((v) => v.trim())
            .filter(Boolean),
          priority: Number(form.priority),
          active: false,
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to create shipping zones."
            : data?.message || "Unable to create shipping zone.",
        );
      onCreated(data);
      onMessage("Shipping zone created successfully.");
      setForm({ name: "", regions: "", prefixes: "", priority: "0" });
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to create shipping zone.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Create shipping zone</h2>
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
        Region codes
        <input
          placeholder="US, CA"
          value={form.regions}
          onChange={(e) => setForm({ ...form, regions: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Postal-code prefixes
        <input
          placeholder="10, 20"
          value={form.prefixes}
          onChange={(e) => setForm({ ...form, prefixes: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Priority
        <input
          type="number"
          min="0"
          value={form.priority}
          onChange={(e) => setForm({ ...form, priority: e.target.value })}
          disabled={saving}
        />
      </label>
      <button className="primary-button" disabled={saving || !form.name.trim()}>
        {saving ? "Creating..." : "Create zone"}
      </button>
    </form>
  );
}
