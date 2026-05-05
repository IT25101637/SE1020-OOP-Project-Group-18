package com.bookstore.onlinebookstore.repository;

import com.bookstore.onlinebookstore.model.PaymentCard;
import com.bookstore.onlinebookstore.util.FileHandler;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentCardRepository {
    private static final String FILE_PATH = "src/main/resources/data/paymentCards.txt";

    public void add(PaymentCard card) {
        List<String> lines = FileHandler.readFile(FILE_PATH);
        lines.add(card.toPipeDelimitedString());
        FileHandler.writeFile(FILE_PATH, lines);
    }

    public List<PaymentCard> findByUserId(String userId) {
        List<PaymentCard> cards = new ArrayList<>();
        List<String> lines = FileHandler.readFile(FILE_PATH);
        
        for (String line : lines) {
            PaymentCard card = PaymentCard.fromPipeDelimitedString(line.trim());
            if (card != null && card.getUserId().equals(userId)) {
                cards.add(card);
            }
        }
        return cards;
    }

    public PaymentCard findById(String paymentCardId) {
        List<String> lines = FileHandler.readFile(FILE_PATH);
        for (String line : lines) {
            PaymentCard card = PaymentCard.fromPipeDelimitedString(line.trim());
            if (card != null && card.getPaymentCardId().equals(paymentCardId)) {
                return card;
            }
        }
        return null;
    }

    public void update(PaymentCard card) {
        List<String> lines = FileHandler.readFile(FILE_PATH);
        List<String> updated = new ArrayList<>();
        
        for (String line : lines) {
            PaymentCard existing = PaymentCard.fromPipeDelimitedString(line.trim());
            if (existing != null && existing.getPaymentCardId().equals(card.getPaymentCardId())) {
                updated.add(card.toPipeDelimitedString());
            } else {
                updated.add(line);
            }
        }
        
        FileHandler.writeFile(FILE_PATH, updated);
    }

    public void delete(String paymentCardId) {
        List<String> lines = FileHandler.readFile(FILE_PATH);
        List<String> updated = new ArrayList<>();
        
        for (String line : lines) {
            PaymentCard card = PaymentCard.fromPipeDelimitedString(line.trim());
            if (card == null || !card.getPaymentCardId().equals(paymentCardId)) {
                updated.add(line);
            }
        }
        
        FileHandler.writeFile(FILE_PATH, updated);
    }

    public void deleteByUserId(String userId) {
        List<String> lines = FileHandler.readFile(FILE_PATH);
        List<String> updated = new ArrayList<>();
        
        for (String line : lines) {
            PaymentCard card = PaymentCard.fromPipeDelimitedString(line.trim());
            if (card == null || !card.getUserId().equals(userId)) {
                updated.add(line);
            }
        }
        
        FileHandler.writeFile(FILE_PATH, updated);
    }

    public List<PaymentCard> findAll() {
        List<PaymentCard> cards = new ArrayList<>();
        List<String> lines = FileHandler.readFile(FILE_PATH);
        
        for (String line : lines) {
            PaymentCard card = PaymentCard.fromPipeDelimitedString(line.trim());
            if (card != null) {
                cards.add(card);
            }
        }
        return cards;
    }
}
