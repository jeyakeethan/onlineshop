"use client";
import { useState } from "react";
type Props = {
  productId: number;
  variantId: number;
  active?: boolean;
  onUpdated: (variant: any) => void;
  onError: (message: string) => void;
};
export default function AdminVariantStatus({
  productId,
  variantId,
  active = true,
  onUpdated,
  onError,
}: Props) {
  const [saving, setSaving] = useState(false);
  async function change(status: string) {
    setSaving(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/products/${productId}/variants/${variantId}/status`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({ status }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update variant status."
            : data?.message || "Unable to update variant status.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to update variant status.",
      );
    } finally {
      setSaving(false);
    }
  }
  return (
    <label className="muted">
      Status
      <select
        value={active ? "ACTIVE" : "INACTIVE"}
        disabled={saving}
        onChange={(event) => change(event.target.value)}
      >
        <option value="ACTIVE">Active</option>
        <option value="INACTIVE">Inactive</option>
      </select>
    </label>
  );
}
