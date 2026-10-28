const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(req: Request) {
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (
      !payload?.couponCode?.trim() ||
      typeof payload.subtotal !== "number" ||
      payload.subtotal < 0
    )
      return Response.json(
        { message: "Coupon code and a valid subtotal are required." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid coupon request." },
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
    const response = await fetch(`${BACKEND}/api/v1/coupons/validate`, {
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
      { message: "Unable to validate coupon." },
      { status: 502 },
    );
  }
}
