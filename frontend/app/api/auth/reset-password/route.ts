const BACKEND = process.env.BACKEND_BASE_URL!;

export async function POST(req: Request) {
  const body = await req.json().catch(() => ({}));
  const response = await fetch(`${BACKEND}/api/v1/auth/reset-password`, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({
      email: body.email,
      token: body.token,
      password: body.password,
    }),
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
        : data || { message: "Unable to reset your password." },
      { status: response.status, headers: { "cache-control": "no-store" } },
    );
  }

  return Response.json(
    { ok: true, message: "Your password has been reset. You can now sign in." },
    { status: 200, headers: { "cache-control": "no-store" } },
  );
}
