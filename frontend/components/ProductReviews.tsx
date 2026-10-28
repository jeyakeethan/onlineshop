"use client";

import { useEffect, useState } from "react";

type Review = {
  id?: number;
  userId?: string;
  reviewText?: string;
  rating?: number;
};

export function ProductReviews({ productId }: { productId?: number }) {
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!productId) {
      setLoading(false);
      return;
    }
    fetch(`/api/bff/products/${productId}/reviews`, { cache: "no-store" })
      .then(async (response) => {
        const data = await response.json().catch(() => []);
        if (response.ok) setReviews(Array.isArray(data) ? data : []);
      })
      .catch(() => setReviews([]))
      .finally(() => setLoading(false));
  }, [productId]);

  if (loading)
    return (
      <section className="mt-12">
        <p className="muted">Loading reviews...</p>
      </section>
    );
  if (!reviews.length)
    return (
      <section className="mt-12">
        <p className="eyebrow">Reviews</p>
        <p className="muted">No approved reviews yet.</p>
      </section>
    );

  const average =
    reviews.reduce((sum, review) => sum + (review.rating || 0), 0) /
    reviews.length;
  return (
    <section className="mt-12">
      <div className="flex items-baseline gap-3">
        <p className="eyebrow">Reviews</p>
        <span className="muted">
          {average.toFixed(1)} / 5 · {reviews.length} review
          {reviews.length === 1 ? "" : "s"}
        </span>
      </div>
      <div className="space-y-3 mt-4">
        {reviews.map((review) => (
          <article key={review.id} className="border-b pb-3">
            <p aria-label={`${review.rating || 0} out of 5 stars`}>
              {"★".repeat(review.rating || 0)}
              {"☆".repeat(Math.max(0, 5 - (review.rating || 0)))}
            </p>
            <p>{review.reviewText}</p>
            <p className="muted text-sm">
              {review.userId
                ? `Verified customer ${review.userId}`
                : "Verified customer"}
            </p>
          </article>
        ))}
      </div>
    </section>
  );
}
