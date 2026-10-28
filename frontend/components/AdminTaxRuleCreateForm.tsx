"use client";
import { FormEvent, useState } from "react";
export default function AdminTaxRuleCreateForm({
  onCreated,
  onMessage,
  onError,
}: {
  onCreated: (rule: unknown) => void;
  onMessage: (message: string) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    region: "",
    rate: "",
    taxInclusive: false,
    effectiveFrom: "",
    effectiveTo: "",
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    const rate = Number(form.rate);
    if (saving || !form.region.trim() || !Number.isFinite(rate) || rate < 0)
      return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch("/api/bff/admin/tax-rules", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          region: form.region.trim(),
          rate,
          taxInclusive: form.taxInclusive,
          effectiveFrom: form.effectiveFrom || null,
          effectiveTo: form.effectiveTo || null,
          active: false,
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to create tax rules."
            : data?.message || "Unable to create tax rule.",
        );
      onCreated(data);
      onMessage("Tax rule created successfully.");
      setForm({
        region: "",
        rate: "",
        taxInclusive: false,
        effectiveFrom: "",
        effectiveTo: "",
      });
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to create tax rule.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Create tax rule</h2>
      <label>
        Region
        <input
          required
          value={form.region}
          onChange={(e) => setForm({ ...form, region: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Rate
        <input
          required
          min="0"
          step="0.0001"
          type="number"
          value={form.rate}
          onChange={(e) => setForm({ ...form, rate: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        <input
          type="checkbox"
          checked={form.taxInclusive}
          onChange={(e) => setForm({ ...form, taxInclusive: e.target.checked })}
          disabled={saving}
        />{" "}
        Tax inclusive
      </label>
      <label>
        Effective from
        <input
          type="datetime-local"
          value={form.effectiveFrom}
          onChange={(e) => setForm({ ...form, effectiveFrom: e.target.value })}
          disabled={saving}
        />
      </label>
      <label>
        Effective to
        <input
          type="datetime-local"
          value={form.effectiveTo}
          onChange={(e) => setForm({ ...form, effectiveTo: e.target.value })}
          disabled={saving}
        />
      </label>
      <button
        className="primary-button"
        disabled={saving || !form.region.trim() || !form.rate}
      >
        {saving ? "Creating..." : "Create tax rule"}
      </button>
    </form>
  );
}
