import React from "react";
import { ProductDTO } from "../../../types/api";
import ProductDetailsClient from "./components/ProductDetailsClient";
import { RelatedProducts } from "../../../components/RelatedProducts";
import { ProductReviews } from "../../../components/ProductReviews";
import {
  productImage,
  formatMoney,
  productPrice,
} from "../../../lib/storefront";

const BACKEND = process.env.BACKEND_BASE_URL!;

interface Props {
  params: Promise<{ id: string }>;
}

async function fetchProduct(id: string) {
  const res = await fetch(
    `${BACKEND}/api/v1/products/${encodeURIComponent(id)}`,
    { cache: "no-store" },
  );
  if (!res.ok) throw new Error("Failed to fetch product");
  return res.json() as Promise<ProductDTO>;
}

export default async function ProductPage({ params }: Props) {
  const { id } = await params;
  let product: ProductDTO;
  try {
    product = await fetchProduct(id);
  } catch {
    return (
      <div className="page-container">
        <div role="alert" className="empty">
          <h1>Product unavailable</h1>
          <p>This product could not be found or is no longer available.</p>
          <a href="/products" className="primary-button">
            Browse products
          </a>
        </div>
      </div>
    );
  }
  const stock = (product.skuDTOs ?? []).reduce(
    (total, sku) =>
      total +
      (sku.inventoryDTOS ?? []).reduce(
        (skuTotal, inventory) => skuTotal + (inventory.stockAvailable ?? 0),
        0,
      ),
    0,
  );

  return (
    <div className="page-container">
      <div className="product-detail">
        <div className="detail-image">
          <img src={productImage(product)} alt={product.name} />
        </div>
        <div>
          <p className="eyebrow">
            {product.categoryDTO?.category} · {product.categoryDTO?.subcategory}
          </p>
          <h1>{product.name}</h1>
          <p className="muted">{product.description}</p>
          <p>
            <strong>{formatMoney(productPrice(product))}</strong>
          </p>
          <p className="muted">{stock > 0 ? `${stock} left` : "Sold out"}</p>
          <ProductDetailsClient product={product} />
        </div>
      </div>
      <RelatedProducts productId={product.productId} />
      <ProductReviews productId={product.productId} />
    </div>
  );
}
