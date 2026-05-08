/**
 * Book Loader - Dynamic Book Display from API
 * Fetches books from REST API and renders them dynamically on the homepage
 */

const API_BASE_URL = 'http://localhost:8091/api/books';

/**
 * Load featured books and render in the featured section
 */
async function loadFeaturedBooks() {
  try {
    const response = await fetch(`${API_BASE_URL}/featured`);
    const data = await response.json();
    
    if (data.status === 'success' && data.books && data.books.length > 0) {
      const container = document.querySelector('#featured-books .product-list .row');
      container.innerHTML = '';
      
      data.books.forEach(book => {
        const bookCard = createBookCard(book);
        container.innerHTML += bookCard;
      });

      attachAddToCartHandlers(container);
    } else {
      console.log('No featured books available');
    }
  } catch (error) {
    console.error('Error loading featured books:', error);
  }
}

/**
 * Load books by category and render in the tab
 */
async function loadBooksByCategory(category) {
  try {
    const endpoint = category === 'all-genre' 
      ? `${API_BASE_URL}` 
      : `${API_BASE_URL}/category/${category}`;
    
    const response = await fetch(endpoint);
    const data = await response.json();
    
    if (data.status === 'success' && data.books && data.books.length > 0) {
      const tabContent = document.querySelector(`#${category}[data-tab-content]`);
      if (tabContent) {
        tabContent.innerHTML = '';
        
        // Limit to 4 books per category for cleaner display
        const booksToShow = data.books.slice(0, 4);
        const totalBooks = data.books.length;
        
        // Create rows with 4 columns each
        let rowHtml = '<div class="row">';
        booksToShow.forEach((book, index) => {
          rowHtml += createBookCard(book);
          
          // Close row and start new one after every 4 items
          if ((index + 1) % 4 === 0 && index + 1 < booksToShow.length) {
            rowHtml += '</div><div class="row">';
          }
        });
        rowHtml += '</div>';
        
        // Add "Load More" indicator if there are more books
        if (totalBooks > 4) {
          rowHtml += `<div class="row mt-3 text-center"><p class="text-muted">Showing 4 of ${totalBooks} books. <a href="#" class="text-decoration-none">View all →</a></p></div>`;
        }
        
        tabContent.innerHTML = rowHtml;
        attachAddToCartHandlers(tabContent);
      }
    } else {
      const tabContent = document.querySelector(`#${category}[data-tab-content]`);
      if (tabContent) {
        tabContent.innerHTML = '<div class="row"><p class="text-center">No books available in this category.</p></div>';
      }
    }
  } catch (error) {
    console.error(`Error loading books for category ${category}:`, error);
  }
}

/**
 * Create a book card HTML element
 */
function createBookCard(book) {
  // Use a placeholder image if imageUrl is not provided
  const imageUrl = book.imageUrl && book.imageUrl.trim() 
    ? book.imageUrl 
    : 'images/product-placeholder.jpg';
  
  return `
    <div class="col-md-3">
      <div class="product-item">
        <figure class="product-style">
          <img src="${imageUrl}" alt="${book.title}" class="product-item" onerror="this.src='images/product-placeholder.jpg'">
          <button
            type="button"
            class="add-to-cart"
            data-product-tile="add-to-cart"
            data-book-id="${book.bookId}"
            data-title="${escapeHtml(book.title)}"
            data-author="${escapeHtml(book.author)}"
            data-price="${parseFloat(book.price).toFixed(2)}"
            data-image-url="${escapeHtml(imageUrl)}"
          >Add to Cart</button>
        </figure>
        <figcaption>
          <h3>${book.title}</h3>
          <span>${book.author}</span>
          <div class="item-price">$ ${parseFloat(book.price).toFixed(2)}</div>
          <a href="book-details.html?bookId=${encodeURIComponent(book.bookId)}" class="text-decoration-none" style="color:#2fa0e3;font-weight:600;font-size:13px;">View Details & Reviews</a>
        </figcaption>
      </div>
    </div>
  `;
}

function attachAddToCartHandlers(containerElement) {
  const buttons = containerElement.querySelectorAll('.add-to-cart[data-book-id]');
  buttons.forEach(button => {
    button.addEventListener('click', async () => {
      if (!window.BookstoreCart || !window.BookstoreCart.requireLoginForCart()) {
        return;
      }

      const originalText = button.textContent;
      button.disabled = true;
      button.textContent = 'Adding...';

      try {
        await window.BookstoreCart.addToCart({
          bookId: button.dataset.bookId,
          title: button.dataset.title,
          author: button.dataset.author,
          price: Number(button.dataset.price),
          imageUrl: button.dataset.imageUrl,
          quantity: 1
        });

        button.textContent = 'Added';
        setTimeout(() => {
          button.textContent = originalText;
          button.disabled = false;
        }, 1000);
      } catch (error) {
        console.error('Error adding to cart:', error);
        alert(error.message || 'Failed to add item to cart');
        button.textContent = originalText;
        button.disabled = false;
      }
    });
  });
}

function escapeHtml(value) {
  return String(value || '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

/**
 * Setup tab click handlers for popular books
 */
function setupTabHandlers() {
  const tabs = document.querySelectorAll('.tabs .tab');
  
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      // Remove active class from all tabs
      tabs.forEach(t => t.classList.remove('active'));
      
      // Add active class to clicked tab
      tab.classList.add('active');
      
      // Hide all tab contents
      document.querySelectorAll('[data-tab-content]').forEach(content => {
        content.classList.remove('active');
      });
      
      // Show selected tab content
      const tabTarget = tab.getAttribute('data-tab-target');
      const targetContent = document.querySelector(`${tabTarget}[data-tab-content]`);
      if (targetContent) {
        targetContent.classList.add('active');
        
        // Extract category from tab target (e.g., '#all-genre' -> 'all-genre')
        const category = tabTarget.substring(1);
        loadBooksByCategory(category);
      }
    });
  });
}

/**
 * Initialize book loader on page load
 */
document.addEventListener('DOMContentLoaded', () => {
  loadFeaturedBooks();
  setupTabHandlers();
  
  // Load initial tab content (all-genre)
  loadBooksByCategory('all-genre');
});
