'use client'

import Link from 'next/link'
import { useEffect, useState } from 'react'
import { OrderDTO, UserDTO } from '../types/api'
import { formatMoney } from '../lib/storefront'

function request(path: string, init?: RequestInit) {
  return fetch(path, init).then(async (response) => {
    if (!response.ok) throw new Error(String(response.status))
    const text = await response.text()
    if (!text) return null
    try { return JSON.parse(text) } catch { return text }
  })
}

const orderStages = ['PENDING', 'FULFILLED', 'PAID']

function statusLabel(status?: string) {
  return status === 'PENDING' ? 'Processing' : status === 'FULFILLED' ? 'Reserved' : status === 'PAID' ? 'Paid' : status || 'Processing'
}

function StatusTracker({ status }: { status?: string }) {
  if (status === 'FAILED' || status === 'CANCELLED') return <p className="order-status-failed">{status === 'FAILED' ? 'Payment or fulfillment failed.' : 'This order was cancelled.'}</p>
  const activeIndex = Math.max(0, orderStages.indexOf(status || 'PENDING'))
  return <div className="status-tracker" aria-label={`Order status: ${statusLabel(status)}`}>{orderStages.map((stage, index) => <div className={index <= activeIndex ? 'status-step active' : 'status-step'} key={stage}><span>{index + 1}</span><small>{statusLabel(stage)}</small></div>)}</div>
}

export function OrdersView() {
  const [orders, setOrders] = useState<OrderDTO[]>([])
  const [message, setMessage] = useState('Loading your orders...')

  useEffect(() => {
    request('/api/bff/users').then((user: UserDTO) => request(`/api/orders/users/${encodeURIComponent(user.email)}/history`)).then((data: OrderDTO[]) => { setOrders(data); setMessage('') }).catch(() => setMessage('We could not load your orders.'))
  }, [])

  return <div className="page-container"><div className="page-heading"><div><p className="eyebrow">Your account</p><h1>Orders</h1></div><Link href="/products" className="back-link">Shop collection</Link></div>{message ? <div className="empty">{message}</div> : orders.length ? <div className="orders-list">{orders.map((order) => <Link className="order-row" href={`/orders/${order.orderId}`} key={order.orderId}><div><p className="eyebrow">Order #{order.orderId}</p><h2>{order.orderStatus || 'Processing'}</h2><p className="muted">{order.orderDate || 'Recent order'}</p></div><div className="order-meta"><strong>{formatMoney(order.totalPrice || 0)}</strong><span>{order.orderItems?.length || 0} items</span></div></Link>)}</div> : <div className="empty"><h2>No orders yet.</h2><Link href="/products" className="primary-button">Explore the collection</Link></div>}</div>
}

export function OrderDetailView({ id }: { id: string }) {
  const [order, setOrder] = useState<OrderDTO | null>(null)
  const [message, setMessage] = useState('Loading order...')

  useEffect(() => {
    let timer: number | undefined
    const load = () => request(`/api/orders/${id}`).then((data: OrderDTO) => {
      setOrder(data)
      setMessage('')
      if (data.orderStatus === 'PENDING' || data.orderStatus === 'FULFILLED') timer = window.setTimeout(load, 3000)
    }).catch(() => setMessage('We could not load this order.'))
    load()
    return () => { if (timer) window.clearTimeout(timer) }
  }, [id])

  async function cancel() {
    setMessage('Cancelling order...')
    try {
      await request(`/api/orders/${id}/cancel`, { method: 'POST' })
      setOrder((current) => current ? { ...current, orderStatus: 'CANCELLED' } : current)
      setMessage('Order cancelled.')
    } catch { setMessage('We could not cancel this order.') }
  }

  if (!order) return <div className="page-container"><div className="empty">{message}</div></div>
  return <div className="page-container"><div className="page-heading"><div><p className="eyebrow">Order #{order.orderId}</p><h1>Order details</h1></div><Link href="/orders" className="back-link">All orders</Link></div><div className="order-detail"><section className="account-card"><p className="eyebrow">Status</p><h2>{statusLabel(order.orderStatus)}</h2><p className="muted">Placed {order.orderDate || 'recently'}</p><StatusTracker status={order.orderStatus} />{order.shippingAddress && <div className="address-block"><p className="eyebrow">Delivery address</p><p>{order.shippingAddress.street}<br />{order.shippingAddress.city} {order.shippingAddress.postalCode}<br />{order.shippingAddress.country}</p></div>}{order.orderStatus !== 'CANCELLED' && order.orderStatus !== 'PAID' && <button className="secondary-button" onClick={cancel}>Cancel order</button>}{message && <p className="form-error">{message}</p>}</section><section className="detail-items">{(order.orderItems || []).map((item, index) => <article className="detail-item" key={index}><div className="detail-thumb"><span>{String(index + 1).padStart(2, '0')}</span></div><div><h3>{item.skuDTO?.skuCode || 'Selected item'}</h3><p className="muted">Quantity {item.quantity || 1}</p></div><strong>{formatMoney((item.price || 0) * (item.quantity || 1))}</strong></article>)}<div className="summary-line"><span>Total</span><strong>{formatMoney(order.totalPrice || 0)}</strong></div></section></div></div>
}
