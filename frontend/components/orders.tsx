"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { OrderDTO, UserDTO } from "../types/api";
import { formatMoney } from "../lib/storefront";

function request(path: string, init?: RequestInit) {
  return fetch(path, init).then(async (response) => {
    if (!response.ok) throw new Error(String(response.status));
    const text = await response.text();
    if (!text) return null;
    try {
      return JSON.parse(text);
    } catch {
      return text;
    }
  });
}

const orderStages = ["PENDING", "FULFILLED", "PAID"];

function statusLabel(status?: string) {
  return status === "PENDING"
    ? "Processing"
    : status === "FULFILLED"
      ? "Reserved"
      : status === "PAID"
        ? "Paid"
        : status || "Processing";
}

function StatusTracker({ status }: { status?: string }) {
  if (status === "FAILED" || status === "CANCELLED")
    return (
      <p className="order-status-failed">
        {status === "FAILED"
          ? "Payment or fulfillment failed."
          : "This order was cancelled."}
      </p>
    );
  const activeIndex = Math.max(0, orderStages.indexOf(status || "PENDING"));
  return (
    <div
      className="status-tracker"
      aria-label={`Order status: ${statusLabel(status)}`}
    >
      {orderStages.map((stage, index) => (
        <div
          className={
            index <= activeIndex ? "status-step active" : "status-step"
          }
          key={stage}
        >
          <span>{index + 1}</span>
          <small>{statusLabel(stage)}</small>
        </div>
      ))}
    </div>
  );
}

export function OrdersView() {
  const [orders, setOrders] = useState<OrderDTO[]>([]);
  const [message, setMessage] = useState("Loading your orders...");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  useEffect(() => {
    request(`/api/orders?page=${page}&size=20`)
      .then((data: { content?: OrderDTO[]; totalPages?: number }) => {
        setOrders(data.content || []);
        setTotalPages(data.totalPages || 1);
        setMessage("");
      })
      .catch((reason) =>
        setMessage(
          reason?.message === "401"
            ? "Please sign in to view your orders."
            : "We could not load your orders.",
        ),
      );
  }, [page]);

  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Your account</p>
          <h1>Orders</h1>
        </div>
        <Link href="/products" className="back-link">
          Shop collection
        </Link>
      </div>
      {message ? (
        <div className="empty">{message}</div>
      ) : orders.length ? (
        <>
          <div className="orders-list">
            {orders.map((order) => (
              <Link
                className="order-row"
                href={`/orders/${order.orderId}`}
                key={order.orderId}
              >
                <div>
                  <p className="eyebrow">Order #{order.orderId}</p>
                  <h2>{order.orderStatus || "Processing"}</h2>
                  <p className="muted">{order.orderDate || "Recent order"}</p>
                </div>
                <div className="order-meta">
                  <strong>{formatMoney(order.totalPrice || 0)}</strong>
                  <span>{order.orderItems?.length || 0} items</span>
                </div>
              </Link>
            ))}
          </div>
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
        </>
      ) : (
        <div className="empty">
          <h2>No orders yet.</h2>
          <Link href="/products" className="primary-button">
            Explore the collection
          </Link>
        </div>
      )}
    </div>
  );
}

export function OrderDetailView({ id }: { id: string }) {
  const [order, setOrder] = useState<OrderDTO | null>(null);
  const [message, setMessage] = useState("Loading order...");

  useEffect(() => {
    let timer: number | undefined;
    const load = () =>
      request(`/api/orders/${id}`)
        .then((data: OrderDTO) => {
          setOrder(data);
          setMessage("");
          if (
            data.orderStatus === "PENDING" ||
            data.orderStatus === "FULFILLED"
          )
            timer = window.setTimeout(load, 3000);
        })
        .catch(() => setMessage("We could not load this order."));
    load();
    return () => {
      if (timer) window.clearTimeout(timer);
    };
  }, [id]);

  async function cancel() {
    if (!window.confirm("Cancel this order?")) return;
    setMessage("Cancelling order...");
    try {
      await request(`/api/orders/${id}/cancel`, {
        method: "POST",
        headers: { "Idempotency-Key": `cancel-${id}` },
      });
      setOrder((current) =>
        current ? { ...current, orderStatus: "CANCELLED" } : current,
      );
      setMessage("Order cancelled.");
    } catch {
      setMessage("We could not cancel this order.");
    }
  }

  async function downloadReceipt() {
    try {
      const response = await fetch(`/api/orders/${id}/receipt`, {
        cache: "no-store",
      });
      if (!response.ok)
        throw new Error(
          response.status === 403
            ? "You do not have permission to view this receipt."
            : "Receipt unavailable.",
        );
      const data = await response.json();
      const blob = new Blob([JSON.stringify(data, null, 2)], {
        type: "application/json",
      });
      const url = URL.createObjectURL(blob);
      const anchor = document.createElement("a");
      anchor.href = url;
      anchor.download = `order-${id}-receipt.json`;
      anchor.click();
      URL.revokeObjectURL(url);
    } catch (reason) {
      setMessage(
        reason instanceof Error ? reason.message : "Receipt unavailable.",
      );
    }
  }

  async function reorder() {
    try {
      const response = await fetch(`/api/orders/${id}/reorder`, {
        method: "POST",
        headers: { "Idempotency-Key": `reorder-${id}-${Date.now()}` },
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(data?.message || "Unable to reorder this order.");
      setMessage(
        data?.unavailableItems?.length
          ? `Added available items. Unavailable: ${data.unavailableItems.join(", ")}`
          : "Available items added to your cart.",
      );
    } catch (reason) {
      setMessage(
        reason instanceof Error
          ? reason.message
          : "Unable to reorder this order.",
      );
    }
  }

  if (!order)
    return (
      <div className="page-container">
        <div className="empty">{message}</div>
      </div>
    );
  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Order #{order.orderId}</p>
          <h1>Order details</h1>
        </div>
        <Link href="/orders" className="back-link">
          All orders
        </Link>
      </div>
      <div className="order-detail">
        <section className="account-card">
          <p className="eyebrow">Status</p>
          <h2>{statusLabel(order.orderStatus)}</h2>
          <p className="muted">
            Placed {order.orderDate || "recently"} · Payment{" "}
            {order.paymentStatus || "Pending"}
          </p>
          <StatusTracker status={order.orderStatus} />
          {order.shippingMethod && (
            <p>
              Shipping: {order.shippingMethod}
              {order.trackingNumber
                ? ` · Tracking ${order.trackingNumber}`
                : ""}
            </p>
          )}
          {order.shippingAddress && (
            <div className="address-block">
              <p className="eyebrow">Delivery address</p>
              <p>
                {order.shippingAddress.street}
                <br />
                {order.shippingAddress.city} {order.shippingAddress.postalCode}
                <br />
                {order.shippingAddress.country}
              </p>
            </div>
          )}
          {order.orderStatus !== "CANCELLED" &&
            order.orderStatus !== "PAID" && (
              <button className="secondary-button" onClick={cancel}>
                Cancel order
              </button>
            )}
          {message && <p className="form-error">{message}</p>}
          {order.statusHistory?.length ? (
            <div>
              <p className="eyebrow">Status history</p>
              {order.statusHistory.map((entry, index) => (
                <p className="muted" key={`${entry.changedAt}-${index}`}>
                  {entry.status} · {entry.changedAt}{" "}
                  {entry.note ? `· ${entry.note}` : ""}
                </p>
              ))}
            </div>
          ) : null}
        </section>
        <section className="detail-items">
          {(order.orderItems || []).map((item, index) => (
            <article className="detail-item" key={index}>
              <div className="detail-thumb">
                <span>{String(index + 1).padStart(2, "0")}</span>
              </div>
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
            <span>Shipping</span>
            <strong>{formatMoney(order.shippingFee || 0)}</strong>
          </div>
          <div className="summary-line summary-total">
            <span>Total</span>
            <strong>{formatMoney(order.totalPrice || 0)}</strong>
          </div>
        </section>
      </div>
    </div>
  );
}
