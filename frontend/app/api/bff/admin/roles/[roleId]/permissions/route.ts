const BACKEND = process.env.BACKEND_BASE_URL!;
export async function PATCH(
  req: Request,
  { params }: { params: Promise<{ roleId: string }> },
) {
  const { roleId } = await params;
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (!Array.isArray(payload.permissions))
      return Response.json(
        { message: "Permissions must be an array." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid permissions update." },
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
      `${BACKEND}/api/v1/admin/roles/${encodeURIComponent(roleId)}/permissions`,
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
      { message: "Unable to update role permissions." },
      { status: 502 },
    );
  }
}
