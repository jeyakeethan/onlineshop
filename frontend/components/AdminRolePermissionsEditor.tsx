"use client";
import { FormEvent, useState } from "react";
export default function AdminRolePermissionsEditor({
  role,
  onUpdated,
  onError,
}: {
  role: { id: number; permissions?: string[] };
  onUpdated: (role: unknown) => void;
  onError: (message: string) => void;
}) {
  const [value, setValue] = useState((role.permissions || []).join(", "));
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    const permissions = value
      .split(",")
      .map((permission) => permission.trim())
      .filter(Boolean);
    if (busy || !window.confirm("Update this role's permissions?")) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/roles/${role.id}/permissions`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({ permissions }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to update roles."
            : data?.message || "Unable to update role permissions.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error
          ? reason.message
          : "Unable to update role permissions.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Permissions
        <input
          value={value}
          onChange={(e) => setValue(e.target.value)}
          disabled={busy}
          placeholder="user:read, order:read"
        />
      </label>
      <button className="primary-button" disabled={busy}>
        {busy ? "Saving..." : "Save permissions"}
      </button>
    </form>
  );
}
