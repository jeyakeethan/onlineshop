export async function mergeGuestCart(
  guestCartToken: string,
  items: Array<{ inventoryId?: number; variantId?: number; quantity: number }>,
) {
  const response = await fetch("/api/cart/merge", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ guestCartToken, items }),
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok)
    throw new Error(
      response.status === 409
        ? "This guest cart has already been merged."
        : data?.message || "Unable to merge guest cart.",
    );
  return data as { guestCartToken?: string };
}
