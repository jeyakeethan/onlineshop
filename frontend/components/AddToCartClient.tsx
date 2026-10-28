"use client";
import React, { useState } from "react";
import { addGuestCartItem } from "../lib/guestCart";

export default function AddToCartClient({
  inventoryIdProp,
}: {
  inventoryIdProp?: number;
}) {
  const [inventoryId, setInventoryId] = useState<string>(
    inventoryIdProp ? String(inventoryIdProp) : "",
  );
  const [quantity, setQuantity] = useState<number>(1);
  const [status, setStatus] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  async function addToCart() {
    if (
      saving ||
      !Number(inventoryId) ||
      !Number.isInteger(quantity) ||
      quantity < 1
    ) {
      setStatus("Enter a valid inventory and quantity.");
      return;
    }
    setStatus(null);
    setSaving(true);
    try {
      const res = await fetch("/api/cart", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ inventoryId: Number(inventoryId), quantity }),
      });
      if (res.ok) setStatus("Added to cart");
      else if (res.status === 401) {
        addGuestCartItem({ inventoryId: Number(inventoryId), quantity });
        setStatus("Saved to your guest cart");
      }
      else {
        const data = await res.json().catch(() => ({}));
        setStatus(
          res.status === 409
            ? data?.message || "That quantity is not available."
            : res.status === 401
              ? "Please sign in to add items."
              : data?.message || "Error adding to cart",
        );
      }
    } catch (e) {
      setStatus("Network error");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="mt-4">
      <div className="grid grid-cols-2 gap-2 items-end">
        <div>
          <label className="text-sm">Inventory ID</label>
          <input
            value={inventoryId}
            onChange={(e) => setInventoryId(e.target.value)}
            className="mt-1 block w-full border rounded px-2 py-1"
          />
        </div>
        <div>
          <label className="text-sm">Quantity</label>
          <input
            type="number"
            min={1}
            value={quantity}
            onChange={(e) => setQuantity(Number(e.target.value))}
            className="mt-1 block w-full border rounded px-2 py-1"
          />
        </div>
      </div>
      <div className="mt-3">
        <button
          onClick={addToCart}
          disabled={saving}
          className="bg-green-600 text-white px-4 py-2 rounded"
        >
          {saving ? "Adding..." : "Add to cart"}
        </button>
      </div>
      {status && <div className="mt-2 text-sm">{status}</div>}
    </div>
  );
}
