const BACKEND = process.env.BACKEND_BASE_URL!;

function buildHeaders(req: Request) {
  const headers: Record<string, string> = {};
  const ct = req.headers.get("content-type");
  if (ct) headers["content-type"] = ct;
  const cookie = req.headers.get("cookie");
  if (cookie) headers["cookie"] = cookie;
  return headers;
}

export async function GET(req: Request) {
  const headers: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  const resp = await fetch(`${BACKEND}/api/v1/cart`, {
    headers,
    cache: "no-store",
  });
  const body = await resp.text();
  return new Response(body, {
    status: resp.status,
    headers: {
      "content-type": resp.headers.get("content-type") || "application/json",
      "cache-control": "no-store",
    },
  });
}

export async function POST(req: Request) {
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (
      !Number.isInteger(payload.inventoryId) ||
      payload.inventoryId <= 0 ||
      !Number.isInteger(payload.quantity) ||
      payload.quantity <= 0
    )
      return Response.json(
        { message: "A valid inventory ID and positive quantity are required." },
        { status: 400 },
      );
  } catch {
    return Response.json({ message: "Invalid cart request." }, { status: 400 });
  }
  const resp = await fetch(`${BACKEND}/api/v1/cart/items`, {
    method: "POST",
    headers: buildHeaders(req),
    body,
  });
  const text = await resp.text();
  return new Response(text, {
    status: resp.status,
    headers: {
      "content-type": resp.headers.get("content-type") || "application/json",
      "cache-control": "no-store",
    },
  });
}

export async function DELETE(req: Request) {
  // support clearing cart
  const url = new URL(req.url);
  const pathname = url.pathname;
  if (pathname.endsWith("/api/cart")) {
    const resp = await fetch(`${BACKEND}/api/v1/cart/items`, {
      method: "DELETE",
      headers: buildHeaders(req),
    });
    const text = await resp.text();
    return new Response(text, { status: resp.status });
  }
  return new Response(null, { status: 405 });
}

// PATCH for cart-level updates is not used; item-level PATCH handled in /api/cart/items/[inventoryId]
