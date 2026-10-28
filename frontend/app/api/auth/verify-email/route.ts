const BACKEND = process.env.BACKEND_BASE_URL!;

export async function POST(req: Request) {
  const body = await req.json().catch(() => ({}));
  const response = await fetch(`${BACKEND}/api/v1/auth/verify-email`, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ email: body.email, token: body.token }),
    cache: "no-store",
  });
  const message = await response.text();

  return new Response(
    JSON.stringify(
      response.ok
        ? { ok: true, message }
        : { message: message || "Verification failed." },
    ),
    {
      status: response.status,
      headers: {
        "content-type": "application/json",
        "cache-control": "no-store",
      },
    },
  );
}
