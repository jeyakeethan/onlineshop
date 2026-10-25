import React from 'react'
import Link from 'next/link'
import { BrowseButton } from '../components/storefront'

export default function HomePage() {
  return (
    <>
      <section className="hero">
        <div className="hero-copy">
          <p className="eyebrow">Arc / Form · Online shop</p>
          <h1>Objects for considered living.</h1>
          <p>Thoughtful pieces for the spaces and rituals that make a day feel like yours.</p>
          <div className="flex gap-3 flex-wrap">
            <BrowseButton />
            <Link href="/auth/login" className="secondary-button">Sign in</Link>
          </div>
        </div>
        <div className="hero-image" />
      </section>

      <section id="categories" className="page-container">
        <p className="eyebrow">The collection</p>
        <h2>Quietly useful, made to last.</h2>
        <div className="category-grid">
          <Link href="/products" className="category-card"><span>Everyday essentials</span></Link>
          <Link href="/products" className="category-card"><span>Desk & study</span></Link>
          <Link href="/products" className="category-card"><span>Living room</span></Link>
        </div>
      </section>
    </>
  )
}
