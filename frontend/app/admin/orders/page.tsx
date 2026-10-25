'use client'

import Link from 'next/link'
import { useEffect, useState } from 'react'
import { OrderDTO } from '../../../types/api'
import { formatMoney } from '../../../lib/storefront'

const statuses = ['PENDING', 'FULFILLED', 'PAID', 'FAILED', 'CANCELLED']

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState<OrderDTO[]>([])
  const [message, setMessage] = useState('Loading orders...')

  async function load() {
    const response = await fetch('/api/orders/admin', { cache: 'no-store' })
    if (!response.ok) throw new Error()
    setOrders(await response.json())
    setMessage('')
  }

  useEffect(() => { load().catch(() => setMessage('Could not load orders.')) }, [])

  async function updateStatus(orderId: number | undefined, status: string) {
    if (!orderId) return
    setMessage('Updating order...')
    const response = await fetch(`/api/orders/admin/${orderId}/status`, { method: 'PATCH', headers: { 'content-type': 'application/json' }, body: JSON.stringify({ status }) })
    if (!response.ok) { setMessage('Could not update order status.'); return }
    await load()
  }

  return <div className="page-container"><div className="page-heading"><div><p className="eyebrow">Operations</p><h1>Orders</h1></div><Link href="/admin/products" className="back-link">Inventory</Link></div>{message && <p className="status">{message}</p>}<div className="orders-list">{orders.map((order) => <article className="order-row" key={order.orderId}><div><p className="eyebrow">Order #{order.orderId}</p><h2>{formatMoney(order.totalPrice || 0)}</h2><p className="muted">{order.orderDate || 'Recent order'} · {order.orderItems?.length || 0} lines</p></div><div className="order-meta"><span>Status</span><select value={order.orderStatus || 'PENDING'} onChange={(event) => updateStatus(order.orderId, event.target.value)}>{statuses.map((status) => <option key={status}>{status}</option>)}</select><Link href={`/orders/${order.orderId}`} className="back-link">View order</Link></div></article>)}{!message && !orders.length && <div className="empty">No orders found.</div>}</div></div>
}
