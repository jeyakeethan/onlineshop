"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { OrderDTO } from "../../../types/api";
import { formatMoney } from "../../../lib/storefront";

const statuses = ["PENDING", "FULFILLED", "PAID", "FAILED", "CANCELLED"];

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState<OrderDTO[]>([]);
  const [message, setMessage] = useState("Loading orders...");
  const [statusFilter, setStatusFilter] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  async function load() {
    const query = new URLSearchParams({ page: String(page), size: "20" });
    if (statusFilter) query.set("status", statusFilter);
    const response = await fetch(`/api/orders/admin?${query}`, {
      cache: "no-store",
    });
    if (!response.ok)
      throw new Error(
        response.status === 403
          ? "You do not have permission to view orders."
          : response.status === 401
            ? "Please sign in to access admin orders."
            : "Could not load orders.",
      );
    const data = await response.json();
    setOrders(Array.isArray(data) ? data : data.content || []);
    setTotalPages(data.totalPages || 1);
    setMessage("");
  }

  useEffect(() => {
    load().catch((reason) =>
      setMessage(
        reason instanceof Error ? reason.message : "Could not load orders.",
      ),
    );
  }, [page, statusFilter]);

  async function updateStatus(orderId: number | undefined, status: string) {
    if (!orderId) return;
    setMessage("Updating order...");
    const response = await fetch(`/api/orders/admin/${orderId}/status`, {
      method: "PATCH",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ status }),
    });
    if (!response.ok) {
      setMessage("Could not update order status.");
      return;
    }
    await load();
  }

  return (
    <div className="page-container">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Operations</p>
          <h1>Orders</h1>
        </div>
        <Link href="/admin/products" className="back-link">
          Inventory
        </Link>
      </div>
      {message && <p className="status">{message}</p>}
      <div className="orders-list">
        {orders.map((order) => (
          <article className="order-row" key={order.orderId}>
            <div>
              <p className="eyebrow">Order #{order.orderId}</p>
              <h2>{formatMoney(order.totalPrice || 0)}</h2>
              <p className="muted">
                {order.orderDate || "Recent order"} ·{" "}
                {order.orderItems?.length || 0} lines
              </p>
            </div>
            <div className="order-meta">
              <span>Status</span>
              <select
                value={order.orderStatus || "PENDING"}
                onChange={(event) =>
                  updateStatus(order.orderId, event.target.value)
                }
              >
                {statuses.map((status) => (
                  <option key={status}>{status}</option>
                ))}
              </select>
              <Link href={`/orders/${order.orderId}`} className="back-link">
                View order
              </Link>
            </div>
          </article>
        ))}
        {!message && !orders.length && (
          <div className="empty">No orders found.</div>
        )}
      </div>
    </div>
  );
}
