const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(req: Request) {
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (payload?.items && !Array.isArray(payload.items))
      return Response.json(
        { message: "Cart items must be an array." },
        { status: 400 },
      );
    for (const item of payload?.items || [])
      if (
        !Number.isInteger(Number(item.inventoryId || item.variantId)) ||
        !Number.isInteger(Number(item.quantity)) ||
        Number(item.quantity) < 1
      )
        return Response.json(
          {
            message:
              "Each cart item requires a valid ID and positive quantity.",
          },
          { status: 400 },
        );
  } catch {
    return Response.json(
      { message: "Invalid cart merge request." },
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
    const response = await fetch(`${BACKEND}/api/v1/cart/merge`, {
      method: "POST",
      headers,
      body,
      cache: "no-store",
    });
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
      { message: "Unable to merge guest cart." },
      { status: 502 },
    );
  }
}
