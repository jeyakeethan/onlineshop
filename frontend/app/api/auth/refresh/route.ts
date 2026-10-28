import { cookies } from "next/headers";

const BACKEND = process.env.BACKEND_BASE_URL!;

function expiredTokenCookie() {
  const secure = process.env.NODE_ENV === "production" ? "; Secure" : "";
  return `token=; Max-Age=0; Path=/; HttpOnly; SameSite=Lax${secure}`;
}

function tokenCookie(token: string) {
  const secure = process.env.NODE_ENV === "production" ? "; Secure" : "";
  return `token=${token}; HttpOnly; Path=/; SameSite=Lax${secure}`;
}

export async function POST() {
  const token = (await cookies()).get("token")?.value;
  if (!token) {
    const response = Response.json(
      { message: "No active session." },
      { status: 401 },
    );
    response.headers.set("Set-Cookie", expiredTokenCookie());
    return response;
  }

  try {
    const backendResponse = await fetch(`${BACKEND}/api/v1/auth/refresh`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
      cache: "no-store",
    });
    const contentType = backendResponse.headers.get("content-type") || "";
    const data = contentType.includes("application/json")
      ? await backendResponse.json().catch(() => ({}))
      : await backendResponse.text();

    if (!backendResponse.ok || !data?.token) {
      const response = Response.json(
        typeof data === "string"
          ? { message: data }
          : data || { message: "Session refresh failed." },
        { status: backendResponse.status || 401 },
      );
      response.headers.set("Set-Cookie", expiredTokenCookie());
      response.headers.set("Cache-Control", "no-store");
      return response;
    }

    const response = Response.json({ ok: true }, { status: 200 });
    response.headers.set("Set-Cookie", tokenCookie(data.token));
    response.headers.set("Cache-Control", "no-store");
    return response;
  } catch {
    const response = Response.json(
      { message: "Unable to refresh the session." },
      { status: 502 },
    );
    response.headers.set("Set-Cookie", expiredTokenCookie());
    response.headers.set("Cache-Control", "no-store");
    return response;
  }
}
