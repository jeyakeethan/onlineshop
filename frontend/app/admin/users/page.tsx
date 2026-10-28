"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type User = {
  email?: string;
  firstName?: string;
  lastName?: string;
  roles?: string[];
  lastLoginTime?: string;
};
export default function AdminUsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    fetch(`/api/bff/admin/users?page=${page}&size=20`, { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access users."
              : r.status === 403
                ? "You do not have permission to view users."
                : d?.message || "Unable to load users.",
          );
        setUsers(d.content || []);
        setTotalPages(d.totalPages || 1);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load users."),
      )
      .finally(() => setLoading(false));
  }, [page]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading users...</div>
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
          <h1>Users</h1>
        </div>
      </div>
      {users.length ? (
        <div className="inventory-list">
          {users.map((user) => (
            <article className="inventory-product" key={user.email}>
              <h2>
                {user.firstName} {user.lastName}
              </h2>
              <p>{user.email}</p>
              <p className="muted">
                Roles: {user.roles?.join(", ") || "None"} · Last login{" "}
                {user.lastLoginTime || "Never"}
              </p>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">No users found.</div>
      )}
      <div className="page-actions">
        <button
          className="back-link"
          disabled={page === 0}
          onClick={() => setPage((current) => current - 1)}
        >
          Previous
        </button>
        <span>Page {page + 1}</span>
        <button
          className="back-link"
          disabled={page + 1 >= totalPages}
          onClick={() => setPage((current) => current + 1)}
        >
          Next
        </button>
      </div>
    </div>
  );
}
