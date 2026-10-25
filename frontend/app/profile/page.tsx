'use client'
import React, { useEffect, useState } from 'react'

export default function ProfilePage() {
  const [user, setUser] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [status, setStatus] = useState<string | null>(null)

  useEffect(() => {
    fetch('/api/bff/users').then(async (r) => { if (r.ok) setUser(await r.json()); setLoading(false) }).catch(() => setLoading(false))
  }, [])

  async function save(e: React.FormEvent) {
    e.preventDefault()
    setStatus(null)
    try {
      const res = await fetch('/api/bff/users', { method: 'PUT', headers: { 'content-type': 'application/json' }, body: JSON.stringify(user) })
      if (res.ok) setStatus('Profile updated')
      else setStatus('Error')
    } catch (e) { setStatus('Network error') }
  }

  if (loading) return <div className="page-container"><div className="empty">Loading profile...</div></div>
  if (!user) return <div className="page-container"><div className="empty">No profile data</div></div>

  const canManageProducts = user.roles?.some((role: string) => ['ADMIN', 'MANAGER', 'ROLE_ADMIN', 'ROLE_MANAGER'].includes(role))

  return (
    <div className="page-container"><div className="page-heading"><div><p className="eyebrow">Your account</p><h1>Profile</h1></div></div><div className="account-grid"><section className="form-shell">
      <form onSubmit={save} className="space-y-4">
        <div>
          <label className="block text-sm">Email</label>
          <input value={user.email || ''} onChange={(e) => setUser({ ...user, email: e.target.value })} className="mt-1 block w-full border rounded px-3 py-2" />
        </div>
        <div>
          <label className="block text-sm">First name</label>
          <input value={user.firstName || ''} onChange={(e) => setUser({ ...user, firstName: e.target.value })} className="mt-1 block w-full border rounded px-3 py-2" />
        </div>
        <div>
          <label className="block text-sm">Last name</label>
          <input value={user.lastName || ''} onChange={(e) => setUser({ ...user, lastName: e.target.value })} className="mt-1 block w-full border rounded px-3 py-2" />
        </div>
        <div>
          <button className="w-full bg-green-600 text-white px-4 py-2 rounded">Save</button>
        </div>
        {status && <div className="text-sm mt-2">{status}</div>}
      </form>
    </section><nav className="account-links" aria-label="Account navigation"><a href="/orders"><span><strong>Order history</strong><small>Track current and past orders</small></span><span aria-hidden="true">→</span></a><a href="/cart"><span><strong>Shopping cart</strong><small>Review saved items</small></span><span aria-hidden="true">→</span></a>{canManageProducts && <><a href="/admin/products"><span><strong>Manage products</strong><small>Update catalog and inventory</small></span><span aria-hidden="true">→</span></a><a href="/admin/orders"><span><strong>Manage orders</strong><small>Review and update order status</small></span><span aria-hidden="true">→</span></a></>}</nav></div></div>
  )
}

