'use client'
import React, { useState } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/navigation'

export default function LoginPage() {
  const [userId, setUserId] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()

  async function submit(e: React.FormEvent) {
    e.preventDefault()
    setError(null)
    try {
      const res = await fetch('/api/auth', { method: 'POST', headers: { 'content-type': 'application/json' }, body: JSON.stringify({ userId, password }) })
      if (res.ok) {
        router.push('/profile')
      } else {
        const data = await res.json()
        setError(data?.message || 'Login failed')
      }
    } catch (err) {
      setError('Network error')
    }
  }

  return (
    <div className="form-page">
      <Link href="/products" className="back-link">Return to products</Link>
      <section className="form-shell">
        <p className="eyebrow">Arc / Form account</p>
        <h1>Welcome back.</h1>
        <p className="muted">Sign in to view your orders and saved details.</p>

        <form onSubmit={submit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium">Email</label>
            <input value={userId} onChange={(e) => setUserId(e.target.value)} className="mt-1 block w-full border rounded px-3 py-2" />
          </div>
          <div>
            <label className="block text-sm font-medium">Password</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} className="mt-1 block w-full border rounded px-3 py-2" />
          </div>
          {error && <div className="text-sm text-red-600">{error}</div>}
          <div>
            <button className="primary-button wide">Sign in</button>
          </div>
        </form>

        <div className="mt-4 text-sm flex items-center justify-between gap-3">
          <Link href="/auth/register" className="text-button">Create account</Link>
          <Link href="/auth/forgot-password" className="text-button">Forgot password?</Link>
        </div>
      </section>
    </div>
  )
}
