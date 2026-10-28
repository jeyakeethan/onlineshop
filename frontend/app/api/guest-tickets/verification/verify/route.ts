const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(req: Request) {
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (
      !Number.isInteger(Number(payload.ticketId)) ||
      !/^\d{6}$/.test(payload.code || "")
    )
      return Response.json(
        {
          message: "Ticket ID and a six-digit verification code are required.",
        },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid guest-ticket verification." },
      { status: 400 },
    );
  }
  try {
    const response = await fetch(
      `${BACKEND}/api/v1/guest-tickets/verification/verify`,
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
      { message: "Unable to verify guest ticket." },
      { status: 502 },
    );
  }
}
