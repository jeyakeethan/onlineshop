import React from 'react'
import { ProductDTO } from '../../../types/api'
import ProductDetailsClient from './components/ProductDetailsClient'
import { productImage, formatMoney, productPrice } from '../../../lib/storefront'

const BACKEND = process.env.BACKEND_BASE_URL!

interface Props { params: Promise<{ id: string }> }

async function fetchProduct(id: string) {
  const res = await fetch(`${BACKEND}/api/v1/products/${id}`, { cache: 'no-store' })
  if (!res.ok) throw new Error('Failed to fetch product')
  return res.json() as Promise<ProductDTO>
}

export default async function ProductPage({ params }: Props) {
  const { id } = await params
  const product = await fetchProduct(id)
  const stock = (product.skuDTOs ?? []).reduce(
    (total, sku) => total + (sku.inventoryDTOS ?? []).reduce((skuTotal, inventory) => skuTotal + (inventory.stockAvailable ?? 0), 0),
    0,
  )

  return (
    <div className="page-container">
      <div className="product-detail">
        <div className="detail-image">
          <img src={productImage(product)} alt={product.name} />
        </div>
        <div>
          <p className="eyebrow">{product.categoryDTO?.category} · {product.categoryDTO?.subcategory}</p>
          <h1>{product.name}</h1>
          <p className="muted">{product.description}</p>
          <p><strong>{formatMoney(productPrice(product))}</strong></p>
          <p className="muted">{stock > 0 ? `${stock} left` : 'Sold out'}</p>
          <ProductDetailsClient product={product} />
        </div>
      </div>
    </div>
  )
}
