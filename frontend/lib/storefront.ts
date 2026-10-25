import { ProductDTO } from '../types/api'

export const fallbackImage = 'https://images.unsplash.com/photo-1525507119028-ed4c629a60a3?auto=format&fit=crop&w=900&q=85'

export function productPrice(product: ProductDTO) {
  return product.skuDTOs?.[0]?.inventoryDTOS?.[0]?.sellingPrice ?? 0
}

export function productStock(product: ProductDTO) {
  return (product.skuDTOs ?? []).reduce((total, sku) => total + (sku.inventoryDTOS ?? []).reduce((skuTotal, inventory) => skuTotal + (inventory.stockAvailable ?? 0), 0), 0)
}

export function productImage(product: ProductDTO) {
  return fallbackImage
}

export function formatMoney(value: number) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value)
}