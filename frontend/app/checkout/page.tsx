'use client'
import React, { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { CartDTO } from '../../types/api'
import { formatMoney } from '../../lib/storefront'

export default function CheckoutPage() {
  const [street, setStreet] = useState('')
  const [city, setCity] = useState('')
  const [postalCode, setPostalCode] = useState('')
  const [country, setCountry] = useState('')
  const [paymentMethod, setPaymentMethod] = useState('Card')
  const [status, setStatus] = useState<string | null>(null)
  const [orderId, setOrderId] = useState<number | null>(null)
  const [cart, setCart] = useState<CartDTO | null>(null)
  const router = useRouter()
  const items = cart?.cartItems ?? []
  const subtotal = items.reduce((total, item) => total + (item.priceAtAdd || 0) * (item.quantity || 0), 0)

  useEffect(() => {
    fetch('/api/cart').then((res) => res.ok ? res.json() : Promise.reject()).then(setCart).catch(() => setStatus('We could not load your cart.'))
  }, [])

  useEffect(() => {
    if (!orderId) return
    const poll = async () => {
      const res = await fetch(`/api/orders/${orderId}/payment-status`)
      if (!res.ok) return
      const payment = await res.json()
      if (payment.paymentStatus === 'REQUIRES_PAYMENT') setStatus('Items reserved. Processing payment...')
      if (payment.paymentStatus === 'PAID') router.push('/checkout/success')
      if (payment.paymentStatus === 'FAILED' || payment.paymentStatus === 'EXPIRED') router.push('/checkout/failed')
    }
    poll()
    const timer = window.setInterval(poll, 2500)
    return () => window.clearInterval(timer)
  }, [orderId, router])

  async function submit(e: React.FormEvent) {
    e.preventDefault()
    setStatus(null)

    if (!street.trim() || !city.trim() || !postalCode.trim() || !country.trim()) {
      setStatus('Complete your shipping address before placing the order.')
      return
    }

    try {
      const order = {
        items: items.map((item) => ({ inventoryId: item.inventoryId, quantity: item.quantity || 1 })),
        shippingAddress: { street, city, postalCode, country },
        paymentMethod,
      }
      const res = await fetch('/api/orders', { method: 'POST', headers: { 'content-type': 'application/json' }, body: JSON.stringify(order) })
      if (res.status === 201 || res.status === 202) {
        const data = await res.json()
        const intent = await fetch('/api/payments/intents', {
          method: 'POST',
          headers: { 'content-type': 'application/json', 'Idempotency-Key': `payment-${data.orderId}` },
          body: JSON.stringify({ orderId: data.orderId, provider: 'mock' })
        })
        if (!intent.ok) {
          setStatus('The order was created, but payment setup is unavailable. Please try again.')
          return
        }
        setOrderId(data.orderId)
        setStatus('Payment intent created. Confirming your order...')
      } else {
        const txt = await res.text()
        setStatus('Error: ' + txt)
      }
    } catch (e) {
      setStatus('Network error')
    }
  }

  return (
    <div className="page-container"><div className="page-heading"><div><p className="eyebrow">Almost yours</p><h1>Checkout</h1></div><a href="/cart" className="back-link">Back to cart</a></div><div className="checkout-layout"><section className="form-shell checkout-form">
      <form onSubmit={submit} className="space-y-4">
        <div><p className="eyebrow">1. Shipping address</p><h2>Where should we send it?</h2></div>
        <div>
          <label className="block text-sm">Street <span aria-hidden="true">*</span></label>
          <input required value={street} onChange={(e) => setStreet(e.target.value)} className="mt-1 block w-full border rounded px-3 py-2" />
        </div>
        <div>
          <label className="block text-sm">City <span aria-hidden="true">*</span></label>
          <input required value={city} onChange={(e) => setCity(e.target.value)} className="mt-1 block w-full border rounded px-3 py-2" />
        </div>
        <div className="admin-form-row">
          <div><label className="block text-sm">Postal Code <span aria-hidden="true">*</span></label><input required value={postalCode} onChange={(e) => setPostalCode(e.target.value)} className="mt-1 block w-full border rounded px-3 py-2" /></div>
          <div><label className="block text-sm">Country <span aria-hidden="true">*</span></label><input required value={country} onChange={(e) => setCountry(e.target.value)} className="mt-1 block w-full border rounded px-3 py-2" /></div>
        </div>
        <div><p className="eyebrow">2. Payment</p><h2>Choose a payment method</h2><div className="payment-options">{['Card', 'Cash on delivery'].map((method) => <button type="button" key={method} className={paymentMethod === method ? 'selected' : ''} onClick={() => setPaymentMethod(method)}>{method}</button>)}</div></div>
        <div>
          <button className="primary-button wide" disabled={!items.length || !!orderId}>{orderId ? 'Processing order...' : `Place order · ${formatMoney(subtotal)}`}</button>
        </div>
        {status && <div className="text-sm mt-2">{status}</div>}
      </form>
    </section><aside className="summary"><p className="eyebrow">Order review</p>{items.map((item) => <div className="summary-line" key={item.cartItemId}><span>{item.quantity} × Inventory #{item.inventoryId}</span><strong>{formatMoney((item.priceAtAdd || 0) * (item.quantity || 0))}</strong></div>)}<div className="summary-line summary-total"><span>Total</span><strong>{formatMoney(subtotal)}</strong></div><p className="muted">Your order is reserved and processed asynchronously after submission.</p></aside></div></div>
  )
}
