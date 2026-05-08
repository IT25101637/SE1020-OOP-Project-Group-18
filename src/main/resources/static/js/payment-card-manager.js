// Payment Card Manager
const PaymentCardManager = (() => {
    const API_BASE = '/api/payment-cards';
    const urlParams = new URLSearchParams(window.location.search);
    const returnTo = urlParams.get('returnTo') || sessionStorage.getItem('checkoutReturnTo');

    // Get current user ID from session
    function getCurrentUserId() {
        const userId = sessionStorage.getItem('currentUserId');
        if (!userId) {
            window.location.href = 'login.html?redirect=payment-cards.html';
            return null;
        }
        return userId;
    }

    function navigateBackToCheckout() {
        const target = returnTo || sessionStorage.getItem('checkoutReturnTo') || 'checkout.html';
        sessionStorage.removeItem('checkoutReturnTo');
        window.location.href = target;
    }

    // Format card number with spaces
    function formatCardNumber(number) {
        return number.replace(/\s/g, '').replace(/(\d{4})/g, '$1 ').trim();
    }

    // Validate card number - 16 digits only
    function validateCardNumber(cardNumber) {
        const cleaned = cardNumber.replace(/\s/g, '');
        return cleaned.length === 16 && /^\d+$/.test(cleaned);
    }

    // Validate expiry date
    function validateExpiryDate(month, year) {
        const currentDate = new Date();
        const currentYear = currentDate.getFullYear();
        const currentMonth = currentDate.getMonth() + 1;

        const expYear = parseInt(year);
        const expMonth = parseInt(month);

        if (expYear < currentYear) {
            return false; // Year in past
        }

        if (expYear === currentYear && expMonth < currentMonth) {
            return false; // Month in past
        }

        return true;
    }

    // Add real-time card number validation
    function setupCardNumberValidation() {
        const cardInput = document.getElementById('cardNumber');
        if (!cardInput) return;

        cardInput.addEventListener('input', (e) => {
            let value = e.target.value.replace(/\s/g, '');
            value = value.replace(/\D/g, ''); // Only digits
            const formatted = formatCardNumber(value);
            e.target.value = formatted;

            const feedback = cardInput.parentElement.querySelector('.validation-feedback');
            if (feedback) {
                if (value.length === 16) {
                    feedback.textContent = '✓ Valid card number';
                    feedback.className = 'validation-feedback valid';
                } else if (value.length > 0) {
                    feedback.textContent = `✗ ${16 - value.length} digits remaining`;
                    feedback.className = 'validation-feedback invalid';
                } else {
                    feedback.className = 'validation-feedback';
                }
            }
        });
    }

    // Load and display saved cards
    async function loadCards() {
        const userId = getCurrentUserId();
        if (!userId) return;

        try {
            const response = await fetch(`${API_BASE}/${userId}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });
            const data = await response.json();

            if (data.success && data.cards && data.cards.length > 0) {
                displayCards(data.cards);
            } else {
                displayEmptyState();
            }
        } catch (error) {
            console.error('Error loading cards:', error);
            displayEmptyState();
        }
    }

    // Display cards
    function displayCards(cards) {
        const cardsList = document.getElementById('cardsList');
        cardsList.innerHTML = '';

        cards.forEach(card => {
            const cardElement = document.createElement('div');
            cardElement.className = 'card-item';
            cardElement.innerHTML = `
                <div class="card-display">
                    <div class="card-number">
                        <i class="fas fa-credit-card"></i> ••••••••••••${card.cardLast4}
                    </div>
                    <div class="card-details">
                        <span><strong>Name:</strong> ${card.cardholderName}</span>
                        <span><strong>Expires:</strong> ${card.expiryMonth}/${card.expiryYear}</span>
                        ${card.default ? '<span class="default-badge"><i class="fas fa-check"></i> Default</span>' : ''}
                    </div>
                </div>
                <div class="card-actions">
                    ${!card.default ? `<button class="btn btn-sm btn-secondary" onclick="PaymentCardManager.setDefault('${card.paymentCardId}')">Set Default</button>` : ''}
                    <button class="btn btn-sm btn-secondary" onclick="PaymentCardManager.editCard('${card.paymentCardId}', '${card.cardholderName}', '${card.expiryMonth}', '${card.expiryYear}')">Edit</button>
                    <button class="btn btn-sm btn-danger" onclick="PaymentCardManager.deleteCard('${card.paymentCardId}')">Delete</button>
                </div>
            `;
            cardsList.appendChild(cardElement);
        });
    }

    // Display empty state
    function displayEmptyState() {
        const cardsList = document.getElementById('cardsList');
        cardsList.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-wallet"></i>
                <p>No saved cards yet. Add your first card above!</p>
            </div>
        `;
    }

    // Add card
    async function addCard() {
        const userId = getCurrentUserId();
        if (!userId) return;

        const cardNumber = document.getElementById('cardNumber').value.trim();
        const cardholderName = document.getElementById('cardholderName').value.trim();
        const expiryMonth = document.getElementById('expiryMonth').value;
        const expiryYear = document.getElementById('expiryYear').value;

        // Clear previous errors
        document.querySelectorAll('#addCardForm .error-message').forEach(el => {
            el.textContent = '';
            el.style.display = 'none';
        });

        // Validate
        const errors = [];
        if (!validateCardNumber(cardNumber)) {
            errors.push('Card number must be 16 digits');
        }
        if (!cardholderName) {
            errors.push('Cardholder name is required');
        }
        if (!expiryMonth) {
            errors.push('Expiry month is required');
        }
        if (!expiryYear) {
            errors.push('Expiry year is required');
        }
        if (expiryMonth && expiryYear && !validateExpiryDate(expiryMonth, expiryYear)) {
            errors.push('Card has expired or expiry date is invalid');
        }

        if (errors.length > 0) {
            showErrors(errors);
            return;
        }

        try {
            const response = await fetch(`${API_BASE}/${userId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    cardNumber,
                    cardholderName,
                    expiryMonth,
                    expiryYear
                })
            });
            const data = await response.json();

            if (data.success) {
                document.getElementById('addCardForm').reset();
                loadCards();
                showSuccess('Card added successfully!');
                if (returnTo) {
                    setTimeout(navigateBackToCheckout, 700);
                }
            } else {
                showErrors([data.message || 'Failed to add card']);
            }
        } catch (error) {
            console.error('Error adding card:', error);
            showErrors(['Error adding card. Please try again.']);
        }
    }

    // Edit card
    window.PaymentCardManager = window.PaymentCardManager || {};
    window.PaymentCardManager.editCard = async function(cardId, cardholderName, expiryMonth, expiryYear) {
        const userId = getCurrentUserId();
        if (!userId) return;

        const newName = prompt('Enter cardholder name:', cardholderName);
        if (newName === null) return;

        try {
            const response = await fetch(`${API_BASE}/${userId}/${cardId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    cardholderName: newName,
                    expiryMonth,
                    expiryYear
                })
            });
            const data = await response.json();

            if (data.success) {
                loadCards();
                showSuccess('Card updated successfully!');
            } else {
                showErrors([data.message || 'Failed to update card']);
            }
        } catch (error) {
            console.error('Error updating card:', error);
            showErrors(['Error updating card. Please try again.']);
        }
    };

    // Delete card
    window.PaymentCardManager.deleteCard = async function(cardId) {
        const userId = getCurrentUserId();
        if (!userId) return;

        if (!confirm('Are you sure you want to delete this card?')) return;

        try {
            const response = await fetch(`${API_BASE}/${userId}/${cardId}`, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' }
            });
            const data = await response.json();

            if (data.success) {
                loadCards();
                showSuccess('Card deleted successfully!');
            } else {
                showErrors([data.message || 'Failed to delete card']);
            }
        } catch (error) {
            console.error('Error deleting card:', error);
            showErrors(['Error deleting card. Please try again.']);
        }
    };

    // Set default card
    window.PaymentCardManager.setDefault = async function(cardId) {
        const userId = getCurrentUserId();
        if (!userId) return;

        try {
            const response = await fetch(`${API_BASE}/${userId}/${cardId}/set-default`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });
            const data = await response.json();

            if (data.success) {
                loadCards();
                showSuccess('Default card updated!');
            } else {
                showErrors([data.message || 'Failed to set default card']);
            }
        } catch (error) {
            console.error('Error setting default card:', error);
            showErrors(['Error setting default card. Please try again.']);
        }
    };

    // Show errors
    function showErrors(errors) {
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-danger alert-dismissible fade show';
        alertDiv.innerHTML = `
            ${errors.map(e => `<div>• ${e}</div>`).join('')}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        const form = document.getElementById('addCardForm');
        form.parentElement.insertBefore(alertDiv, form);
        setTimeout(() => alertDiv.remove(), 5000);
    }

    // Show success message
    function showSuccess(message) {
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-success alert-dismissible fade show';
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        const form = document.getElementById('addCardForm');
        form.parentElement.insertBefore(alertDiv, form);
        setTimeout(() => alertDiv.remove(), 3000);
    }

    // Setup expiry year selector
    function setupExpiryYears() {
        const yearSelect = document.getElementById('expiryYear');
        const currentYear = new Date().getFullYear();
        for (let i = 0; i < 20; i++) {
            const year = currentYear + i;
            const option = document.createElement('option');
            option.value = year.toString();
            option.textContent = year.toString();
            yearSelect.appendChild(option);
        }
    }

    // Initialize
    function init() {
        const userId = getCurrentUserId();
        if (!userId) return;

        if (returnTo) {
            const header = document.querySelector('.page-header p');
            if (header) {
                header.textContent = 'Manage your saved payment methods and return to checkout';
            }

            const container = document.getElementById('paymentCardsContent');
            if (container) {
                const backButton = document.createElement('div');
                backButton.className = 'mb-3';
                backButton.innerHTML = `
                    <button type="button" class="btn btn-secondary" id="backToCheckoutBtn" style="width: auto;">
                        <i class="fas fa-arrow-left"></i> Back to Checkout
                    </button>
                `;
                container.insertBefore(backButton, container.firstChild);
            }
        }

        setupExpiryYears();
        setupCardNumberValidation();
        loadCards();

        const backToCheckoutBtn = document.getElementById('backToCheckoutBtn');
        if (backToCheckoutBtn) {
            backToCheckoutBtn.addEventListener('click', navigateBackToCheckout);
        }

        const form = document.getElementById('addCardForm');
        if (form) {
            form.addEventListener('submit', (e) => {
                e.preventDefault();
                addCard();
            });
        }
    }

    // Return public methods
    return {
        init,
        loadCards,
        deleteCard: window.PaymentCardManager?.deleteCard,
        setDefault: window.PaymentCardManager?.setDefault,
        editCard: window.PaymentCardManager?.editCard
    };
})();

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', PaymentCardManager.init);
