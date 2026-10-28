"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Role = { id?: number; name?: string; permissions?: string[] };
export default function AdminRolesPage() {
  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    fetch("/api/bff/admin/roles", { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access roles."
              : r.status === 403
                ? "You do not have permission to view roles."
                : d?.message || "Unable to load roles.",
          );
        setRoles(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load roles."),
      )
      .finally(() => setLoading(false));
  }, []);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading roles...</div>
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
          <p className="eyebrow">Identity administration</p>
          <h1>Roles</h1>
        </div>
        <Link href="/admin/users" className="back-link">
          Users
        </Link>
      </div>
      {roles.length ? (
        <div className="inventory-list">
          {roles.map((role) => (
            <article className="inventory-product" key={role.id}>
              <h2>{role.name}</h2>
              <p className="muted">
                Role #{role.id} · {role.permissions?.length || 0} permissions
              </p>
              <p>{role.permissions?.join(", ") || "No permissions"}</p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No roles found.</div>
      )}
    </div>
  );
}
