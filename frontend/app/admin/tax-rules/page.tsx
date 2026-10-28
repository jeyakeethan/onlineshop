"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Rule = {
  ruleId?: number;
  region?: string;
  rate?: number;
  taxInclusive?: boolean;
  effectiveFrom?: string;
  effectiveTo?: string;
  active?: boolean;
};
export default function AdminTaxRulesPage() {
  const [rules, setRules] = useState<Rule[]>([]);
  const [region, setRegion] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    const query = region ? `?region=${encodeURIComponent(region)}` : "";
    fetch(`/api/bff/admin/tax-rules${query}`, { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access tax rules."
              : r.status === 403
                ? "You do not have permission to view tax rules."
                : d?.message || "Unable to load tax rules.",
          );
        setRules(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load tax rules."),
      )
      .finally(() => setLoading(false));
  }, [region]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading tax rules...</div>
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
          <p className="eyebrow">Pricing administration</p>
          <h1>Tax rules</h1>
        </div>
        <Link href="/admin/products" className="back-link">
          Back to catalog
        </Link>
      </div>
      <label>
        Region filter
        <input
          value={region}
          onChange={(e) => setRegion(e.target.value)}
          placeholder="Country or region"
        />
      </label>
      {rules.length ? (
        <div className="inventory-list">
          {rules.map((rule) => (
            <article className="inventory-product" key={rule.ruleId}>
              <h2>{rule.region}</h2>
              <p className="muted">
                Rate {rule.rate ?? 0} ·{" "}
                {rule.taxInclusive ? "Tax inclusive" : "Tax exclusive"} ·{" "}
                {rule.active ? "Active" : "Inactive"}
              </p>
              <p>
                {rule.effectiveFrom || "No start"} –{" "}
                {rule.effectiveTo || "No end"}
              </p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No tax rules found.</div>
      )}
    </div>
  );
}
