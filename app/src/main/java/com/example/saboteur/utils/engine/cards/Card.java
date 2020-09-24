package com.example.saboteur.utils.engine.cards;

public class Card {
    private CardType type;
    private boolean rotated = false;

    public Card(CardType type) {
        this.type = type;
    }

    public CardType getCard() {
        return type;
    }

    public String getCardString() {
        return type.toString();
    }

    public void changeRotation() {
        if (!rotated) {
            rotated = true;
        } else {
            rotated = false;
        }
    }
}
