package com.alex.reactivaspring.document;

public enum TitleEnum {
    HELLO {
        public String getType() {
            return "Hello";
        }
    },
    BYE {
        public String getType() {
            return "Bye";
        }
    };

    public abstract String getType();

}
