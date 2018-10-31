package com.github.quadinsa5if.findingandqueryingtext.lang;

public class Unit {

    private static Unit singleton = new Unit();

    private Unit() {}

    public static Unit create() {
        return singleton;
    }

}
