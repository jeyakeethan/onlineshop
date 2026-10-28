"use client";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
type Detail = {
  subject?: string;
  description?: string;
  status?: string;
  priority?: string;
  messages?: Array<{
    messageId?: number;
    authorType?: string;
    message?: string;
    createdAt?: string;
    internalNote?: boolean;
  }>;
};
export default function TicketDetailPage() {
  const { ticketId } = useParams<{ ticketId: string }>();
  const [ticket, setTicket] = useState<Detail | null>(null);
  const [message, setMessage] = useState("Loading ticket...");
  useEffect(() => {
    fetch(`/api/bff/tickets/${encodeURIComponent(ticketId)}`, {
      cache: "no-store",
    })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to view this ticket."
              : r.status === 403
                ? "You do not have access to this ticket."
                : d?.message || "Ticket not found.",
          );
        setTicket(d);
        setMessage("");
      })
      .catch((e) =>
        setMessage(e instanceof Error ? e.message : "Unable to load ticket."),
      );
  }, [ticketId]);
  if (!ticket)
    return (
      <div className="page-container">
        <div className="empty">{message}</div>
      </div>
    );
  return (
    <div className="page-container">
      <Link href="/tickets" className="back-link">
        Back to tickets
      </Link>
      <div className="page-heading">
        <div>
          <p className="eyebrow">Support ticket</p>
          <h1>{ticket.subject}</h1>
          <p className="muted">
            {ticket.status} · {ticket.priority}
          </p>
        </div>
      </div>
      <section className="account-card">
        <h2>Original request</h2>
        <p>{ticket.description}</p>
      </section>
      <section className="inventory-list mt-6">
        {ticket.messages?.length ? (
          ticket.messages.map((item) => (
            <article className="inventory-product" key={item.messageId}>
              <p className="muted">
                {item.authorType} · {item.createdAt}
              </p>
              <p>{item.message}</p>
            </article>
          ))
        ) : (
          <div className="empty">No messages yet.</div>
        )}
      </section>
    </div>
  );
}
