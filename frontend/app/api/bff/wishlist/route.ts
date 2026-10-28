const BACKEND = process.env.BACKEND_BASE_URL!;
export async function GET(req: Request) {
  const headers: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(`${BACKEND}/api/v1/wishlist`, {
      headers,
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
      { message: "Unable to load wishlist." },
      { status: 502 },
    );
  }
}

export async function POST(req: Request) {
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (
      !Number.isInteger(Number(payload.inventoryId || payload.variantId)) ||
      Number(payload.inventoryId || payload.variantId) <= 0
    )
      return Response.json(
        { message: "A valid inventory or variant ID is required." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid wishlist request." },
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
    const response = await fetch(`${BACKEND}/api/v1/wishlist/items`, {
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
      { message: "Unable to add wishlist item." },
      { status: 502 },
    );
  }
}
