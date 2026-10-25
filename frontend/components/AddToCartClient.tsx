'use client'
import React, { useState } from 'react'

export default function AddToCartClient({ inventoryIdProp }: { inventoryIdProp?: number }) {
  const [inventoryId, setInventoryId] = useState<string>(inventoryIdProp ? String(inventoryIdProp) : '')
  const [quantity, setQuantity] = useState<number>(1)
  const [status, setStatus] = useState<string | null>(null)

  async function addToCart() {
    setStatus(null)
    try {
      const res = await fetch('/api/cart', { method: 'POST', headers: { 'content-type': 'application/json' }, body: JSON.stringify({ inventoryId: Number(inventoryId), quantity }) })
      if (res.ok) setStatus('Added to cart')
      else setStatus('Error adding to cart')
    } catch (e) {
      setStatus('Network error')
    }
  }

  return (
    <div className="mt-4">
      <div className="grid grid-cols-2 gap-2 items-end">
        <div>
          <label className="text-sm">Inventory ID</label>
          <input value={inventoryId} onChange={(e) => setInventoryId(e.target.value)} className="mt-1 block w-full border rounded px-2 py-1" />
        </div>
        <div>
          <label className="text-sm">Quantity</label>
          <input type="number" min={1} value={quantity} onChange={(e) => setQuantity(Number(e.target.value))} className="mt-1 block w-full border rounded px-2 py-1" />
        </div>
      </div>
      <div className="mt-3">
        <button onClick={addToCart} className="bg-green-600 text-white px-4 py-2 rounded">Add to cart</button>
      </div>
      {status && <div className="mt-2 text-sm">{status}</div>}
    </div>
  )
}
