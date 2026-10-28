const BACKEND = process.env.BACKEND_BASE_URL!;
export async function PATCH(
  req: Request,
  { params }: { params: Promise<{ userId: string }> },
) {
  const { userId } = await params;
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (!Array.isArray(payload.roles) || !payload.roles.length)
      return Response.json(
        { message: "At least one role is required." },
        { status: 400 },
      );
  } catch {
    return Response.json({ message: "Invalid role update." }, { status: 400 });
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
      `${BACKEND}/api/v1/admin/users/${encodeURIComponent(userId)}/roles`,
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
      { message: "Unable to update user roles." },
      { status: 502 },
    );
  }
}
