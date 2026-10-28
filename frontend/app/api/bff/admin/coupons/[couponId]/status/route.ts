const BACKEND = process.env.BACKEND_BASE_URL!;
export async function PATCH(
  req: Request,
  { params }: { params: Promise<{ couponId: string }> },
) {
  const { couponId } = await params;
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (!["ACTIVE", "INACTIVE"].includes(String(payload.status).toUpperCase()))
      return Response.json(
        { message: "Status must be ACTIVE or INACTIVE." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid coupon status request." },
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
      `${BACKEND}/api/v1/admin/coupons/${encodeURIComponent(couponId)}/status`,
      { method: "PATCH", headers, body, cache: "no-store" },
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
      { message: "Unable to update coupon status." },
      { status: 502 },
    );
  }
}
