"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export function LogoutButton() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  async function logout() {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch("/api/auth/logout", { method: "POST" });
      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        setError(data?.message || "Unable to sign out. Please try again.");
        return;
      }

      router.replace("/auth/login");
      router.refresh();
    } catch {
      setError("Network error while signing out. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="mt-4">
      <button
        type="button"
        className="text-button"
        onClick={logout}
        disabled={loading}
      >
        {loading ? "Signing out..." : "Sign out"}
      </button>
      {error && (
        <p role="alert" className="mt-2 text-sm text-red-600">
          {error}
        </p>
      )}
    </div>
  );
}
