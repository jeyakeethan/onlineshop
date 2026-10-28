const BACKEND = process.env.BACKEND_BASE_URL!;
export async function GET(
  req: Request,
  { params }: { params: Promise<{ orderId: string }> },
) {
  const { orderId } = await params;
  const access = req.headers.get("X-Guest-Order-Access");
  if (!access)
    return Response.json(
      { message: "Guest order verification is required." },
      { status: 401 },
    );
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/guest-orders/${encodeURIComponent(orderId)}`,
      { headers: { "X-Guest-Order-Access": access }, cache: "no-store" },
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
      { message: "Unable to load guest order." },
      { status: 502 },
    );
  }
}
