'use client'
import React, { useState } from 'react'
import { useRouter } from 'next/navigation'

import { ProductDTO } from '../../../../types/api'

export default function ProductDetailsClient({ product }: { product: ProductDTO }) {
  const router = useRouter()
  const [skuIndex, setSkuIndex] = useState(0)
  const [quantity, setQuantity] = useState<number>(1)
  const [status, setStatus] = useState<string | null>(null)
  const sku = product.skuDTOs?.[skuIndex]
  const inventory = sku?.inventoryDTOS?.[0]
  const stockLeft = inventory?.stockAvailable ?? 0
  const maxQuantity = Math.max(1, stockLeft)

  const stockLabel = stockLeft > 0 ? `${stockLeft} left` : 'Sold out'

  function handleQuantityChange(nextQty: number) {
    if (!inventory || stockLeft <= 0) return
    setQuantity(Math.min(Math.max(1, nextQty), maxQuantity))
  }

  async function addToCart() {
    setStatus(null)

    if (!inventory || stockLeft <= 0) {
      setStatus('This variant is currently sold out.')
      return
    }

    try {
      const res = await fetch('/api/cart', { method: 'POST', headers: { 'content-type': 'application/json' }, body: JSON.stringify({ inventoryId: inventory?.inventoryId, quantity }) })
      if (res.ok) {
        setStatus('Added to cart successfully.')
        setTimeout(() => router.push('/cart'), 400)
      } else {
        const txt = await res.text()
        setStatus('Unable to add item: ' + (txt || 'Try again.'))
      }
    } catch (e) {
      setStatus('Network error while adding to cart.')
    }
  }

  return (
    <div className="buy-panel">
      <div className="option">
        <span>Variant</span>
        <div className="variant-list">
          {(product.skuDTOs ?? []).map((option, index) => (
            <button
              key={`${option.size ?? 'size'}-${option.color ?? 'color'}-${index}`}
              className={index === skuIndex ? 'selected' : ''}
              onClick={() => {
                setSkuIndex(index)
                setStatus(null)
                setQuantity(1)
              }}
            >
              {option.size}{option.color ? ` / ${option.color}` : ''}
            </button>
          ))}
        </div>
      </div>

      <div className="purchase-row">
        <div className="stepper">
          <button onClick={() => handleQuantityChange(quantity - 1)}>-</button>
          <span>{quantity}</span>
          <button onClick={() => handleQuantityChange(quantity + 1)}>+</button>
        </div>
        <button
          onClick={addToCart}
          className="primary-button"
          disabled={!inventory?.inventoryId || stockLeft <= 0}
        >
          Add to cart
        </button>
      </div>

      <div className="flex items-center justify-between text-sm mt-3">
        <span className={stockLeft > 0 ? 'text-emerald-600' : 'text-red-500'}>{stockLabel}</span>
        {inventory?.inventoryId ? <span className="muted">SKU #{inventory.inventoryId}</span> : null}
      </div>

      {status && <p className="status">{status}</p>}
    </div>
  )
}
