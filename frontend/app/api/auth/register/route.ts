const BACKEND = process.env.BACKEND_BASE_URL!;

export async function POST(req: Request) {
  const body = await req.json();
  const resp = await fetch(`${BACKEND}/api/v1/auth/register`, {
    method: "POST",
    body: JSON.stringify({
      email: body.email,
      firstName: body.firstName,
      lastName: body.lastName,
      password: body.password,
      dob: body.dob || null,
    }),
    headers: { "content-type": "application/json" },
  });

  const contentType = resp.headers.get("content-type") || "";
  const isJson = contentType.includes("application/json");
  const data = isJson ? await resp.json() : await resp.text();

  if (resp.ok) {
    return Response.json({
      ok: true,
      message: typeof data === "string" ? data : data?.message,
    });
  }

  return Response.json(typeof data === "string" ? { message: data } : data, {
    status: resp.status,
  });
}
