package com.example.saboteur.utils.engine;

import com.example.saboteur.utils.engine.cards.Card;
import com.example.saboteur.utils.engine.cards.Deck;

import java.util.ArrayList;

public class GameEngine {
    private Deck deck;
    private ArrayList<Player> players;

    public GameEngine() {
        deck = Deck.getInstance();
        players = new ArrayList<>();
    }
}
