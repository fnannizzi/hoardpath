package com.fnannizz;

/**
 * Used to implement a priority queue with integer priority values, as well as grouping
 * string room ids to their numeric array index.
 */
class PairStringInteger implements Comparable<PairStringInteger> {
    private String string;
    private Integer integer;

    PairStringInteger(String s, Integer i) {
        string = s;
        integer = i;
    }

    String getStr() {
        return string;
    }

    Integer getInteger() {
        return integer;
    }

    @Override public int compareTo(PairStringInteger other) {
        return Integer.compare(this.integer, other.integer);
    }
}