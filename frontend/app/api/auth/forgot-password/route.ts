const BACKEND = process.env.BACKEND_BASE_URL!;

export async function POST(req: Request) {
  const body = await req.json().catch(() => ({}));
  const response = await fetch(`${BACKEND}/api/v1/auth/forgot-password`, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ email: body.email }),
    cache: "no-store",
  });

  const contentType = response.headers.get("content-type") || "";
  const data = contentType.includes("application/json")
    ? await response.json().catch(() => ({}))
    : await response.text();

  if (!response.ok) {
    return Response.json(
      typeof data === "string"
        ? { message: data }
        : data || { message: "Unable to process the request." },
      { status: response.status, headers: { "cache-control": "no-store" } },
    );
  }

  // Do not expose reset tokens or email-template details to the browser.
  return Response.json(
    {
      ok: true,
      message:
        "If an account exists for that email, reset instructions will be sent.",
    },
    { status: 200, headers: { "cache-control": "no-store" } },
  );
}
