const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(req: Request) {
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (!payload?.challenge?.trim())
      return Response.json(
        { message: "A verification challenge is required." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid verification request." },
      { status: 400 },
    );
  }
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/guest-orders/lookup/verify`,
      {
        method: "POST",
        headers: { "content-type": "application/json" },
        body,
        cache: "no-store",
      },
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
      { message: "Unable to verify guest order lookup." },
      { status: 502 },
    );
  }
}
