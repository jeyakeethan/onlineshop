export type GuestCartItem = {
  inventoryId: number;
  quantity: number;
  name?: string;
  price?: number;
  imageUrl?: string;
  variantLabel?: string;
};

const STORAGE_KEY = "onlineshop.guest-cart.v1";
const EVENT_NAME = "guest-cart-changed";

function canUseStorage() {
  return typeof window !== "undefined";
}

export function readGuestCart(): GuestCartItem[] {
  if (!canUseStorage()) return [];
  try {
    const value = JSON.parse(window.localStorage.getItem(STORAGE_KEY) || "[]");
    return Array.isArray(value) ? value.filter((item) => Number.isInteger(item?.inventoryId) && item.inventoryId > 0 && item.quantity > 0) : [];
  } catch {
    return [];
  }
}

export function writeGuestCart(items: GuestCartItem[]) {
  if (!canUseStorage()) return;
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
  window.dispatchEvent(new CustomEvent(EVENT_NAME));
}

export function addGuestCartItem(item: GuestCartItem) {
  const current = readGuestCart();
  const existing = current.find((entry) => entry.inventoryId === item.inventoryId);
  if (existing) existing.quantity = Math.min(10, existing.quantity + item.quantity);
  else current.push({ ...item, quantity: Math.min(10, item.quantity) });
  writeGuestCart(current);
}

export function updateGuestCartItem(inventoryId: number, quantity: number) {
  writeGuestCart(readGuestCart().map((item) => item.inventoryId === inventoryId ? { ...item, quantity: Math.min(10, Math.max(1, quantity)) } : item));
}

export function removeGuestCartItem(inventoryId: number) {
  writeGuestCart(readGuestCart().filter((item) => item.inventoryId !== inventoryId));
}

export function clearGuestCart() {
  writeGuestCart([]);
}

export function guestCartToken() {
  if (!canUseStorage()) return "guest-server";
  let token = window.localStorage.getItem(`${STORAGE_KEY}.token`);
  if (!token) { token = `guest-${crypto.randomUUID()}`; window.localStorage.setItem(`${STORAGE_KEY}.token`, token); }
  return token;
}

export function onGuestCartChange(listener: () => void) {
  if (!canUseStorage()) return () => undefined;
  window.addEventListener(EVENT_NAME, listener);
  return () => window.removeEventListener(EVENT_NAME, listener);
}
