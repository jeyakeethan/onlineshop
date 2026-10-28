const BACKEND = process.env.BACKEND_BASE_URL!;

export async function POST(req: Request) {
  const body = await req.json();
  const resp = await fetch(`${BACKEND}/api/v1/auth/login`, {
    method: "POST",
    body: JSON.stringify({ userId: body.userId, password: body.password }),
    headers: { "content-type": "application/json" },
  });
  const contentType = resp.headers.get("content-type") || "";
  const data = contentType.includes("application/json")
    ? await resp.json().catch(() => ({}))
    : await resp.text();

  // backend returns { token }
  if (resp.ok && data?.token) {
    const headers = new Headers();
    // set HttpOnly cookie
    const secure = process.env.NODE_ENV === "production" ? "; Secure" : "";
    headers.append(
      "Set-Cookie",
      `token=${data.token}; HttpOnly; Path=/; SameSite=Lax${secure}`,
    );
    headers.set("Cache-Control", "no-store");
    return new Response(JSON.stringify({ ok: true }), {
      status: 200,
      headers: {
        ...Object.fromEntries(headers.entries()),
        "content-type": "application/json",
      },
    });
  }

  const error = typeof data === "string" ? { message: data } : data;
  return new Response(JSON.stringify(error || { message: "Login failed." }), {
    status: resp.status,
    headers: {
      "content-type": "application/json",
      "cache-control": "no-store",
    },
  });
}
