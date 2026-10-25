'use client'

import Link from 'next/link'
import { ArrowRight, Search, ShoppingBag, UserRound } from 'lucide-react'
import { ProductDTO } from '../types/api'
import { formatMoney, productImage, productPrice, productStock } from '../lib/storefront'

export function Header({ isAuthenticated }: { isAuthenticated: boolean }) {
  const profilePath = isAuthenticated ? '/profile' : '/auth/login'

  return <header className="site-header"><Link href="/" className="logo">arc / form</Link><nav><Link href="/products">Products</Link><Link href="/#categories">Collections</Link><Link href="/#journal">Journal</Link></nav><div className="header-actions"><Link href="/products" aria-label="Search"><Search size={18} /></Link><Link href="/cart" aria-label="Cart"><ShoppingBag size={18} /></Link><Link href={profilePath} aria-label="Profile"><UserRound size={18} /></Link></div></header>
}

export function Footer() {
  return <footer><div className="logo">arc / form</div><p>Objects for considered living.</p><div className="footer-links"><Link href="/products">Shop all</Link><Link href="/#journal">About</Link><Link href="/profile">Account</Link></div><small>© 2026 Arc / Form. Made with intention.</small></footer>
}

export function ProductCard({ product }: { product: ProductDTO }) {
  const stock = productStock(product)

  return <Link className="product-card" href={`/products/${product.productId}`}><div className="product-image"><img src={productImage(product)} alt={product.name} /><span className="badge">{stock > 0 ? `${stock} left` : 'Sold out'}</span></div><div className="product-meta"><div><p className="eyebrow">{product.categoryDTO?.category} · {product.categoryDTO?.subcategory}</p><h3>{product.name}</h3><p className="muted">{product.description}</p></div><strong>{formatMoney(productPrice(product))}</strong></div></Link>
}

export function ProductGrid({ products }: { products: ProductDTO[] }) {
  return <div className="product-grid">{products.map((product) => <ProductCard key={product.productId} product={product} />)}</div>
}

export function BrowseButton() {
  return <Link href="/products" className="primary-button">Browse collection <ArrowRight size={17} /></Link>
}