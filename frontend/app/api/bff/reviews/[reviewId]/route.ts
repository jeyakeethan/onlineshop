const BACKEND = process.env.BACKEND_BASE_URL!;
export async function PATCH(
  req: Request,
  { params }: { params: Promise<{ reviewId: string }> },
) {
  const { reviewId } = await params;
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (
      !payload.reviewText?.trim() ||
      !Number.isInteger(Number(payload.rating)) ||
      Number(payload.rating) < 1 ||
      Number(payload.rating) > 5
    )
      return Response.json(
        { message: "Review text and a rating from 1 to 5 are required." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid review update." },
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
      `${BACKEND}/api/v1/reviews/${encodeURIComponent(reviewId)}`,
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
      { message: "Unable to update review." },
      { status: 502 },
    );
  }
}

export async function DELETE(
  req: Request,
  { params }: { params: Promise<{ reviewId: string }> },
) {
  const { reviewId } = await params;
  const headers: Record<string, string> = {};
  const cookie = req.headers.get("cookie");
  const authorization = req.headers.get("authorization");
  if (cookie) headers.cookie = cookie;
  if (authorization) headers.authorization = authorization;
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/reviews/${encodeURIComponent(reviewId)}`,
      { method: "DELETE", headers, cache: "no-store" },
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
      { message: "Unable to delete review." },
      { status: 502 },
    );
  }
}
