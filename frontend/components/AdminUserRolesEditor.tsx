"use client";
import { FormEvent, useState } from "react";
export default function AdminUserRolesEditor({
  userId,
  roles,
  onUpdated,
  onError,
}: {
  userId: string;
  roles?: string[];
  onUpdated: (user: unknown) => void;
  onError: (message: string) => void;
}) {
  const [value, setValue] = useState((roles || []).join(", "));
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    const nextRoles = value
      .split(",")
      .map((role) => role.trim())
      .filter(Boolean);
    if (
      busy ||
      !nextRoles.length ||
      !window.confirm("Update this user's roles?")
    )
      return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(
        `/api/bff/admin/users/${encodeURIComponent(userId)}/roles`,
        {
          method: "PATCH",
          headers: { "content-type": "application/json" },
          body: JSON.stringify({ roles: nextRoles }),
        },
      );
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to assign roles."
            : data?.message || "Unable to update roles.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to update roles.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <label>
        Roles
        <input
          value={value}
          onChange={(e) => setValue(e.target.value)}
          disabled={busy}
          placeholder="ROLE_USER, ROLE_MANAGER"
        />
      </label>
      <button className="primary-button" disabled={busy || !value.trim()}>
        {busy ? "Saving..." : "Save roles"}
      </button>
    </form>
  );
}
