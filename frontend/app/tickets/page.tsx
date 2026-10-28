"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
type Ticket = {
  ticketId?: number;
  subject?: string;
  description?: string;
  priority?: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
};
export default function TicketsPage() {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    setLoading(true);
    fetch(`/api/bff/tickets?page=${page}&size=20`, { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to view support tickets."
              : d?.message || "Unable to load tickets.",
          );
        setTickets(d.content || []);
        setTotalPages(d.totalPages || 1);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load tickets."),
      )
      .finally(() => setLoading(false));
  }, [page]);
  if (loading)
    return (
      <div className="page-container">
        <div className="empty">Loading tickets...</div>
      </div>
    );
  if (error)
    return (
      <div className="page-container">
        <div role="alert" className="empty">
          <h1>Support unavailable</h1>
          <p>{error}</p>
          <Link href="/auth/login" className="primary-button">
            Sign in
          </Link>
        </div>
      </div>
    );
  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Customer support</p>
          <h1>Your tickets</h1>
        </div>
      </div>
      {tickets.length ? (
        <div className="inventory-list">
          {tickets.map((ticket) => (
            <Link
              href={`/tickets/${ticket.ticketId}`}
              className="inventory-product"
              key={ticket.ticketId}
            >
              <h2>{ticket.subject}</h2>
              <p>{ticket.description}</p>
              <p className="muted">
                {ticket.status} · {ticket.priority} ·{" "}
                {ticket.updatedAt || ticket.createdAt || ""}
              </p>
            </Link>
          ))}
        </div>
      ) : (
        <div className="empty">No support tickets found.</div>
      )}
      <div className="page-actions">
        <button
          className="back-link"
          disabled={page === 0}
          onClick={() => setPage((current) => current - 1)}
        >
          Previous
        </button>
        <span>Page {page + 1}</span>
        <button
          className="back-link"
          disabled={page + 1 >= totalPages}
          onClick={() => setPage((current) => current + 1)}
        >
          Next
        </button>
      </div>
    </div>
  );
}
