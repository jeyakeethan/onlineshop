const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(req: Request) {
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (
      !Number.isInteger(Number(payload.ticketId)) ||
      !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(payload.email || "")
    )
      return Response.json(
        { message: "Ticket ID and a valid email are required." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid guest-ticket verification request." },
      { status: 400 },
    );
  }
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/guest-tickets/verification/request`,
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
      { message: "Unable to process verification request." },
      { status: 502 },
    );
  }
}
