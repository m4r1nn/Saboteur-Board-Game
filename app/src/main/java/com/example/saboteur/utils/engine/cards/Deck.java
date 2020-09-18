package com.example.saboteur.utils.engine.cards;

import androidx.annotation.NonNull;

import com.example.saboteur.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


public final class Deck {

    private static Deck instance = null;

    private ArrayList<Card> cards;

    private BiMap<CardType, Integer> type2Id = null;

    private Deck() {
        cards = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            cards.add(new Card(CardType.ActionType.SpecialType.ACTION_AVALANCHE));
        }
        for (int i = 0; i < 6; i++) {
            cards.add(new Card(CardType.ActionType.SpecialType.ACTION_MAP));
        }
        for (int i = 0; i < 3; i++) {
            cards.add(new Card(CardType.ActionType.BlockType.ACTION_BLOCK_CART));
            cards.add(new Card(CardType.ActionType.BlockType.ACTION_BLOCK_LAMP));
            cards.add(new Card(CardType.ActionType.BlockType.ACTION_BLOCK_PICKAXE));
        }
        for (int i = 0; i < 2; i++) {
            cards.add(new Card(CardType.ActionType.UnblockType.ACTION_UNBLOCK_CART));
            cards.add(new Card(CardType.ActionType.UnblockType.ACTION_UNBLOCK_LAMP));
            cards.add(new Card(CardType.ActionType.UnblockType.ACTION_UNBLOCK_PICKAXE));
        }
        cards.add(new Card(CardType.ActionType.UnblockType.ACTION_UNBLOCK_CART_LAMP));
        cards.add(new Card(CardType.ActionType.UnblockType.ACTION_UNBLOCK_LAMP_PICKAXE));
        cards.add(new Card(CardType.ActionType.UnblockType.ACTION_UNBLOCK_PICKAXE_CART));
        for (int i = 0; i < 5; i++) {
            cards.add(new Card(CardType.RoadType.ROAD_CORNER_LEFT));
            cards.add(new Card(CardType.RoadType.ROAD_CORNER_RIGHT));
            cards.add(new Card(CardType.RoadType.ROAD_CROSS));
            cards.add(new Card(CardType.RoadType.ROAD_VERTICAL_1_HORIZONTAL_2));
            cards.add(new Card(CardType.RoadType.ROAD_VERTICAL_2));
            cards.add(new Card(CardType.RoadType.ROAD_VERTICAL_2_HORIZONTAL_1));
            cards.add(new Card(CardType.RoadType.ROAD_HORIZONTAL_2));
        }
        cards.add(new Card(CardType.BlockType.BLOCK_CORNER_LEFT));
        cards.add(new Card(CardType.BlockType.BLOCK_CORNER_RIGHT));
        cards.add(new Card(CardType.BlockType.BLOCK_CROSS));
        cards.add(new Card(CardType.BlockType.BLOCK_VERTICAL_2));
        cards.add(new Card(CardType.BlockType.BLOCK_VERTICAL_2_HORIZONTAL_1));
        cards.add(new Card(CardType.BlockType.BLOCK_VERTICAL_1_HORIZONTAL_2));
        cards.add(new Card(CardType.BlockType.BLOCK_VERTICAL_1));
        cards.add(new Card(CardType.BlockType.BLOCK_HORIZONTAL_1));
        cards.add(new Card(CardType.BlockType.BLOCK_HORIZONTAL_2));
        Collections.shuffle(cards);
    }

    private void buildMap() {
        type2Id = HashBiMap.create();
        type2Id.put(CardType.StartType.START, R.drawable.card_road_start);
        type2Id.put(CardType.EndType.FINISH_TREASURE, R.drawable.card_end_win);
        type2Id.put(CardType.EndType.FINISH_TURN_LEFT, R.drawable.card_end_turn_left);
        type2Id.put(CardType.EndType.FINISH_TURN_RIGHT, R.drawable.card_end_turn_right);
        type2Id.put(CardType.ActionType.SpecialType.ACTION_AVALANCHE, R.drawable.card_action_avalanche);
        type2Id.put(CardType.ActionType.SpecialType.ACTION_MAP, R.drawable.card_action_map);
        type2Id.put(CardType.ActionType.BlockType.ACTION_BLOCK_CART, R.drawable.card_action_block_cart);
        type2Id.put(CardType.ActionType.BlockType.ACTION_BLOCK_LAMP, R.drawable.card_action_block_lamp);
        type2Id.put(CardType.ActionType.BlockType.ACTION_BLOCK_PICKAXE, R.drawable.card_action_block_pickaxe);
        type2Id.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_CART, R.drawable.card_action_unblock_cart);
        type2Id.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_LAMP, R.drawable.card_action_unblock_lamp);
        type2Id.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_PICKAXE, R.drawable.card_action_unblock_pickaxe);
        type2Id.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_CART_LAMP, R.drawable.card_action_unblock_lamp_cart);
        type2Id.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_LAMP_PICKAXE, R.drawable.card_action_unblock_pickaxe_lamp);
        type2Id.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_PICKAXE_CART, R.drawable.card_action_unblock_pickaxe_cart);
        type2Id.put(CardType.RoadType.ROAD_CORNER_LEFT, R.drawable.card_road_corner_left);
        type2Id.put(CardType.RoadType.ROAD_CORNER_RIGHT, R.drawable.card_road_corner_right);
        type2Id.put(CardType.RoadType.ROAD_CROSS, R.drawable.card_road_cross);
        type2Id.put(CardType.RoadType.ROAD_VERTICAL_1_HORIZONTAL_2, R.drawable.card_road_vertical_1_horizontal_2);
        type2Id.put(CardType.RoadType.ROAD_VERTICAL_2, R.drawable.card_road_vertical_2);
        type2Id.put(CardType.RoadType.ROAD_VERTICAL_2_HORIZONTAL_1, R.drawable.card_road_vertical_2_horizontal_1);
        type2Id.put(CardType.RoadType.ROAD_HORIZONTAL_2, R.drawable.card_road_horizontal_2);
        type2Id.put(CardType.BlockType.BLOCK_CORNER_LEFT, R.drawable.card_block_corner_left);
        type2Id.put(CardType.BlockType.BLOCK_CORNER_RIGHT, R.drawable.card_block_corner_right);
        type2Id.put(CardType.BlockType.BLOCK_CROSS, R.drawable.card_block_cross);
        type2Id.put(CardType.BlockType.BLOCK_VERTICAL_2, R.drawable.card_block_vertical_2);
        type2Id.put(CardType.BlockType.BLOCK_VERTICAL_2_HORIZONTAL_1, R.drawable.card_block_vertical_2_horizontal_1);
        type2Id.put(CardType.BlockType.BLOCK_VERTICAL_1_HORIZONTAL_2, R.drawable.card_block_vertical_1_horizontal_2);
        type2Id.put(CardType.BlockType.BLOCK_VERTICAL_1, R.drawable.card_block_vertical_1);
        type2Id.put(CardType.BlockType.BLOCK_HORIZONTAL_1, R.drawable.card_block_horizontal_1);
        type2Id.put(CardType.BlockType.BLOCK_HORIZONTAL_2, R.drawable.card_block_horizontal_2);
        type2Id.put(CardType.Back.BACK_FINISH, R.drawable.card_back_end);
    }

    public BiMap<CardType, Integer> getType2Id() {
        return type2Id;
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
