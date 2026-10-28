const BACKEND = process.env.BACKEND_BASE_URL!;
export async function PATCH(
  req: Request,
  { params }: { params: Promise<{ templateId: string }> },
) {
  const { templateId } = await params;
  const body = await req.text();
  try {
    const payload = JSON.parse(body);
    if (
      (payload.subject && payload.subject.length > 200) ||
      (payload.body && payload.body.length > 100000) ||
      (payload.allowedVariables && payload.allowedVariables.length > 2000)
    )
      return Response.json(
        { message: "Email-template fields exceed allowed lengths." },
        { status: 400 },
      );
  } catch {
    return Response.json(
      { message: "Invalid email-template update." },
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
      `${BACKEND}/api/v1/admin/email-templates/${encodeURIComponent(templateId)}`,
      { method: "PATCH", headers, body, cache: "no-store" },
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
      { message: "Unable to update email template." },
      { status: 502 },
    );
  }
}
