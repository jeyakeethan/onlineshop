const BACKEND = process.env.BACKEND_BASE_URL!;
export async function GET(req: Request) {
  const headers: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(`${BACKEND}/api/v1/admin/settings`, {
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
      { message: "Unable to load settings." },
      { status: 502 },
    );
  }
}

export async function PATCH(req: Request) {
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (
      (payload.storeName && payload.storeName.length > 255) ||
      (payload.logoUrl && payload.logoUrl.length > 255) ||
      (payload.contactEmail &&
        !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(payload.contactEmail)) ||
      (payload.currency && !/^[A-Za-z]{3}$/.test(payload.currency))
    )
      return Response.json(
        { message: "Invalid settings field." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid settings update." },
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
    const response = await fetch(`${BACKEND}/api/v1/admin/settings`, {
      method: "PATCH",
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
      { message: "Unable to update settings." },
      { status: 502 },
    );
  }
}
