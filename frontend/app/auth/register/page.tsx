'use client'
import React, { useState } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/navigation'

export default function RegisterPage() {
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    dob: '',
  })
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const router = useRouter()

  async function submit(e: React.FormEvent) {
    e.preventDefault()
    setError(null)

    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match.')
      return
    }

    setLoading(true)

    try {
      const res = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'content-type': 'application/json' },
        body: JSON.stringify({
          firstName: form.firstName,
          lastName: form.lastName,
          email: form.email,
          password: form.password,
          dob: form.dob,
        }),
      })

      if (!res.ok) {
        const data = await res.json().catch(() => ({}))
        setError(data?.message || 'Unable to create account.')
        return
      }

      router.push('/profile')
    } catch {
      setError('Network error while creating account.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="form-page">
      <Link href="/auth/login" className="back-link">Back to login</Link>
      <section className="form-shell">
        <p className="eyebrow">Create account</p>
        <h1>Join Arc / Form</h1>
        <p className="muted">Create your account to track orders and manage your profile.</p>

        <form onSubmit={submit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium">First name</label>
              <input value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} className="mt-1 block w-full border rounded px-3 py-2" required />
            </div>
            <div>
              <label className="block text-sm font-medium">Last name</label>
              <input value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} className="mt-1 block w-full border rounded px-3 py-2" required />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium">Email</label>
            <input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} className="mt-1 block w-full border rounded px-3 py-2" required />
          </div>

          <div>
            <label className="block text-sm font-medium">Date of birth</label>
            <input type="date" value={form.dob} onChange={(e) => setForm({ ...form, dob: e.target.value })} className="mt-1 block w-full border rounded px-3 py-2" />
          </div>

          <div>
            <label className="block text-sm font-medium">Password</label>
            <input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} className="mt-1 block w-full border rounded px-3 py-2" required />
          </div>

          <div>
            <label className="block text-sm font-medium">Confirm password</label>
            <input type="password" value={form.confirmPassword} onChange={(e) => setForm({ ...form, confirmPassword: e.target.value })} className="mt-1 block w-full border rounded px-3 py-2" required />
          </div>

          {error && <div className="text-sm text-red-600">{error}</div>}

          <button className="primary-button wide" disabled={loading}>{loading ? 'Creating account...' : 'Create account'}</button>
        </form>

        <p className="muted mt-4 text-sm">
          Already have an account? <Link href="/auth/login" className="text-button">Sign in</Link>
        </p>
      </section>
    </div>
  )
}
