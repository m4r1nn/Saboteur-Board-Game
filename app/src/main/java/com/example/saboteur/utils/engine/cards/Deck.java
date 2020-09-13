package com.example.saboteur.utils.engine.cards;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;

public final class Deck {

    private static Deck instance = null;

    private ArrayList<Card> cards;

    private Deck() {
        cards = new ArrayList<>();

        cards.add(new Card(CardType.StartType.START));
        cards.add(new Card(CardType.EndType.TREASURE));
        cards.add(new Card(CardType.EndType.TURN_LEFT));
        cards.add(new Card(CardType.EndType.TURN_RIGHT));
        for (int i = 0; i < 3; i++) {
            cards.add(new Card(CardType.ActionType.SpecialType.AVALANCHE));
        }
        for (int i = 0; i < 6; i++) {
            cards.add(new Card(CardType.ActionType.SpecialType.MAP));
        }
        for (int i = 0; i < 3; i++) {
            cards.add(new Card(CardType.ActionType.BlockType.CART));
            cards.add(new Card(CardType.ActionType.BlockType.LAMP));
            cards.add(new Card(CardType.ActionType.BlockType.PICKAXE));
        }
        for (int i = 0; i < 2; i++) {
            cards.add(new Card(CardType.ActionType.UnblockType.CART));
            cards.add(new Card(CardType.ActionType.UnblockType.LAMP));
            cards.add(new Card(CardType.ActionType.UnblockType.PICKAXE));
        }
        cards.add(new Card(CardType.ActionType.UnblockType.CART_LAMP));
        cards.add(new Card(CardType.ActionType.UnblockType.LAMP_PICKAXE));
        cards.add(new Card(CardType.ActionType.UnblockType.PICKAXE_CART));
        for (int i = 0; i < 7; i++) {
            cards.add(new Card(CardType.RoadType.CORNER_LEFT));
            cards.add(new Card(CardType.RoadType.CORNER_RIGHT));
            cards.add(new Card(CardType.RoadType.CROSS));
            cards.add(new Card(CardType.RoadType.VERTICAL_1_HORIZONTAL_2));
            cards.add(new Card(CardType.RoadType.VERTICAL_2));
            cards.add(new Card(CardType.RoadType.VERTICAL_2_HORIZONTAL_1));
            cards.add(new Card(CardType.RoadType.HORIZONTAL_2));
        }
        cards.add(new Card(CardType.BlockType.CORNER_LEFT));
        cards.add(new Card(CardType.BlockType.CORNER_RIGHT));
        cards.add(new Card(CardType.BlockType.CROSS));
        cards.add(new Card(CardType.BlockType.VERTICAL_2));
        cards.add(new Card(CardType.BlockType.VERTICAL_2_HORIZONTAL_1));
        cards.add(new Card(CardType.BlockType.VERTICAL_1_HORIZONTAL_2));
        cards.add(new Card(CardType.BlockType.VERTICAL_1));
        cards.add(new Card(CardType.BlockType.HORIZONTAL_1));
        cards.add(new Card(CardType.BlockType.HORIZONTAL_2));
        Collections.shuffle(cards);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public static Deck getInstance() {
        if (instance == null) {
            instance = new Deck();
        }
        return instance;
    }

    public Card draw() {
        if (cards.size() == 0) {
            return null;
        }
        Card card = cards.get(cards.size() - 1);
        cards.remove(cards.size() - 1);
        return card;
    }

    public void burn(Card card) {
        if (cards.size() != 0) {
            cards.add(0, card);
        }
    }
}
