import { cookies } from "next/headers";

const BACKEND = process.env.BACKEND_BASE_URL!;
const actions = {
  "revoke-all": "/api/v1/me/sessions/revoke-all",
  deactivate: "/api/v1/me/deactivation-request",
  delete: "/api/v1/me/deletion-request",
} as const;

export async function POST(
  _req: Request,
  context: { params: Promise<{ action: string }> },
) {
  const { action } = await context.params;
  const backendPath = actions[action as keyof typeof actions];
  if (!backendPath)
    return Response.json(
      { message: "Unknown account action." },
      { status: 404 },
    );

  const token = (await cookies()).get("token")?.value;
  if (!token)
    return Response.json({ message: "Please sign in again." }, { status: 401 });

  try {
    const response = await fetch(`${BACKEND}${backendPath}`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
      cache: "no-store",
    });
    const text = await response.text();
    const result = {
      message: text || (response.ok ? "Request completed." : "Request failed."),
    };
    const nextResponse = Response.json(result, { status: response.status });
    if (action === "revoke-all" && response.ok) {
      const secure = process.env.NODE_ENV === "production" ? "; Secure" : "";
      nextResponse.headers.set(
        "Set-Cookie",
        `token=; Max-Age=0; Path=/; HttpOnly; SameSite=Lax${secure}`,
      );
    }
    nextResponse.headers.set("Cache-Control", "no-store");
    return nextResponse;
  } catch {
    return Response.json(
      { message: "The account service is unavailable." },
      { status: 502 },
    );
  }
}
