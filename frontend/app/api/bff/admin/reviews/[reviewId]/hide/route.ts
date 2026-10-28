const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(
  req: Request,
  { params }: { params: Promise<{ reviewId: string }> },
) {
  const { reviewId } = await params;
  const body = await req.text();
  try {
    const payload = body ? JSON.parse(body) : {};
    if ((payload.reason || "").length > 500)
      return Response.json(
        { message: "Hide reason must be at most 500 characters." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid review hide request." },
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
      `${BACKEND}/api/v1/admin/reviews/${encodeURIComponent(reviewId)}/hide`,
      { method: "POST", headers, body, cache: "no-store" },
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
      { message: "Unable to hide review." },
      { status: 502 },
    );
  }
}
