'use client'
import React, { useEffect, useMemo, useState } from 'react'
import { ProductDTO } from '../../types/api'
import { ProductGrid } from '../../components/storefront'

const DEFAULT_CATEGORIES = ['All', 'Home', 'Office', 'Living', 'Accessories']

export default function ProductsPage() {
  const [products, setProducts] = useState<ProductDTO[]>([])
  const [keyword, setKeyword] = useState('')
  const [category, setCategory] = useState('All')
  const [loading, setLoading] = useState(true)

  async function loadProducts(nextKeyword = keyword, nextCategory = category) {
    setLoading(true)
    try {
      const normalizedKeyword = (nextKeyword || '').trim()
      const normalizedCategory = (nextCategory || 'All').trim()

      let url = '/api/bff/products'
      if (normalizedCategory !== 'All' || normalizedKeyword) {
        if (normalizedCategory === 'All') {
          url = `/api/bff/products/search?keyword=${encodeURIComponent(normalizedKeyword)}`
        } else {
          url = `/api/bff/products/filter?category=${encodeURIComponent(normalizedCategory)}&keyword=${encodeURIComponent(normalizedKeyword)}`
        }
      }

      const res = await fetch(url, { cache: 'no-store' })
      if (!res.ok) throw new Error('Failed to fetch products')
      const data = await res.json()
      setProducts(Array.isArray(data) ? data : [])
    } catch {
      setProducts([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadProducts()
  }, [])

  useEffect(() => {
    const timeout = setTimeout(() => {
      loadProducts(keyword, category)
    }, 250)
    return () => clearTimeout(timeout)
  }, [keyword, category])

  const activeCount = useMemo(() => products.length, [products])

  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Arc / Form collection</p>
          <h1>Products</h1>
        </div>
      </div>

      <div className="catalog-tools">
        <label className="search">
          <span>⌕</span>
          <input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="Search products" />
        </label>
        <div className="filters">
          {DEFAULT_CATEGORIES.map((item) => (
            <button
              key={item}
              className={item === category ? 'active' : ''}
              onClick={() => setCategory(item)}
              type="button"
            >
              {item}
            </button>
          ))}
        </div>
      </div>

      <p className="muted" style={{ marginBottom: '1rem' }}>
        {loading ? 'Loading products...' : `${activeCount} item${activeCount === 1 ? '' : 's'} found`}
      </p>

      {products.length ? <ProductGrid products={products} /> : !loading ? <div className="empty"><h2>No products match your search.</h2></div> : null}
    </div>
  )
}
