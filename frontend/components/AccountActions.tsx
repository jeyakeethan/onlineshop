"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export function AccountActions() {
  const [loading, setLoading] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const router = useRouter();

  async function request(
    action: "revoke-all" | "deactivate" | "delete",
    label: string,
  ) {
    if (!window.confirm(`Are you sure you want to ${label}?`)) return;
    setLoading(action);
    setMessage(null);
    try {
      const response = await fetch(`/api/auth/account/${action}`, {
        method: "POST",
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        setMessage(data?.message || "The request could not be completed.");
        return;
      }
      setMessage(data?.message || "Request completed.");
      if (action === "revoke-all") {
        router.replace("/auth/login");
        router.refresh();
      }
    } catch {
      setMessage("Network error. Please try again.");
    } finally {
      setLoading(null);
    }
  }

  return (
    <div className="mt-6 space-y-2 border-t pt-4">
      <button
        type="button"
        className="text-button block"
        disabled={Boolean(loading)}
        onClick={() => request("revoke-all", "revoke all active sessions")}
      >
        {loading === "revoke-all"
          ? "Revoking sessions..."
          : "Revoke all sessions"}
      </button>
      <button
        type="button"
        className="text-button block"
        disabled={Boolean(loading)}
        onClick={() => request("deactivate", "request account deactivation")}
      >
        {loading === "deactivate"
          ? "Requesting..."
          : "Request account deactivation"}
      </button>
      <button
        type="button"
        className="text-button block text-red-700"
        disabled={Boolean(loading)}
        onClick={() => request("delete", "request account deletion")}
      >
        {loading === "delete" ? "Requesting..." : "Request account deletion"}
      </button>
      {message && (
        <p role="status" className="text-sm mt-2">
          {message}
        </p>
      )}
    </div>
  );
}
