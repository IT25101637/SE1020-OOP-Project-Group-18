// Checkout Handler
const Checkout = (() => {
    let cartItems = [];
    let selectedCardId = null;
    let currentUserId = null;
    let userEmail = null;
    const billingDraftKey = 'checkoutBillingDraft';
    const checkoutReturnKey = 'checkoutReturnTo';

    function getCurrentUserId() {
        currentUserId = sessionStorage.getItem('currentUserId');
        userEmail = sessionStorage.getItem('currentUserEmail');
        
        if (!currentUserId) {
            window.location.href = 'login.html?redirect=checkout.html';
            return false;
        }
        return true;
    }

    function getBillingFields() {
        return {
            address: document.getElementById('address')?.value || '',
            city: document.getElementById('city')?.value || '',
            zipCode: document.getElementById('zipCode')?.value || '',
            state: document.getElementById('state')?.value || '',
            country: document.getElementById('country')?.value || ''
        };
    }

    function saveBillingDraft() {
        sessionStorage.setItem(billingDraftKey, JSON.stringify(getBillingFields()));
    }

    function restoreBillingDraft() {
        const draft = sessionStorage.getItem(billingDraftKey);
        if (!draft) {
            return;
        }

        try {
            const parsed = JSON.parse(draft);
            const addressField = document.getElementById('address');
            const cityField = document.getElementById('city');
            const zipField = document.getElementById('zipCode');
            const stateField = document.getElementById('state');
            const countryField = document.getElementById('country');

            if (addressField) addressField.value = parsed.address || '';
            if (cityField) cityField.value = parsed.city || '';
            if (zipField) zipField.value = parsed.zipCode || '';
            if (stateField) stateField.value = parsed.state || '';
            if (countryField) countryField.value = parsed.country || '';
        } catch (error) {
            console.error('Failed to restore billing draft:', error);
        }
    }

    function wireBillingDraftPersistence() {
        ['address', 'city', 'zipCode', 'state', 'country'].forEach((fieldId) => {
            const field = document.getElementById(fieldId);
            if (!field) {
                return;
            }
            field.addEventListener('input', saveBillingDraft);
            field.addEventListener('change', saveBillingDraft);
        });
    }

    function openPaymentCardManager() {
        saveBillingDraft();
        sessionStorage.setItem(checkoutReturnKey, 'checkout.html');
        window.location.href = 'payment-cards.html?returnTo=checkout.html';
    }

    async function loadCartItems() {
        try {
            const response = await fetch(`/api/cart/${encodeURIComponent(currentUserId)}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });
            const data = await response.json();

            if (response.ok && data.status === 'success' && Array.isArray(data.items) && data.items.length > 0) {
                cartItems = data.items;
                displayOrderItems();
                updateOrderSummary();
                return true;
            } else {
                showError('Your cart is empty');
                setTimeout(() => window.location.href = 'cart.html', 2000);
                return false;
            }
        } catch (error) {
            console.error('Error loading cart:', error);
            showError('Error loading cart items');
            return false;
        }
    }

    function displayOrderItems() {
        const itemsList = document.getElementById('orderItemsList');
        itemsList.innerHTML = '';

        cartItems.forEach(item => {
            const itemDiv = document.createElement('div');
            itemDiv.className = 'checkout-item';
            itemDiv.innerHTML = `
                <img src="${item.imageUrl}" alt="${item.title}" class="checkout-item-image">
                <div class="checkout-item-details">
                    <p><strong>${item.title}</strong></p>
                    <p>by ${item.author}</p>
                    <p style="color: #C5A992; font-weight: bold;">$${item.price.toFixed(2)} x ${item.quantity}</p>
                </div>
                <div style="text-align: right; font-weight: bold;">
                    $${(item.price * item.quantity).toFixed(2)}
                </div>
            `;
            itemsList.appendChild(itemDiv);
        });
    }

    function calculateTotals() {
        let subtotal = 0;
        cartItems.forEach(item => {
            subtotal += item.price * item.quantity;
        });

        const tax = subtotal * 0.08; // 8% tax
        const shipping = 5.00;
        const total = subtotal + tax + shipping;

        return { subtotal, tax, shipping, total };
    }

    function updateOrderSummary() {
        const { subtotal, tax, shipping, total } = calculateTotals();
        const summary = document.getElementById('orderSummary');

        summary.innerHTML = `
            <div class="summary-item">
                <span>Subtotal:</span>
                <span>$${subtotal.toFixed(2)}</span>
            </div>
            <div class="summary-item">
                <span>Tax (8%):</span>
                <span>$${tax.toFixed(2)}</span>
            </div>
            <div class="summary-item">
                <span>Shipping:</span>
                <span>$${shipping.toFixed(2)}</span>
            </div>
            <div class="summary-total">
                <span>Total:</span>
                <span>$${total.toFixed(2)}</span>
            </div>
        `;
    }

    async function loadPaymentMethods() {
        try {
            const response = await fetch(`/api/payment-cards/${encodeURIComponent(currentUserId)}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });
            const data = await response.json();

            const methodsList = document.getElementById('paymentMethodsList');
            methodsList.innerHTML = '';

            if (data.success && data.cards && data.cards.length > 0) {
                data.cards.forEach((card, index) => {
                    const isSelected = card.isDefault || index === 0;
                    if (isSelected) {
                        selectedCardId = card.paymentCardId;
                    }

                    const cardDiv = document.createElement('div');
                    cardDiv.className = `card-selector ${isSelected ? 'selected' : ''}`;
                    cardDiv.innerHTML = `
                        <input type="radio" name="paymentCard" value="${card.paymentCardId}" ${isSelected ? 'checked' : ''}>
                        <span class="card-info">
                            <span class="card-number-display">••••${card.cardLast4}</span>
                            <span>${card.cardholderName} (${card.expiryMonth}/${card.expiryYear})</span>
                            ${card.isDefault ? '<span style="color: #2fa0e3; font-weight: bold;">Default</span>' : ''}
                        </span>
                    `;
                    cardDiv.addEventListener('click', () => {
                        document.querySelectorAll('.card-selector').forEach(el => el.classList.remove('selected'));
                        cardDiv.classList.add('selected');
                        document.querySelector(`input[value="${card.paymentCardId}"]`).checked = true;
                        selectedCardId = card.paymentCardId;
                        document.getElementById('paymentError').style.display = 'none';
                    });
                    methodsList.appendChild(cardDiv);
                });
            } else {
                methodsList.innerHTML = `
                    <div style="text-align: center; color: #999; padding: 20px;">
                        <p>No saved payment cards</p>
                        <a href="payment-cards.html" class="btn btn-accent" style="display: inline-block; width: auto;">
                            Add Payment Card
                        </a>
                    </div>
                `;
                selectedCardId = null;
            }
        } catch (error) {
            console.error('Error loading payment methods:', error);
            showError('Error loading payment methods');
        }
    }

    async function completePayment() {
        // Validate
        const address = document.getElementById('address').value.trim();
        const city = document.getElementById('city').value.trim();
        const zipCode = document.getElementById('zipCode').value.trim();
        const state = document.getElementById('state').value.trim();
        const country = document.getElementById('country').value.trim();

        const errors = [];
        if (!address) errors.push('Address is required');
        if (!city) errors.push('City is required');
        if (!zipCode) errors.push('Zip Code is required');
        if (!state) errors.push('State is required');
        if (!country) errors.push('Country is required');
        if (!selectedCardId) errors.push('Please select a payment card');

        if (errors.length > 0) {
            showError(errors.join('<br>'));
            return;
        }

        const btn = document.getElementById('completePaymentBtn');
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';

        try {
            const { total } = calculateTotals();
            const cartItemsJson = JSON.stringify(cartItems.map(item => ({
                bookId: item.bookId,
                title: item.title,
                author: item.author,
                imageUrl: item.imageUrl,
                quantity: item.quantity,
                price: item.price,
                lineTotal: item.price * item.quantity
            })));

            const paymentResponse = await fetch('/api/payments/process', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    userId: currentUserId,
                    userEmail: userEmail,
                    paymentCardId: selectedCardId,
                    totalAmount: total,
                    cartItemsJson: cartItemsJson
                })
            });

            const paymentData = await paymentResponse.json();

            if (paymentData.success && paymentData.payment) {
                // Clear cart after successful payment
                await clearCart();

                // Show receipt
                showReceipt(paymentData.payment);
            } else {
                showError(paymentData.message || 'Payment processing failed');
                btn.disabled = false;
                btn.innerHTML = '<i class="fas fa-lock"></i> Complete Payment';
            }
        } catch (error) {
            console.error('Error processing payment:', error);
            showError('Error processing payment. Please try again.');
            btn.disabled = false;
            btn.innerHTML = '<i class="fas fa-lock"></i> Complete Payment';
        }
    }

    async function clearCart() {
        try {
            await fetch(`/api/cart/${encodeURIComponent(currentUserId)}/clear`, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' }
            });
        } catch (error) {
            console.error('Error clearing cart:', error);
        }
    }

    function showReceipt(payment) {
        // Create receipt HTML
        const receiptHtml = generateReceiptHTML(payment);

        // Show receipt in modal or redirect to receipt page
        // For now, let's store the payment data and redirect
        sessionStorage.setItem('lastPayment', JSON.stringify(payment));
        
        // Show success message and redirect
        const container = document.querySelector('.checkout-container');
        container.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 40px;">
                <div style="background: #e8f8e8; padding: 40px; border-radius: 8px; margin-bottom: 20px;">
                    <i class="fas fa-check-circle" style="font-size: 60px; color: #27ae60; margin-bottom: 20px; display: block;"></i>
                    <h2 style="color: #27ae60; margin-bottom: 10px;">Payment Successful!</h2>
                    <p style="font-size: 18px; margin-bottom: 10px;">Order ID: <strong>${payment.orderId}</strong></p>
                    <p style="color: #666;">A confirmation email has been sent to ${payment.userEmail}</p>
                </div>
                <button onclick="Checkout.downloadReceipt('${payment.paymentId}')" class="btn btn-accent" style="width: auto; display: inline-block; margin-right: 10px;">
                    <i class="fas fa-download"></i> Download Receipt
                </button>
                <a href="index.html" class="btn" style="background-color: #ccc; color: #333; width: auto; display: inline-block; text-decoration: none;">
                    <i class="fas fa-home"></i> Continue Shopping
                </a>
            </div>
        `;

        sessionStorage.removeItem(billingDraftKey);
    }

    function generateReceiptHTML(payment) {
        const { total } = calculateTotals();
        const date = new Date(payment.paymentDate).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

        let itemsHtml = '';
        const items = JSON.parse(payment.cartItems);
        items.forEach(item => {
            itemsHtml += `
                <tr>
                    <td>${item.title}</td>
                    <td style="text-align: center;">${item.quantity}</td>
                    <td style="text-align: right;">$${item.price.toFixed(2)}</td>
                    <td style="text-align: right;">$${item.lineTotal.toFixed(2)}</td>
                </tr>
            `;
        });

        const subtotal = items.reduce((sum, item) => sum + item.lineTotal, 0);
        const tax = subtotal * 0.08;
        const shipping = 5.00;

        return `
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; color: #333; }
                    .receipt { max-width: 600px; margin: 0 auto; padding: 30px; background: white; }
                    .header { text-align: center; border-bottom: 2px solid #C5A992; padding-bottom: 20px; margin-bottom: 20px; }
                    .header h1 { color: #C5A992; margin: 0; font-size: 28px; }
                    .order-id { font-size: 12px; color: #666; margin-top: 5px; }
                    .section { margin-bottom: 25px; }
                    .section-title { color: #C5A992; font-weight: bold; border-bottom: 1px solid #ddd; padding-bottom: 8px; margin-bottom: 12px; }
                    table { width: 100%; border-collapse: collapse; }
                    table td { padding: 8px; border-bottom: 1px solid #ddd; }
                    table th { text-align: left; padding: 8px; background: #f8f6f3; font-weight: bold; }
                    .totals { text-align: right; }
                    .total-row { font-size: 18px; font-weight: bold; color: #C5A992; border-top: 2px solid #C5A992; padding-top: 10px; }
                    .info-row { font-size: 13px; margin: 8px 0; }
                </style>
            </head>
            <body>
                <div class="receipt">
                    <div class="header">
                        <h1>BookSaw</h1>
                        <p class="order-id">Order Receipt</p>
                    </div>

                    <div class="section">
                        <div class="section-title">Order Information</div>
                        <div class="info-row"><strong>Order ID:</strong> ${payment.orderId}</div>
                        <div class="info-row"><strong>Date:</strong> ${date}</div>
                        <div class="info-row"><strong>Email:</strong> ${payment.userEmail}</div>
                        <div class="info-row"><strong>Payment Method:</strong> Card ending in ${payment.cardLast4}</div>
                    </div>

                    <div class="section">
                        <div class="section-title">Order Items</div>
                        <table>
                            <thead>
                                <tr>
                                    <th>Item</th>
                                    <th style="text-align: center;">Qty</th>
                                    <th style="text-align: right;">Price</th>
                                    <th style="text-align: right;">Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${itemsHtml}
                            </tbody>
                        </table>
                    </div>

                    <div class="section">
                        <div class="totals">
                            <div class="info-row">Subtotal: $${subtotal.toFixed(2)}</div>
                            <div class="info-row">Tax (8%): $${tax.toFixed(2)}</div>
                            <div class="info-row">Shipping: $${shipping.toFixed(2)}</div>
                            <div class="total-row">Total: $${total.toFixed(2)}</div>
                        </div>
                    </div>

                    <div style="text-align: center; margin-top: 40px; color: #999; font-size: 12px;">
                        <p>Thank you for your purchase!</p>
                        <p>This is an automated receipt. Please keep it for your records.</p>
                    </div>
                </div>
            </body>
            </html>
        `;
    }

    window.Checkout = window.Checkout || {};
    window.Checkout.downloadReceipt = function(paymentId) {
        const payment = JSON.parse(sessionStorage.getItem('lastPayment'));
        if (!payment) {
            alert('Receipt not found');
            return;
        }

        const receiptHtml = generateReceiptHTML(payment);
        downloadPDF(receiptHtml, `receipt-${payment.orderId}`);
    };

    function downloadPDF(htmlContent, filename) {
        // Use html2pdf library for PDF generation
        const script = document.createElement('script');
        script.src = 'https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js';
        script.onload = function() {
            const element = document.createElement('div');
            element.innerHTML = htmlContent;
            const opt = {
                margin: 10,
                filename: filename + '.pdf',
                image: { type: 'jpeg', quality: 0.98 },
                html2canvas: { scale: 2 },
                jsPDF: { orientation: 'portrait', unit: 'mm', format: 'a4' }
            };
            html2pdf().set(opt).from(element).save();
        };
        document.head.appendChild(script);
    }

    function showError(message) {
        const errorDiv = document.getElementById('checkoutError');
        errorDiv.innerHTML = message;
        errorDiv.style.display = 'block';
        window.scrollTo(0, 0);
    }

    function init() {
        if (!getCurrentUserId()) return;

        restoreBillingDraft();
        wireBillingDraftPersistence();
        loadCartItems();
        loadPaymentMethods();

        const addCardBtn = document.getElementById('addPaymentCardBtn');
        if (addCardBtn) {
            addCardBtn.addEventListener('click', openPaymentCardManager);
        }

        const completeBtn = document.getElementById('completePaymentBtn');
        if (completeBtn) {
            completeBtn.addEventListener('click', completePayment);
        }
    }

    return {
        init,
        downloadReceipt: window.Checkout?.downloadReceipt,
        openPaymentCardManager
    };
})();

document.addEventListener('DOMContentLoaded', Checkout.init);
