'use client'
import React, { useEffect, useState } from 'react'
import { CartDTO } from '../../types/api'
import { formatMoney } from '../../lib/storefront'

export default function CartPage() {
  const [cart, setCart] = useState<CartDTO | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function load() {
    setLoading(true)
    setError(null)
    try {
      const r = await fetch('/api/cart')
      if (r.ok) setCart(await r.json())
      else setError('We could not load your cart.')
    } catch (e) {
      setError('We could not load your cart.')
    } finally { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  async function removeItem(inventoryId?: number) {
    if (!inventoryId) return
    const response = await fetch(`/api/cart/items/${inventoryId}`, { method: 'DELETE' })
    if (!response.ok) setError('We could not remove that item.')
    await load()
  }

  async function updateItem(inventoryId?: number, qty = 1) {
    if (!inventoryId) return
    const response = await fetch(`/api/cart/items/${inventoryId}`, { method: 'PATCH', headers: { 'content-type': 'application/json' }, body: JSON.stringify({ inventoryId, quantity: qty }) })
    if (!response.ok) setError('That quantity is not available.')
    await load()
  }

  const items = cart?.cartItems ?? []
  const itemCount = items.reduce((total, item) => total + (item.quantity || 0), 0)
  const subtotal = items.reduce((total, item) => total + (item.priceAtAdd || 0) * (item.quantity || 0), 0)
  const shipping = subtotal > 0 ? 0 : 0
  const total = subtotal + shipping

  if (loading && !cart) return <div className="page-container"><div className="empty">Loading your cart...</div></div>
  return (
    <div className="page-container">
      <div className="page-heading"><div><p className="eyebrow">Your selection</p><h1>Cart</h1></div>{cart?.cartItems?.length ? <button className="text-button" onClick={() => fetch('/api/cart', { method: 'DELETE' }).then(load)}>Clear cart</button> : null}</div>
      {error && <p className="form-error">{error}</p>}
      {items.length ? (
        <div className="cart-layout"><ul className="cart-list">
          {items.map((it) => (
            <li key={it.cartItemId} className="cart-item">
              <div>
                <div className="eyebrow">Inventory #{it.inventoryId}</div>
                <h3>Selected item</h3>
                <div className="muted">{formatMoney(it.priceAtAdd || 0)} each</div>
              </div>
              <div className="cart-line-actions"><strong>{formatMoney((it.priceAtAdd || 0) * (it.quantity || 0))}</strong>
                <div className="stepper"><button onClick={() => updateItem(it.inventoryId, Math.max(1, (it.quantity || 1) - 1))}>-</button><span>{it.quantity}</span><button onClick={() => updateItem(it.inventoryId, (it.quantity || 1) + 1)}>+</button></div>
                <button onClick={() => removeItem(it.inventoryId)} className="text-button">Remove</button>
              </div>
            </li>
          ))}
        </ul><aside className="summary"><p className="eyebrow">Order review</p><div className="summary-line"><span>Items ({itemCount})</span><strong>{formatMoney(subtotal)}</strong></div><div className="summary-line"><span>Shipping</span><strong>{shipping ? formatMoney(shipping) : 'Free'}</strong></div><div className="summary-line summary-total"><span>Total</span><strong>{formatMoney(total)}</strong></div><a href="/checkout" className="primary-button">Continue to checkout</a></aside></div>
      ) : (
        <div className="empty"><h2>Your cart is empty.</h2><p className="muted">Find something considered for your everyday.</p><a href="/products" className="primary-button">Continue shopping</a></div>
      )}
    </div>
  )
}
