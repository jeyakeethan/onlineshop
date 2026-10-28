const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(req: Request) {
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (!payload?.accessToken?.trim())
      return Response.json(
        { message: "A guest-order access token is required." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid claim request." },
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
    const response = await fetch(`${BACKEND}/api/v1/me/guest-orders/claim`, {
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
      { message: "Unable to claim guest order." },
      { status: 502 },
    );
  }
}
