package com.example.saboteur.utils.engine.cards;

import com.example.saboteur.R;

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


public final class Deck {

    private static Deck instance = null;

    private ArrayList<Card> cards;

    private BiMap<CardType, Integer> type2Id = null;
    private BiMap<CardType, String> type2String;

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

    public ArrayList<String> getEndCards() {
        ArrayList<String> endCards = new ArrayList<>();
        endCards.add(CardType.EndType.FINISH_TREASURE.getName());
        endCards.add(CardType.EndType.FINISH_TURN_LEFT.getName());
        endCards.add(CardType.EndType.FINISH_TURN_RIGHT.getName());
        Collections.shuffle(endCards);
        return endCards;
    }

    private void buildType2StringMap() {
        type2String = HashBiMap.create();
        type2String.put(CardType.StartType.START, CardType.StartType.START.getName());
        type2String.put(CardType.EndType.FINISH_TREASURE, CardType.EndType.FINISH_TREASURE.getName());
        type2String.put(CardType.EndType.FINISH_TURN_LEFT, CardType.EndType.FINISH_TURN_LEFT.getName());
        type2String.put(CardType.EndType.FINISH_TURN_RIGHT, CardType.EndType.FINISH_TURN_RIGHT.getName());
        type2String.put(CardType.ActionType.SpecialType.ACTION_AVALANCHE, CardType.ActionType.SpecialType.ACTION_AVALANCHE.getName());
        type2String.put(CardType.ActionType.SpecialType.ACTION_MAP, CardType.ActionType.SpecialType.ACTION_MAP.getName());
        type2String.put(CardType.ActionType.BlockType.ACTION_BLOCK_CART, CardType.ActionType.BlockType.ACTION_BLOCK_CART.getName());
        type2String.put(CardType.ActionType.BlockType.ACTION_BLOCK_LAMP, CardType.ActionType.BlockType.ACTION_BLOCK_LAMP.getName());
        type2String.put(CardType.ActionType.BlockType.ACTION_BLOCK_PICKAXE, CardType.ActionType.BlockType.ACTION_BLOCK_PICKAXE.getName());
        type2String.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_CART, CardType.ActionType.UnblockType.ACTION_UNBLOCK_CART.getName());
        type2String.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_LAMP, CardType.ActionType.UnblockType.ACTION_UNBLOCK_LAMP.getName());
        type2String.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_PICKAXE, CardType.ActionType.UnblockType.ACTION_UNBLOCK_PICKAXE.getName());
        type2String.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_CART_LAMP, CardType.ActionType.UnblockType.ACTION_UNBLOCK_CART_LAMP.getName());
        type2String.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_LAMP_PICKAXE, CardType.ActionType.UnblockType.ACTION_UNBLOCK_LAMP_PICKAXE.getName());
        type2String.put(CardType.ActionType.UnblockType.ACTION_UNBLOCK_PICKAXE_CART, CardType.ActionType.UnblockType.ACTION_UNBLOCK_PICKAXE_CART.getName());
        type2String.put(CardType.RoadType.ROAD_CORNER_LEFT, CardType.RoadType.ROAD_CORNER_LEFT.getName());
        type2String.put(CardType.RoadType.ROAD_CORNER_RIGHT, CardType.RoadType.ROAD_CORNER_RIGHT.getName());
        type2String.put(CardType.RoadType.ROAD_CROSS, CardType.RoadType.ROAD_CROSS.getName());
        type2String.put(CardType.RoadType.ROAD_VERTICAL_1_HORIZONTAL_2, CardType.RoadType.ROAD_VERTICAL_1_HORIZONTAL_2.getName());
        type2String.put(CardType.RoadType.ROAD_VERTICAL_2, CardType.RoadType.ROAD_VERTICAL_2.getName());
        type2String.put(CardType.RoadType.ROAD_VERTICAL_2_HORIZONTAL_1, CardType.RoadType.ROAD_VERTICAL_2_HORIZONTAL_1.getName());
        type2String.put(CardType.RoadType.ROAD_HORIZONTAL_2, CardType.RoadType.ROAD_HORIZONTAL_2.getName());
        type2String.put(CardType.BlockType.BLOCK_CORNER_LEFT, CardType.BlockType.BLOCK_CORNER_LEFT.getName());
        type2String.put(CardType.BlockType.BLOCK_CORNER_RIGHT, CardType.BlockType.BLOCK_CORNER_RIGHT.getName());
        type2String.put(CardType.BlockType.BLOCK_CROSS, CardType.BlockType.BLOCK_CROSS.getName());
        type2String.put(CardType.BlockType.BLOCK_VERTICAL_2, CardType.BlockType.BLOCK_VERTICAL_2.getName());
        type2String.put(CardType.BlockType.BLOCK_VERTICAL_2_HORIZONTAL_1, CardType.BlockType.BLOCK_VERTICAL_2_HORIZONTAL_1.getName());
        type2String.put(CardType.BlockType.BLOCK_VERTICAL_1_HORIZONTAL_2, CardType.BlockType.BLOCK_VERTICAL_1_HORIZONTAL_2.getName());
        type2String.put(CardType.BlockType.BLOCK_VERTICAL_1, CardType.BlockType.BLOCK_VERTICAL_1.getName());
        type2String.put(CardType.BlockType.BLOCK_HORIZONTAL_1, CardType.BlockType.BLOCK_HORIZONTAL_1.getName());
        type2String.put(CardType.BlockType.BLOCK_HORIZONTAL_2, CardType.BlockType.BLOCK_HORIZONTAL_2.getName());
        type2String.put(CardType.Back.BACK_FINISH, CardType.Back.BACK_FINISH.getName());
    }

    private void buildType2IdMap() {
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
        if (type2Id == null) {
            buildType2IdMap();
        }
        return type2Id;
    }

    public BiMap<CardType, String> getType2String() {
        if (type2String == null) {
            buildType2StringMap();
        }
        return type2String;
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
