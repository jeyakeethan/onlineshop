const BACKEND = process.env.BACKEND_BASE_URL!;
export async function POST(req: Request) {
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (
      !payload.name?.trim() ||
      !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(payload.email || "") ||
      (payload.message || "").trim().length < 10 ||
      (payload.message || "").length > 5000
    )
      return Response.json(
        {
          message:
            "Name, valid email, and a message between 10 and 5000 characters are required.",
        },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid contact request." },
      { status: 400 },
    );
  }
  try {
    const response = await fetch(`${BACKEND}/api/v1/contact`, {
      method: "POST",
      headers: { "content-type": "application/json" },
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
      { message: "Unable to submit contact request." },
      { status: 502 },
    );
  }
}
