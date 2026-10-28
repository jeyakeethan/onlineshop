const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(
  req: Request,
  { params }: { params: Promise<{ variantId: string }> },
) {
  const { variantId } = await params;
  const body = await req.text();
  try {
    const payload = body ? JSON.parse(body) : {};
    if (
      !Number.isInteger(Number(payload.quantity ?? 1)) ||
      Number(payload.quantity ?? 1) < 1
    )
      return Response.json(
        { message: "Quantity must be a positive integer." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid move-to-cart request." },
      { status: 400 },
    );
  }
  const headers: Record<string, string> = {
    "content-type": "application/json",
  };
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/wishlist/items/${encodeURIComponent(variantId)}/move-to-cart`,
      {
        method: "POST",
        headers,
        body: body || JSON.stringify({ quantity: 1 }),
        cache: "no-store",
      },
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
      { message: "Unable to move wishlist item to cart." },
      { status: 502 },
    );
  }
}
