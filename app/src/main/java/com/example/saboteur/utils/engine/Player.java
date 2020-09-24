package com.example.saboteur.utils.engine;

import com.example.saboteur.utils.engine.cards.Card;
import com.example.saboteur.utils.engine.cards.Deck;

import java.util.ArrayList;

public class Player {
    private ArrayList<Card> hand;
    private int cardsNumber;

    public Player(int cardsNumber, Deck deck) {
        this.cardsNumber = cardsNumber;
        hand = new ArrayList<>();
        for (int i = 0; i < cardsNumber; i++) {
            hand.add(deck.draw());
        }
    }
}
