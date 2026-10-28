"use client";
import { useEffect, useState } from "react";
type Review = {
  reviewId?: number;
  productId?: number;
  rating?: number;
  reviewText?: string;
  moderationStatus?: string;
  createdAt?: string;
};
export default function MyReviews() {
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    fetch("/api/bff/me/reviews", { cache: "no-store" })
      .then(async (r) => {
        const d = await r.json().catch(() => ({}));
        if (!r.ok)
          throw new Error(
            r.status === 401
              ? "Please sign in to view your reviews."
              : d?.message || "Unable to load reviews.",
          );
        setReviews(Array.isArray(d) ? d : []);
      })
      .catch((e) =>
        setError(e instanceof Error ? e.message : "Unable to load reviews."),
      )
      .finally(() => setLoading(false));
  }, []);
  if (loading) return <p className="muted">Loading reviews...</p>;
  if (error)
    return (
      <p role="alert" className="form-error">
        {error}
      </p>
    );
  return reviews.length ? (
    <div className="inventory-list">
      {reviews.map((review) => (
        <article className="inventory-product" key={review.reviewId}>
          <h2>Product #{review.productId}</h2>
          <p>
            {"★".repeat(review.rating || 0)} ·{" "}
            {review.moderationStatus || "Pending moderation"}
          </p>
          <p>{review.reviewText}</p>
          <p className="muted">{review.createdAt || ""}</p>
        </article>
      ))}
    </div>
  ) : (
    <p className="muted">You have not written any reviews.</p>
  );
}
