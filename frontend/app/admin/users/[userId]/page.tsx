"use client";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
type User = {
  email?: string;
  firstName?: string;
  lastName?: string;
  dob?: string;
  roles?: string[];
  lastLoginTime?: string;
};
export default function AdminUserDetailPage() {
  const { userId } = useParams<{ userId: string }>();
  const [user, setUser] = useState<User | null>(null);
  const [message, setMessage] = useState("Loading user...");
  useEffect(() => {
    fetch(`/api/bff/admin/users/${encodeURIComponent(userId)}`, {
      cache: "no-store",
    })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access users."
              : r.status === 403
                ? "You do not have permission to view this user."
                : d?.message || "User not found.",
          );
        setUser(d);
        setMessage("");
      })
      .catch((e) =>
        setMessage(e instanceof Error ? e.message : "Unable to load user."),
      );
  }, [userId]);
  if (!user)
    return (
      <div className="page-container">
        <div className="empty">{message}</div>
      </div>
    );
  return (
    <div className="page-container">
      <Link href="/admin/users" className="back-link">
        Back to users
      </Link>
      <div className="page-heading">
        <div>
          <p className="eyebrow">Admin user detail</p>
          <h1>
            {user.firstName} {user.lastName}
          </h1>
          <p className="muted">{user.email}</p>
        </div>
      </div>
      <section className="account-card">
        <p>Roles: {user.roles?.join(", ") || "None"}</p>
        <p>Date of birth: {user.dob || "Not provided"}</p>
        <p>Last login: {user.lastLoginTime || "Never"}</p>
      </section>
    </div>
  );
}
