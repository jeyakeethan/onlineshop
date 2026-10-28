"use client";
import { useEffect, useState } from "react";
type ReturnRequest = {
  returnRequestId?: number;
  status?: string;
  reason?: string;
  requestedAt?: string;
  refundStatus?: string;
};
export default function OrderReturns({ orderId }: { orderId: number }) {
  const [returns, setReturns] = useState<ReturnRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    fetch(`/api/orders/${orderId}/returns`, { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to view returns."
              : d?.message || "Unable to load returns.",
          );
        setReturns(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load returns."),
      )
      .finally(() => setLoading(false));
  }, [orderId]);
  if (loading) return <p className="muted">Loading returns...</p>;
  if (error)
    return (
      <p role="alert" className="form-error">
        {error}
      </p>
    );
  return returns.length ? (
    <div>
      {returns.map((item) => (
        <p className="muted" key={item.returnRequestId}>
          Return #{item.returnRequestId}: {item.status || "Requested"} ·{" "}
          {item.reason || "No reason"} · Refund {item.refundStatus || "Pending"}
        </p>
      ))}
    </div>
  ) : (
    <p className="muted">No return requests.</p>
  );
}
