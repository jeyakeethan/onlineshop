"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";

export default function VerifyEmailPage() {
  const [email, setEmail] = useState("");
  const [token, setToken] = useState("");
  const [loading, setLoading] = useState(false);
  const [resendLoading, setResendLoading] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [resendMessage, setResendMessage] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    setEmail(params.get("email") || "");
    setToken(params.get("token") || "");
  }, []);

  useEffect(() => {
    if (resendCooldown <= 0) return;
    const timer = window.setInterval(
      () => setResendCooldown((value) => Math.max(0, value - 1)),
      1000,
    );
    return () => window.clearInterval(timer);
  }, [resendCooldown]);

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    setError(null);

    if (!email.trim() || !token.trim()) {
      setError("Enter the email and token from your verification email.");
      return;
    }

    setLoading(true);
    try {
      const response = await fetch("/api/auth/verify-email", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ email: email.trim(), token: token.trim() }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        setError(
          data?.message || "This verification token is invalid or expired.",
        );
        return;
      }
      setSuccess(true);
    } catch {
      setError("Network error while verifying your email. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  async function resendVerification() {
    setError(null);
    setResendMessage(null);
    if (!email.trim()) {
      setError("Enter your email before requesting a new verification email.");
      return;
    }
    if (resendCooldown > 0) return;

    setResendLoading(true);
    try {
      const response = await fetch("/api/auth/verification/resend", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ email: email.trim() }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        setError(data?.message || "Unable to resend the verification email.");
        return;
      }
      setResendMessage(
        data?.message ||
          "If the account exists and is not verified, a new email has been queued.",
      );
      setResendCooldown(60);
    } catch {
      setError("Network error while requesting a new verification email.");
    } finally {
      setResendLoading(false);
    }
  }

  return (
    <div className="form-page">
      <Link href="/auth/login" className="back-link">
        Back to login
      </Link>
      <section className="form-shell">
        <p className="eyebrow">Account verification</p>
        <h1>Verify your email</h1>
        {success ? (
          <div className="status-box">
            <p role="status" className="muted">
              Your email has been verified. You can now sign in.
            </p>
            <Link href="/auth/login" className="primary-button">
              Sign in
            </Link>
          </div>
        ) : (
          <form onSubmit={submit} className="space-y-4">
            <div>
              <label
                htmlFor="verify-email"
                className="block text-sm font-medium"
              >
                Email
              </label>
              <input
                id="verify-email"
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
                htmlFor="verify-token"
                className="block text-sm font-medium"
              >
                Verification token
              </label>
              <input
                id="verify-token"
                name="token"
                type="text"
                value={token}
                onChange={(event) => setToken(event.target.value)}
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
              {loading ? "Verifying..." : "Verify email"}
            </button>
            <button
              type="button"
              className="text-button"
              onClick={resendVerification}
              disabled={resendLoading || resendCooldown > 0}
            >
              {resendLoading
                ? "Sending new email..."
                : resendCooldown > 0
                  ? `Resend available in ${resendCooldown}s`
                  : "Resend verification email"}
            </button>
            {resendMessage && (
              <div role="status" className="text-sm text-green-700">
                {resendMessage}
              </div>
            )}
          </form>
        )}
      </section>
    </div>
  );
}
