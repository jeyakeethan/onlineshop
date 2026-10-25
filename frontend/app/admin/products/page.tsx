'use client'

import React, { useEffect, useState } from 'react'
import { InventoryDTO, ProductDTO } from '../../../types/api'

type ProductForm = {
  name: string
  description: string
  size: string
  color: string
  skuCode: string
  costPrice: string
  sellingPrice: string
  stockAvailable: string
}

const emptyForm: ProductForm = {
  name: '',
  description: '',
  size: '',
  color: '',
  skuCode: '',
  costPrice: '',
  sellingPrice: '',
  stockAvailable: '',
}

export default function ProductManagementPage() {
  const [products, setProducts] = useState<ProductDTO[]>([])
  const [form, setForm] = useState<ProductForm>(emptyForm)
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  async function loadProducts() {
    const response = await fetch('/api/bff/products', { cache: 'no-store' })
    if (!response.ok) throw new Error('Could not load products')
    setProducts(await response.json())
  }

  useEffect(() => {
    loadProducts().catch(() => setError('Could not load products.'))
  }, [])

  function updateForm(field: keyof ProductForm, value: string) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function createProduct(event: React.FormEvent) {
    event.preventDefault()
    setMessage(null)
    setError(null)
    setSaving(true)
    const body = {
      name: form.name,
      description: form.description,
      skuDTOs: [{
        size: form.size,
        color: form.color,
        skuCode: form.skuCode,
        inventoryDTOS: [{
          costPrice: Number(form.costPrice),
          sellingPrice: Number(form.sellingPrice),
          stockAvailable: Number(form.stockAvailable),
        }],
      }],
    }

    try {
      const response = await fetch('/api/bff/products', {
        method: 'POST',
        headers: { 'content-type': 'application/json' },
        body: JSON.stringify(body),
      })
      if (!response.ok) {
        setError(await response.text() || 'Could not create product.')
        return
      }
      setForm(emptyForm)
      setMessage('Product added successfully.')
      await loadProducts()
    } catch {
      setError('Network error while creating the product.')
    } finally {
      setSaving(false)
    }
  }

  async function updateStock(productId: number | undefined, inventory: InventoryDTO, stock: string) {
    if (!productId || !inventory.inventoryId) return
    setMessage(null)
    setError(null)
    const response = await fetch(`/api/bff/products/${productId}/stock?inventoryId=${inventory.inventoryId}`, {
      method: 'PATCH',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({ stock: Number(stock) }),
    })
    if (!response.ok) {
      setError(await response.text() || 'Could not update stock.')
      return
    }
    setMessage(`Inventory ${inventory.inventoryId} updated.`)
    await loadProducts()
  }

  async function updatePricing(productId: number | undefined, inventory: InventoryDTO, sellingPrice: string, costPrice: string) {
    if (!productId || !inventory.inventoryId) return
    setMessage(null)
    setError(null)
    const response = await fetch(`/api/bff/products/${productId}/pricing?inventoryId=${inventory.inventoryId}`, { method: 'PATCH', headers: { 'content-type': 'application/json' }, body: JSON.stringify({ sellingPrice: Number(sellingPrice), costPrice: Number(costPrice) }) })
    if (!response.ok) { setError(await response.text() || 'Could not update pricing.'); return }
    setMessage(`Inventory ${inventory.inventoryId} pricing updated.`)
    await loadProducts()
  }

  return (
    <div className="page-container">
      <div className="page-heading">
        <div><p className="eyebrow">Catalog administration</p><h1>Products</h1></div>
        <a className="back-link" href="/profile">Back to profile</a>
      </div>

      {message && <p className="status">{message}</p>}
      {error && <p className="form-error">{error}</p>}

      <div className="admin-grid">
        <section className="form-shell">
          <p className="eyebrow">New catalog item</p>
          <h2>Add product</h2>
          <form onSubmit={createProduct} className="stack-form">
            <label>Name<input required value={form.name} onChange={(event) => updateForm('name', event.target.value)} /></label>
            <label>Description<textarea required value={form.description} onChange={(event) => updateForm('description', event.target.value)} /></label>
            <label>SKU code<input required value={form.skuCode} onChange={(event) => updateForm('skuCode', event.target.value)} /></label>
            <div className="admin-form-row">
              <label>Size<input value={form.size} onChange={(event) => updateForm('size', event.target.value)} /></label>
              <label>Color<input value={form.color} onChange={(event) => updateForm('color', event.target.value)} /></label>
            </div>
            <div className="admin-form-row">
              <label>Cost price<input required min="0" step="0.01" type="number" value={form.costPrice} onChange={(event) => updateForm('costPrice', event.target.value)} /></label>
              <label>Selling price<input required min="0" step="0.01" type="number" value={form.sellingPrice} onChange={(event) => updateForm('sellingPrice', event.target.value)} /></label>
            </div>
            <label>Opening stock<input required min="0" type="number" value={form.stockAvailable} onChange={(event) => updateForm('stockAvailable', event.target.value)} /></label>
            <button className="primary-button wide" disabled={saving}>{saving ? 'Adding product...' : 'Add product'}</button>
          </form>
        </section>

        <section>
          <div className="page-heading"><div><p className="eyebrow">Live catalog</p><h2>Inventory</h2></div><a className="secondary-button" href="/admin/orders">Manage orders</a></div>
          <div className="inventory-list">
            {products.map((product) => (
              <article className="inventory-product" key={product.productId}>
                <div><p className="eyebrow">Product #{product.productId}</p><h3>{product.name}</h3></div>
                {(product.skuDTOs || []).flatMap((sku) => (sku.inventoryDTOS || []).map((inventory) => (
                  <InventoryRow key={inventory.inventoryId} productId={product.productId} skuCode={sku.skuCode} inventory={inventory} onSave={updateStock} onPricingSave={updatePricing} />
                )))}
              </article>
            ))}
            {!products.length && <div className="empty">No products found.</div>}
          </div>
        </section>
      </div>
    </div>
  )
}

function InventoryRow({ productId, skuCode, inventory, onSave, onPricingSave }: { productId?: number; skuCode?: string; inventory: InventoryDTO; onSave: (productId: number | undefined, inventory: InventoryDTO, stock: string) => Promise<void>; onPricingSave: (productId: number | undefined, inventory: InventoryDTO, sellingPrice: string, costPrice: string) => Promise<void> }) {
  const [stock, setStock] = useState(String(inventory.stockAvailable ?? 0))
  const [sellingPrice, setSellingPrice] = useState(String(inventory.sellingPrice ?? 0))
  const [costPrice, setCostPrice] = useState(String(inventory.costPrice ?? 0))

  useEffect(() => { setStock(String(inventory.stockAvailable ?? 0)); setSellingPrice(String(inventory.sellingPrice ?? 0)); setCostPrice(String(inventory.costPrice ?? 0)) }, [inventory.stockAvailable, inventory.sellingPrice, inventory.costPrice])

  return (
    <div className="inventory-row">
      <div><strong>{skuCode || 'SKU'}</strong><span className="muted">Inventory #{inventory.inventoryId}</span></div>
      <label>Stock<input min="0" type="number" value={stock} onChange={(event) => setStock(event.target.value)} /></label>
      <label>Sell<input min="0" step="0.01" type="number" value={sellingPrice} onChange={(event) => setSellingPrice(event.target.value)} /></label>
      <label>Cost<input min="0" step="0.01" type="number" value={costPrice} onChange={(event) => setCostPrice(event.target.value)} /></label>
      <button className="secondary-button" onClick={() => onSave(productId, inventory, stock)}>Update</button>
      <button className="secondary-button" onClick={() => onPricingSave(productId, inventory, sellingPrice, costPrice)}>Save pricing</button>
    </div>
  )
}
