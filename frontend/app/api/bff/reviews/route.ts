const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(req: Request) {
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (
      !Number.isInteger(Number(payload.productId)) ||
      !payload.reviewText?.trim() ||
      !Number.isInteger(Number(payload.rating)) ||
      Number(payload.rating) < 1 ||
      Number(payload.rating) > 5
    )
      return Response.json(
        {
          message:
            "Product, review text, and a rating from 1 to 5 are required.",
        },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid review request." },
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
    const response = await fetch(`${BACKEND}/api/v1/reviews`, {
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
      { message: "Unable to submit review." },
      { status: 502 },
    );
  }
}
