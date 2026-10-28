const BACKEND = process.env.BACKEND_BASE_URL!;

function buildHeaders(req: Request) {
  const headers: Record<string, string> = {};
  const contentType = req.headers.get("content-type");
  const cookie = req.headers.get("cookie");
  const idempotencyKey = req.headers.get("Idempotency-Key");
  const mockToken = req.headers.get("X-Mock-Payment-Token");
  if (contentType) headers["content-type"] = contentType;
  if (cookie) headers.cookie = cookie;
  if (idempotencyKey) headers["Idempotency-Key"] = idempotencyKey;
  if (mockToken && process.env.NODE_ENV !== "production")
    headers["X-Mock-Payment-Token"] = mockToken;
  return headers;
}

async function proxy(req: Request) {
  const url = new URL(req.url);
  const tail = url.pathname.replace("/api/payments", "");
  const init: RequestInit = { method: req.method, headers: buildHeaders(req) };
  if (req.method !== "GET" && req.method !== "HEAD")
    init.body = await req.arrayBuffer();
  const backendPath =
    tail === "/mock/complete"
      ? `/api/v1/orders/payments/mock/complete${url.search}`
      : `/api/v1/payments${tail}${url.search}`;
  const response = await fetch(`${BACKEND}${backendPath}`, {
    ...init,
    cache: "no-store",
  });
  return new Response(await response.text(), {
    status: response.status,
    headers: {
      "content-type":
        response.headers.get("content-type") || "application/json",
    },
  });
}

export const POST = proxy;
