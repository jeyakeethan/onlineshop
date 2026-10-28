import { cookies } from "next/headers";

const BACKEND = process.env.BACKEND_BASE_URL!;

function expiredTokenCookie() {
  const secure = process.env.NODE_ENV === "production" ? "; Secure" : "";
  return `token=; Max-Age=0; Path=/; HttpOnly; SameSite=Lax${secure}`;
}

export async function POST() {
  const token = (await cookies()).get("token")?.value;
  let backendStatus = 200;
  let message = "Logged out successfully.";

  if (token) {
    try {
      const response = await fetch(`${BACKEND}/api/v1/auth/logout`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        cache: "no-store",
      });

      backendStatus = response.status;
      if (!response.ok) {
        message =
          "Your local session was cleared, but the server could not revoke the session.";
      }
    } catch {
      backendStatus = 502;
      message =
        "Your local session was cleared, but the server could not be reached.";
    }
  }

  const response = Response.json(
    { ok: backendStatus === 200, message },
    { status: backendStatus === 401 ? 200 : backendStatus },
  );
  response.headers.set("Set-Cookie", expiredTokenCookie());
  response.headers.set("Cache-Control", "no-store");
  return response;
}
