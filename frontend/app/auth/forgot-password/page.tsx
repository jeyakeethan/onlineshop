'use client'
import React, { useState } from 'react'
import Link from 'next/link'

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('')
  const [sent, setSent] = useState(false)

  async function submit(e: React.FormEvent) {
    e.preventDefault()
    setSent(true)
  }

  return (
    <div className="form-page">
      <Link href="/auth/login" className="back-link">Back to login</Link>
      <section className="form-shell">
        <p className="eyebrow">Need help signing in?</p>
        <h1>Reset your password</h1>
        <p className="muted">Enter the email associated with your account and we’ll send reset instructions.</p>

        {!sent ? (
          <form onSubmit={submit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium">Email</label>
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} className="mt-1 block w-full border rounded px-3 py-2" required />
            </div>
            <button className="primary-button wide">Send reset link</button>
          </form>
        ) : (
          <div className="status-box">
            <h2>Check your inbox</h2>
            <p className="muted">We sent a reset link to {email || 'your email'}.</p>
            <Link href="/auth/login" className="primary-button">Back to login</Link>
          </div>
        )}
      </section>
    </div>
  )
}
