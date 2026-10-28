const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(
  req: Request,
  { params }: { params: Promise<{ orderId: string }> },
) {
  const { orderId } = await params;
  const headers: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  const idempotency = req.headers.get("idempotency-key");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  if (idempotency) headers["Idempotency-Key"] = idempotency;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/admin/orders/${encodeURIComponent(orderId)}/fulfillment/deliver`,
      { method: "POST", headers, cache: "no-store" },
    );
    return new Response(await response.text(), {
      status: response.status,
      headers: {
        "content-type":
          response.headers.get("content-type") || "application/json",
        "cache-control": "no-store",
      },
    });
  } catch {
    return Response.json(
      { message: "Unable to mark order delivered." },
      { status: 502 },
    );
  }
}
