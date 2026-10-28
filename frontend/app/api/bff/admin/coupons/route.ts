const BACKEND = process.env.BACKEND_BASE_URL!;
export async function GET(req: Request) {
  const incoming = new URL(req.url);
  const query = new URLSearchParams();
  for (const key of ["status", "search", "sort"]) {
    const value = incoming.searchParams.get(key);
    if (value) query.set(key, value);
  }
  const headers: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(`${BACKEND}/api/v1/admin/coupons?${query}`, {
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
      { message: "Unable to load coupons." },
      { status: 502 },
    );
  }
}

export async function POST(req: Request) {
  const headers: Record<string, string> = {
    "content-type": "application/json",
  };
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(`${BACKEND}/api/v1/admin/coupons`, {
      method: "POST",
      headers,
      body: await req.text(),
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
      { message: "Unable to create coupon." },
      { status: 502 },
    );
  }
}
