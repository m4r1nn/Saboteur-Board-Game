package com.example.saboteur.utils.engine.cards;

public interface CardType {
    enum StartType implements CardType {
        START
    }

    enum EndType implements CardType {
        TREASURE, TURN_LEFT, TURN_RIGHT
    }

    enum RoadType implements CardType {
        CORNER_LEFT, CORNER_RIGHT, CROSS, HORIZONTAL_2, VERTICAL_1_HORIZONTAL_2, VERTICAL_2, VERTICAL_2_HORIZONTAL_1
    }

    enum BlockType implements CardType {
        CORNER_LEFT, CORNER_RIGHT, CROSS, HORIZONTAL_2, VERTICAL_1_HORIZONTAL_2, VERTICAL_2, VERTICAL_2_HORIZONTAL_1, VERTICAL_1, HORIZONTAL_1
    }

    interface ActionType extends CardType {
        enum SpecialType implements ActionType {
            MAP, AVALANCHE
        }

        enum BlockType implements ActionType {
            PICKAXE, CART, LAMP
        }

        enum UnblockType implements ActionType {
            PICKAXE, CART, LAMP, PICKAXE_CART, CART_LAMP, LAMP_PICKAXE
        }
    }
}
