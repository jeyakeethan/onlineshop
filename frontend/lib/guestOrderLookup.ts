export async function requestGuestOrderLookup(
  orderReference: string,
  email: string,
) {
  const response = await fetch("/api/guest-orders/lookup/request", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ orderReference, email }),
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok)
    throw new Error(data?.message || "Unable to process lookup request.");
  return data as { message?: string };
}
export async function verifyGuestOrderLookup(challenge: string) {
  const response = await fetch("/api/guest-orders/lookup/verify", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ challenge }),
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok)
    throw new Error(data?.message || "Unable to verify guest order lookup.");
  return data as { orderId: number; accessToken: string; expiresAt: string };
}
export async function getVerifiedGuestOrder(
  orderId: number,
  accessToken: string,
) {
  const response = await fetch(`/api/guest-orders/${orderId}`, {
    headers: { "X-Guest-Order-Access": accessToken },
    cache: "no-store",
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok)
    throw new Error(
      response.status === 403
        ? "Guest order access has expired."
        : data?.message || "Unable to load guest order.",
    );
  return data;
}
export async function claimGuestOrder(accessToken: string) {
  const response = await fetch("/api/me/guest-orders/claim", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ accessToken }),
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok)
    throw new Error(
      response.status === 409
        ? "This order is already claimed or cannot be claimed."
        : data?.message || "Unable to claim guest order.",
    );
  return data;
}
