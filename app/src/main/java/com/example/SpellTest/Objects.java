package com.example.SpellTest;

public class Objects {
    public static class User {

        public int id;
        public String firstName;
        public String lastName;

        public User (int id, String firstName, String lastName){
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public static class SpellingList {
        public int id;
        public String name;
        public int userId;

        public SpellingList (int id, String name, int userId) {
            this.id = id;
            this.name = name;
            this.userId = userId;
        }
    }
}
