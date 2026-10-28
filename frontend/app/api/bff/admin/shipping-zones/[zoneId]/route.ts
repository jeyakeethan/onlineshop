const BACKEND = process.env.BACKEND_BASE_URL!;
export async function PATCH(
  req: Request,
  { params }: { params: Promise<{ zoneId: string }> },
) {
  const { zoneId } = await params;
  const headers: Record<string, string> = {
    "content-type": "application/json",
  };
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/admin/shipping-zones/${encodeURIComponent(zoneId)}`,
      { method: "PATCH", headers, body: await req.text(), cache: "no-store" },
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
      { message: "Unable to update shipping zone." },
      { status: 502 },
    );
  }
}
