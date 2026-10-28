"use client";
import { FormEvent, useState } from "react";
export default function ReviewCreateForm({
  productId,
  onCreated,
  onError,
}: {
  productId: number;
  onCreated: (review: unknown) => void;
  onError: (message: string) => void;
}) {
  const [rating, setRating] = useState("5");
  const [reviewText, setReviewText] = useState("");
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !reviewText.trim()) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch("/api/bff/reviews", {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          productId,
          rating: Number(rating),
          reviewText: reviewText.trim(),
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to review this product."
            : response.status === 403
              ? "Only eligible verified purchasers can review this product."
              : data?.message || "Unable to submit review.",
        );
      onCreated(data);
      setReviewText("");
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to submit review.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
      <h2>Write a review</h2>
      <label>
        Rating
        <select
          value={rating}
          onChange={(e) => setRating(e.target.value)}
          disabled={busy}
        >
          {[5, 4, 3, 2, 1].map((value) => (
            <option key={value}>{value}</option>
          ))}
        </select>
      </label>
      <label>
        Review
        <textarea
          required
          value={reviewText}
          onChange={(e) => setReviewText(e.target.value)}
          disabled={busy}
        />
      </label>
      <button className="primary-button" disabled={busy || !reviewText.trim()}>
        {busy ? "Submitting..." : "Submit review"}
      </button>
    </form>
  );
}
