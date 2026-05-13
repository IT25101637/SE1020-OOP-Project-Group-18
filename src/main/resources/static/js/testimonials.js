/**
 * Testimonials Loader - Display Top 3 Recent Reviews
 * Fetches top reviews from API and renders them in testimonials section
 */

const TESTIMONIALS_API = '/api/reviews/top';

/**
 * Load top 3 recent reviews and render in testimonials section
 */
async function loadTestimonials() {
  try {
    const response = await fetch(TESTIMONIALS_API);
    const data = await response.json();
    
    if (data.status === 'success' && data.reviews && data.reviews.length > 0) {
      const container = document.getElementById('testimonials-container');
      container.innerHTML = '';
      
      data.reviews.forEach(review => {
        const testimonialCard = createTestimonialCard(review);
        const colDiv = document.createElement('div');
        colDiv.className = 'col-md-4 mb-4';
        colDiv.innerHTML = testimonialCard;
        container.appendChild(colDiv);
      });
    } else {
      const container = document.getElementById('testimonials-container');
      container.innerHTML = '<div class="col-md-12"><div class="text-center text-muted">No reviews yet. Be the first to review!</div></div>';
    }
  } catch (error) {
    console.error('Error loading testimonials:', error);
    const container = document.getElementById('testimonials-container');
    container.innerHTML = '<div class="col-md-12"><div class="text-center text-muted">Could not load reviews at this time.</div></div>';
  }
}

/**
 * Create a testimonial card HTML element
 */
function createTestimonialCard(review) {
  const stars = '★'.repeat(review.rating) + '☆'.repeat(5 - review.rating);
  const truncatedComment = review.comment.length > 150 
    ? review.comment.substring(0, 150) + '...' 
    : review.comment;
  
  const reviewDate = new Date(review.updatedAt).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });

  return `
    <div class="card h-100 shadow-sm border-0">
      <div class="card-body">
        <div class="mb-3">
          <span style="color: #f0b429; font-size: 18px;">${stars}</span>
        </div>
        <p class="card-text" style="font-style: italic; color: #555; min-height: 80px;">
          "${truncatedComment}"
        </p>
        <div class="border-top pt-3 mt-3">
          <div style="font-weight: 700; color: #333;">${escapeHtml(review.username)}</div>
          <div class="small text-muted">${escapeHtml(review.bookTitle)}</div>
          <div class="small text-muted">${reviewDate}</div>
        </div>
      </div>
    </div>
  `;
}

/**
 * Escape HTML to prevent XSS
 */
function escapeHtml(value) {
  return String(value || '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

/**
 * Initialize testimonials on page load
 */
document.addEventListener('DOMContentLoaded', () => {
  loadTestimonials();
});
