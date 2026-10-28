import React from "react";
import Link from "next/link";
import { BrowseButton } from "../components/storefront";
import { CategoryGrid } from "../components/CategoryGrid";
import { FeaturedProducts } from "../components/FeaturedProducts";

export default function HomePage() {
  return (
    <>
      <section className="hero">
        <div className="hero-copy">
          <p className="eyebrow">Arc / Form · Online shop</p>
          <h1>Objects for considered living.</h1>
          <p>
            Thoughtful pieces for the spaces and rituals that make a day feel
            like yours.
          </p>
          <div className="flex gap-3 flex-wrap">
            <BrowseButton />
            <Link href="/auth/login" className="secondary-button">
              Sign in
            </Link>
          </div>
        </div>
        <div className="hero-image" />
      </section>

      <section id="categories" className="page-container">
        <p className="eyebrow">The collection</p>
        <h2>Quietly useful, made to last.</h2>
        <CategoryGrid />
      </section>
      <section className="page-container">
        <p className="eyebrow">Featured</p>
        <h2>Selected for your space.</h2>
        <FeaturedProducts />
      </section>
    </>
  );
}
