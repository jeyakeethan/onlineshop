"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";

export default function ResetPasswordPage() {
  const [email, setEmail] = useState("");
  const [token, setToken] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    setEmail(params.get("email") || "");
    setToken(params.get("token") || "");
  }, []);

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    setError(null);

    if (password.length < 8) {
      setError("Password must be at least 8 characters long.");
      return;
    }
    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }
    if (!email.trim() || !token.trim()) {
      setError("Use the reset link from your email to reset your password.");
      return;
    }

    setLoading(true);
    try {
      const response = await fetch("/api/auth/reset-password", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          email: email.trim(),
          token: token.trim(),
          password,
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        setError(
          data?.message ||
            "Unable to reset your password. The link may be invalid or expired.",
        );
        return;
      }
      setSuccess(true);
    } catch {
      setError(
        "Network error while resetting your password. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="form-page">
      <Link href="/auth/login" className="back-link">
        Back to login
      </Link>
      <section className="form-shell">
        <p className="eyebrow">Account security</p>
        <h1>Choose a new password</h1>
        {success ? (
          <div className="status-box">
            <p role="status" className="muted">
              Your password has been reset successfully.
            </p>
            <Link href="/auth/login" className="primary-button">
              Sign in
            </Link>
          </div>
        ) : (
          <form onSubmit={submit} className="space-y-4">
            <div>
              <label
                htmlFor="reset-email"
                className="block text-sm font-medium"
              >
                Email
              </label>
              <input
                id="reset-email"
                name="email"
                type="email"
                autoComplete="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                className="mt-1 block w-full border rounded px-3 py-2"
                required
              />
            </div>
            <div>
              <label
                htmlFor="reset-token"
                className="block text-sm font-medium"
              >
                Reset token
              </label>
              <input
                id="reset-token"
                name="token"
                type="text"
                value={token}
                onChange={(event) => setToken(event.target.value)}
                className="mt-1 block w-full border rounded px-3 py-2"
                required
              />
            </div>
            <div>
              <label
                htmlFor="reset-password"
                className="block text-sm font-medium"
              >
                New password
              </label>
              <input
                id="reset-password"
                name="password"
                type="password"
                autoComplete="new-password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                className="mt-1 block w-full border rounded px-3 py-2"
                minLength={8}
                required
              />
            </div>
            <div>
              <label
                htmlFor="reset-confirm-password"
                className="block text-sm font-medium"
              >
                Confirm new password
              </label>
              <input
                id="reset-confirm-password"
                name="confirmPassword"
                type="password"
                autoComplete="new-password"
                value={confirmPassword}
                onChange={(event) => setConfirmPassword(event.target.value)}
                className="mt-1 block w-full border rounded px-3 py-2"
                minLength={8}
                required
              />
            </div>
            {error && (
              <div role="alert" className="text-sm text-red-600">
                {error}
              </div>
            )}
            <button
              type="submit"
              className="primary-button wide"
              disabled={loading}
            >
              {loading ? "Resetting password..." : "Reset password"}
            </button>
          </form>
        )}
      </section>
    </div>
  );
}
