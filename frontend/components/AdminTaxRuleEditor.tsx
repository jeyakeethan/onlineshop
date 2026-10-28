"use client";
import { FormEvent, useState } from "react";
export default function AdminTaxRuleEditor({
  rule,
  onUpdated,
  onError,
}: {
  rule: {
    ruleId: number;
    rate?: number;
    taxInclusive?: boolean;
    effectiveFrom?: string;
    effectiveTo?: string;
    active?: boolean;
  };
  onUpdated: (rule: unknown) => void;
  onError: (message: string) => void;
}) {
  const [form, setForm] = useState({
    rate: String(rule.rate ?? 0),
    taxInclusive: Boolean(rule.taxInclusive),
    effectiveFrom: rule.effectiveFrom || "",
    effectiveTo: rule.effectiveTo || "",
    active: Boolean(rule.active),
  });
  const [saving, setSaving] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    const rate = Number(form.rate);
    if (saving || !Number.isFinite(rate) || rate < 0) return;
    setSaving(true);
    onError("");
    try {
      const response = await fetch(`/api/bff/admin/tax-rules/${rule.ruleId}`, {
        method: "PATCH",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          ...form,
          rate,
          effectiveFrom: form.effectiveFrom || null,
          effectiveTo: form.effectiveTo || null,
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update tax rules."
            : data?.message || "Unable to update tax rule.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to update tax rule.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
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
        {saving ? "Saving..." : "Save tax rule"}
      </button>
    </form>
  );
}
