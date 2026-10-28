"use client";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { OrderDTO } from "../../../../types/api";
import { formatMoney } from "../../../../lib/storefront";
export default function AdminOrderDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [order, setOrder] = useState<OrderDTO | null>(null);
  const [message, setMessage] = useState("Loading order...");
  useEffect(() => {
    fetch(`/api/orders/admin/${encodeURIComponent(id)}`, { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to access admin orders."
              : r.status === 403
                ? "You do not have permission to view this order."
                : d?.message || "Order not found.",
          );
        setOrder(d);
        setMessage("");
      })
      .catch((e) =>
        setMessage(e instanceof Error ? e.message : "Unable to load order."),
      );
  }, [id]);
  if (!order)
    return (
      <div className="page-container">
        <div className="empty">{message}</div>
      </div>
    );
  return (
    <div className="page-container">
      <Link href="/admin/orders" className="back-link">
        Back to orders
      </Link>
      <div className="page-heading">
        <div>
          <p className="eyebrow">Admin order #{order.orderId}</p>
          <h1>{order.orderStatus || "Processing"}</h1>
          <p className="muted">
            Payment: {order.paymentStatus || "Pending"} · Shipping:{" "}
            {order.shippingMethod || "Not selected"}
          </p>
        </div>
      </div>
      <section className="detail-items">
        {(order.orderItems || []).map((item, index) => (
          <article className="detail-item" key={index}>
            <div>
              <h3>{item.skuDTO?.skuCode || "Selected item"}</h3>
              <p className="muted">Quantity {item.quantity || 1}</p>
            </div>
            <strong>
              {formatMoney((item.price || 0) * (item.quantity || 1))}
            </strong>
          </article>
        ))}
        <div className="summary-line">
          <span>Subtotal</span>
          <strong>{formatMoney(order.subtotal || 0)}</strong>
        </div>
        <div className="summary-line">
          <span>Tax</span>
          <strong>{formatMoney(order.tax || 0)}</strong>
        </div>
        <div className="summary-line summary-total">
          <span>Total</span>
          <strong>{formatMoney(order.totalPrice || 0)}</strong>
        </div>
      </section>
      {order.statusHistory?.length ? (
        <section className="account-card mt-6">
          <p className="eyebrow">Status history</p>
          {order.statusHistory.map((entry, index) => (
            <p className="muted" key={`${entry.changedAt}-${index}`}>
              {entry.status} · {entry.changedAt} {entry.note || ""}
            </p>
          ))}
        </section>
      ) : null}
    </div>
  );
}
