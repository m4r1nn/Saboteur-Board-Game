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
        FINISH_TREASURE, FINISH_TURN_LEFT, FINISH_TURN_RIGHT;

        @Override
        public String getName() {
            return this.name();
        }
    }

    enum RoadType implements CardType {
        ROAD_CORNER_LEFT, ROAD_CORNER_RIGHT, ROAD_CROSS, ROAD_HORIZONTAL_2,
        ROAD_VERTICAL_1_HORIZONTAL_2, ROAD_VERTICAL_2, ROAD_VERTICAL_2_HORIZONTAL_1;

        @Override
        public String getName() {
            return this.name();
        }
    }

    enum BlockType implements CardType {
        BLOCK_CORNER_LEFT, BLOCK_CORNER_RIGHT, BLOCK_CROSS, BLOCK_HORIZONTAL_2,
        BLOCK_VERTICAL_1_HORIZONTAL_2, BLOCK_VERTICAL_2, BLOCK_VERTICAL_2_HORIZONTAL_1, BLOCK_VERTICAL_1, BLOCK_HORIZONTAL_1;

        @Override
        public String getName() {
            return this.name();
        }
    }

    enum Back implements CardType {
        BACK_NORMAL, BACK_FINISH, BACK_GOLD, BACK_ROLE;

        @Override
        public String getName() {
            return this.name();
        }
    }

    interface ActionType extends CardType {
        enum SpecialType implements ActionType {
            ACTION_MAP, ACTION_AVALANCHE;

            @Override
            public String getName() {
                return this.name();
            }
        }

        enum BlockType implements ActionType {
            ACTION_BLOCK_PICKAXE, ACTION_BLOCK_CART, ACTION_BLOCK_LAMP;

            @Override
            public String getName() {
                return this.name();
            }
        }

        enum UnblockType implements ActionType {
            ACTION_UNBLOCK_PICKAXE, ACTION_UNBLOCK_CART, ACTION_UNBLOCK_LAMP,
            ACTION_UNBLOCK_PICKAXE_CART, ACTION_UNBLOCK_CART_LAMP, ACTION_UNBLOCK_LAMP_PICKAXE;

            @Override
            public String getName() {
                return this.name();
            }
        }
    }
}
