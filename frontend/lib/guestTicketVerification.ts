export async function requestGuestTicketVerification(
  ticketId: number,
  email: string,
) {
  const response = await fetch("/api/guest-tickets/verification/request", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ ticketId, email }),
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok)
    throw new Error(data?.message || "Unable to process verification request.");
  return data as { message?: string };
}
export async function verifyGuestTicket(ticketId: number, code: string) {
  const response = await fetch("/api/guest-tickets/verification/verify", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ ticketId, code }),
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok)
    throw new Error(data?.message || "Unable to verify guest ticket.");
  return data as { ticketId: number; accessToken: string; expiresAt: string };
}
