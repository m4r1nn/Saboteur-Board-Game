package com.example.saboteur.utils.engine.cards;

import java.util.List;

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
        rotated = !rotated;
    }

    public boolean getRotated() {
        return rotated;
    }
}
