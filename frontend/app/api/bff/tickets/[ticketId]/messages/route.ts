const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(
  req: Request,
  { params }: { params: Promise<{ ticketId: string }> },
) {
  const { ticketId } = await params;
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (!payload.message?.trim() || payload.message.length > 5000)
      return Response.json(
        { message: "A message up to 5000 characters is required." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid ticket message." },
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
      `${BACKEND}/api/v1/tickets/${encodeURIComponent(ticketId)}/messages`,
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
      { message: "Unable to send ticket message." },
      { status: 502 },
    );
  }
}
