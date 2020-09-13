package com.example.saboteur.utils.engine.cards;
public interface CardType {

    String getName();

    enum StartType implements CardType {
        START;

        @Override
        public String getName() {
            return this.name();
        }
    }

    enum EndType implements CardType {
        TREASURE, TURN_LEFT, TURN_RIGHT;

        @Override
        public String getName() {
            return this.name();
        }
    }

    enum RoadType implements CardType {
        CORNER_LEFT, CORNER_RIGHT, CROSS, HORIZONTAL_2, VERTICAL_1_HORIZONTAL_2, VERTICAL_2, VERTICAL_2_HORIZONTAL_1;

        @Override
        public String getName() {
            return this.name();
        }
    }

    enum BlockType implements CardType {
        CORNER_LEFT, CORNER_RIGHT, CROSS, HORIZONTAL_2, VERTICAL_1_HORIZONTAL_2, VERTICAL_2, VERTICAL_2_HORIZONTAL_1, VERTICAL_1, HORIZONTAL_1;

        @Override
        public String getName() {
            return this.name();
        }
    }

    interface ActionType extends CardType {
        enum SpecialType implements ActionType {
            MAP, AVALANCHE;

            @Override
            public String getName() {
                return this.name();
            }
        }

        enum BlockType implements ActionType {
            PICKAXE, CART, LAMP;

            @Override
            public String getName() {
                return this.name();
            }
        }

        enum UnblockType implements ActionType {
            PICKAXE, CART, LAMP, PICKAXE_CART, CART_LAMP, LAMP_PICKAXE;

            @Override
            public String getName() {
                return this.name();
            }
        }
    }
}
