"use client";
import React, { useState } from "react";
import Link from "next/link";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [sent, setSent] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await fetch("/api/auth/forgot-password", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ email: email.trim() }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        setError(
          data?.message || "Unable to process the request. Please try again.",
        );
        return;
      }
      setSent(true);
    } catch {
      setError(
        "Network error while requesting a reset link. Please try again.",
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
        <p className="eyebrow">Need help signing in?</p>
        <h1>Reset your password</h1>
        <p className="muted">
          Enter the email associated with your account and we’ll send reset
          instructions.
        </p>

        {!sent ? (
          <form onSubmit={submit} className="space-y-4">
            <div>
              <label
                htmlFor="forgot-email"
                className="block text-sm font-medium"
              >
                Email
              </label>
              <input
                id="forgot-email"
                name="email"
                type="email"
                autoComplete="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="mt-1 block w-full border rounded px-3 py-2"
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
              {loading ? "Sending..." : "Send reset link"}
            </button>
          </form>
        ) : (
          <div className="status-box">
            <h2>Check your inbox</h2>
            <p role="status" className="muted">
              If an account exists for that email, reset instructions will be
              sent.
            </p>
            <Link href="/auth/login" className="primary-button">
              Back to login
            </Link>
          </div>
        )}
      </section>
    </div>
  );
}
