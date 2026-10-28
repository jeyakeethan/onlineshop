"use client";
import { FormEvent, useState } from "react";
export default function ReviewEditForm({
  review,
  onUpdated,
  onError,
}: {
  review: { reviewId: number; reviewText?: string; rating?: number };
  onUpdated: (review: unknown) => void;
  onError: (message: string) => void;
}) {
  const [text, setText] = useState(review.reviewText || "");
  const [rating, setRating] = useState(String(review.rating || 5));
  const [busy, setBusy] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (busy || !text.trim()) return;
    setBusy(true);
    onError("");
    try {
      const response = await fetch(`/api/bff/reviews/${review.reviewId}`, {
        method: "PATCH",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({
          reviewText: text.trim(),
          rating: Number(rating),
        }),
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok)
        throw new Error(
          response.status === 401
            ? "Please sign in to edit this review."
            : response.status === 403
              ? "You can only edit your own review."
              : data?.message || "Unable to update review.",
        );
      onUpdated(data);
    } catch (reason) {
      onError(
        reason instanceof Error ? reason.message : "Unable to update review.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <form onSubmit={submit} className="stack-form">
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
          value={text}
          onChange={(e) => setText(e.target.value)}
          disabled={busy}
        />
      </label>
      <button className="primary-button" disabled={busy || !text.trim()}>
        {busy ? "Saving..." : "Save review"}
      </button>
    </form>
  );
}
