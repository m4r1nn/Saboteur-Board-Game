package com.example.saboteur.utils.engine.cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface CardType {

    public default List<Directions> getCardDirections(boolean rotated) {
        if (rotated) {
            return getReverseRoadDirections();
        } else {
            return getRoadDirections();
        }
    }

    public default List<Directions> rotateDirections(List<Directions> directions) {
        List<Directions> res = new ArrayList<>();
        for (Directions direction : directions) {
            switch (direction) {
                case NORTH:
                    res.add(Directions.SOUTH);
                case SOUTH:
                    res.add(Directions.NORTH);
                case EAST:
                    res.add(Directions.WEST);
                case WEST:
                    res.add(Directions.EAST);
            }
        }
        return res;
    }

    String getName();

    public List<Directions> getRoadDirections();

    public List<Directions> getReverseRoadDirections();

    enum StartType implements CardType, CardEdge {
        START;

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public List<Directions> getRoadDirections() {
            return Arrays.asList(Directions.NORTH, Directions.EAST, Directions.SOUTH, Directions.WEST);
        }

        @Override
        public List<Directions> getReverseRoadDirections() {
            return rotateDirections(getRoadDirections());
        }
    }

    enum EndType implements CardType, CardEdge {
        FINISH_TREASURE, FINISH_TURN_LEFT, FINISH_TURN_RIGHT;

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public List<Directions> getRoadDirections() {
            switch (this) {
                case FINISH_TREASURE:
                    return Arrays.asList(Directions.NORTH, Directions.EAST, Directions.SOUTH, Directions.WEST);
                case FINISH_TURN_LEFT:
                    return Arrays.asList(Directions.SOUTH, Directions.WEST);
                case FINISH_TURN_RIGHT:
                    return Arrays.asList(Directions.NORTH, Directions.EAST);
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public List<Directions> getReverseRoadDirections() {
            return rotateDirections(getRoadDirections());
        }
    }

    enum RoadType implements CardType, CardEdge {
        ROAD_CORNER_LEFT, ROAD_CORNER_RIGHT, ROAD_CROSS, ROAD_HORIZONTAL_2,
        ROAD_VERTICAL_1_HORIZONTAL_2, ROAD_VERTICAL_2, ROAD_VERTICAL_2_HORIZONTAL_1;

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public List<Directions> getRoadDirections() {
            switch (this) {
                case ROAD_CORNER_LEFT:
                    return Arrays.asList(Directions.NORTH, Directions.EAST);
                case ROAD_CORNER_RIGHT:
                    return Arrays.asList(Directions.NORTH, Directions.WEST);
                case ROAD_CROSS:
                    return Arrays.asList(Directions.NORTH, Directions.EAST, Directions.SOUTH, Directions.WEST);
                case ROAD_HORIZONTAL_2:
                    return Arrays.asList(Directions.EAST, Directions.WEST);
                case ROAD_VERTICAL_1_HORIZONTAL_2:
                    return Arrays.asList(Directions.NORTH, Directions.EAST, Directions.WEST);
                case ROAD_VERTICAL_2:
                    return Arrays.asList(Directions.NORTH, Directions.SOUTH);
                case ROAD_VERTICAL_2_HORIZONTAL_1:
                    return Arrays.asList(Directions.NORTH, Directions.EAST, Directions.SOUTH);
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public List<Directions> getReverseRoadDirections() {
            return rotateDirections(getRoadDirections());
        }
    }

    enum BlockType implements CardType, CardEdge {
        BLOCK_CORNER_LEFT, BLOCK_CORNER_RIGHT, BLOCK_CROSS, BLOCK_HORIZONTAL_2,
        BLOCK_VERTICAL_1_HORIZONTAL_2, BLOCK_VERTICAL_2, BLOCK_VERTICAL_2_HORIZONTAL_1, BLOCK_VERTICAL_1, BLOCK_HORIZONTAL_1;

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public List<Directions> getRoadDirections() {
            switch (this) {
                case BLOCK_CORNER_LEFT:
                    return Arrays.asList(Directions.NORTH, Directions.EAST);
                case BLOCK_CORNER_RIGHT:
                    return Arrays.asList(Directions.NORTH, Directions.WEST);
                case BLOCK_CROSS:
                    return Arrays.asList(Directions.NORTH, Directions.EAST, Directions.SOUTH, Directions.WEST);
                case BLOCK_HORIZONTAL_1:
                    return Collections.singletonList(Directions.WEST);
                case BLOCK_HORIZONTAL_2:
                    return Arrays.asList(Directions.EAST, Directions.WEST);
                case BLOCK_VERTICAL_1:
                    return Collections.singletonList(Directions.SOUTH);
                case BLOCK_VERTICAL_1_HORIZONTAL_2:
                    return Arrays.asList(Directions.EAST, Directions.SOUTH, Directions.WEST);
                case BLOCK_VERTICAL_2:
                    return Arrays.asList(Directions.NORTH, Directions.SOUTH);
                case BLOCK_VERTICAL_2_HORIZONTAL_1:
                    return Arrays.asList(Directions.NORTH, Directions.EAST, Directions.SOUTH);
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public List<Directions> getReverseRoadDirections() {
            return rotateDirections(getRoadDirections());
        }
    }

    enum Back implements CardType {
        BACK_NORMAL, BACK_FINISH, BACK_GOLD, BACK_ROLE;

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public List<Directions> getRoadDirections() {
            // TODO langa cartile BACK se poate pune orice fel de carte de drum
            return Arrays.asList(Directions.NORTH, Directions.EAST, Directions.SOUTH, Directions.WEST);
        }

        @Override
        public List<Directions> getReverseRoadDirections() {
            return null;
        }
    }

    interface ActionType extends CardType {
        enum SpecialType implements ActionType {
            ACTION_MAP, ACTION_AVALANCHE;

            @Override
            public String getName() {
                return this.name();
            }

            @Override
            public List<Directions> getRoadDirections() {
                return null;
            }

            @Override
            public List<Directions> getReverseRoadDirections() {
                return null;
            }
        }

        enum BlockType implements ActionType {
            ACTION_BLOCK_PICKAXE, ACTION_BLOCK_CART, ACTION_BLOCK_LAMP;

            @Override
            public String getName() {
                return this.name();
            }

            @Override
            public List<Directions> getRoadDirections() {
                return null;
            }

            @Override
            public List<Directions> getReverseRoadDirections() {
                return null;
            }
        }

        enum UnblockType implements ActionType {
            ACTION_UNBLOCK_PICKAXE, ACTION_UNBLOCK_CART, ACTION_UNBLOCK_LAMP,
            ACTION_UNBLOCK_PICKAXE_CART, ACTION_UNBLOCK_CART_LAMP, ACTION_UNBLOCK_LAMP_PICKAXE;

            @Override
            public String getName() {
                return this.name();
            }

            @Override
            public List<Directions> getRoadDirections() {
                return null;
            }

            @Override
            public List<Directions> getReverseRoadDirections() {
                return null;
            }
        }
    }
}
