package com.taylorgirard.comicconvo.tools;

public enum ListType {
    LIKES("Likes"),
    DISLIKES("Dislikes");

    private final String type;

    private ListType(String type){
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
