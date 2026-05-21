/**
 * Book Loader - Dynamic Book Display from API
 * Fetches books from REST API and renders them dynamically on the homepage
 */

const API_BASE_URL = 'http://localhost:8091/api/books';

// Track current sort per category tab
const currentSort = {};

/**
 * Build the API endpoint for a given category and sort option.
 * @param {string} category
 * @param {string} sort - 'default' | 'price' | 'name'
 */
function buildEndpoint(category, sort) {
  const base = category === 'all-genre'
    ? `${API_BASE_URL}`
    : `${API_BASE_URL}/category/${category}`;

  if (sort === 'price') return `${API_BASE_URL}/category/${category}/sort/price`;
  if (sort === 'name')  return `${API_BASE_URL}/category/${category}/sort/name`;
  return base;
}

/**
 * Render a sort toolbar for the given category tab container.
 */
function createSortBar(category, tabContent) {
  const existing = tabContent.querySelector('.sort-bar');
  if (existing) existing.remove();

  const bar = document.createElement('div');
  bar.className = 'sort-bar';
  bar.style.cssText = 'display:flex;align-items:center;gap:8px;margin-bottom:16px;flex-wrap:wrap;';

  const label = document.createElement('span');
  label.textContent = 'Sort by:';
  label.style.cssText = 'font-size:13px;color:#888;font-weight:600;';
  bar.appendChild(label);

  const options = [
    { value: 'default', label: 'Default' },
    { value: 'price',   label: 'Price ↑' },
    { value: 'name',    label: 'Name A–Z' },
  ];

  const active = currentSort[category] || 'default';

  options.forEach(opt => {
    const btn = document.createElement('button');
    btn.textContent = opt.label;
    btn.dataset.sort = opt.value;
    btn.style.cssText = `
      padding:4px 12px;border-radius:20px;border:1px solid #c8a96e;background:${opt.value === active ? '#c8a96e' : '#fff'};
      color:${opt.value === active ? '#fff' : '#c8a96e'};font-size:12px;font-weight:600;cursor:pointer;transition:all .2s;
    `;
    btn.addEventListener('click', () => {
      currentSort[category] = opt.value;
      loadBooksByCategory(category);
    });
    bar.appendChild(btn);
  });

  return bar;
}

/**
 * Load featured books and render in the featured section
 */
async function loadFeaturedBooks() {
  try {
    const response = await fetch(`${API_BASE_URL}/featured`);
    const data = await response.json();
    
    if (data.status === 'success' && data.books && data.books.length > 0) {
      const productList = document.querySelector('#featured-books .product-list');
      
      // Render first 4 books
      const booksToShow = data.books.slice(0, 4);
      const totalBooks = data.books.length;

      let rowHtml = '<div class="row">';
      booksToShow.forEach((book, index) => {
        rowHtml += createBookCard(book);
        if ((index + 1) % 4 === 0 && index + 1 < booksToShow.length) {
          rowHtml += '</div><div class="row">';
        }
      });
      rowHtml += '</div>';
      productList.innerHTML = rowHtml;

      attachAddToCartHandlers(productList);

      // Add "View all" link if there are more than 4 books
      if (totalBooks > 4) {
        const viewAllRow = document.createElement('div');
        viewAllRow.className = 'row mt-3 text-center';
        viewAllRow.innerHTML = `<p class="text-muted">Showing 4 of ${totalBooks} books. <a href="#" class="view-all-featured text-decoration-none" style="color:#c8a96e;font-weight:600;">View all →</a></p>`;

        viewAllRow.querySelector('.view-all-featured').addEventListener('click', (e) => {
          e.preventDefault();
          let allRowHtml = '<div class="row">';
          data.books.forEach((book, index) => {
            allRowHtml += createBookCard(book);
            if ((index + 1) % 4 === 0 && index + 1 < data.books.length) {
              allRowHtml += '</div><div class="row">';
            }
          });
          allRowHtml += '</div>';
          productList.innerHTML = allRowHtml;
          attachAddToCartHandlers(productList);
        });

        productList.appendChild(viewAllRow);
      }
    } else {
      console.log('No featured books available');
    }
  } catch (error) {
    console.error('Error loading featured books:', error);
  }
}

/**
 * Load books by category (with optional sort) and render in the tab
 */
async function loadBooksByCategory(category) {
  try {
    const sort = currentSort[category] || 'default';
    const endpoint = buildEndpoint(category, sort);
    
    const response = await fetch(endpoint);
    const data = await response.json();
    
    const tabContent = document.querySelector(`#${category}[data-tab-content]`);
    if (!tabContent) return;

    // Always insert/refresh the sort bar first
    const sortBar = createSortBar(category, tabContent);

    if (data.status === 'success' && data.books && data.books.length > 0) {
      tabContent.innerHTML = '';
      tabContent.appendChild(sortBar);
      
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
      
      const booksContainer = document.createElement('div');
      booksContainer.innerHTML = rowHtml;
      tabContent.appendChild(booksContainer);

      // Add "View all" link if there are more books, with a working click handler
      if (totalBooks > 4) {
        const viewAllRow = document.createElement('div');
        viewAllRow.className = 'row mt-3 text-center';
        viewAllRow.innerHTML = `<p class="text-muted">Showing 4 of ${totalBooks} books. <a href="#" class="view-all-link text-decoration-none" style="color:#c8a96e;font-weight:600;">View all →</a></p>`;

        viewAllRow.querySelector('.view-all-link').addEventListener('click', (e) => {
          e.preventDefault();
          // Render ALL books
          let allRowHtml = '<div class="row">';
          data.books.forEach((book, index) => {
            allRowHtml += createBookCard(book);
            if ((index + 1) % 4 === 0 && index + 1 < data.books.length) {
              allRowHtml += '</div><div class="row">';
            }
          });
          allRowHtml += '</div>';
          booksContainer.innerHTML = allRowHtml;
          attachAddToCartHandlers(tabContent);
          viewAllRow.remove();
        });

        tabContent.appendChild(viewAllRow);
      }

      attachAddToCartHandlers(tabContent);
    } else {
      tabContent.innerHTML = '';
      tabContent.appendChild(sortBar);
      const empty = document.createElement('div');
      empty.className = 'row';
      empty.innerHTML = '<p class="text-center">No books available in this category.</p>';
      tabContent.appendChild(empty);
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
  // Delegate to CartDrawer, which is the single owner of add-to-cart click handling.
  // This prevents duplicate API calls that were caused by both book-loader.js and
  // cart-drawer.js independently attaching click listeners to the same buttons.
  if (window.CartDrawer && typeof window.CartDrawer.wire === 'function') {
    window.CartDrawer.wire();
  }
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