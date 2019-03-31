package com.example.SpellTest;

import java.util.Date;

public class Objects {
    public static class User {

        public long id;
        public String firstName;
        public String lastName;

        public User (long id, String firstName, String lastName){
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public static class SpellingList {
        public long id;
        public String name;
        public long userId;

        public SpellingList (long id, String name, long userId) {
            this.id = id;
            this.name = name;
            this.userId = userId;
        }
    }

    public static class SpellingListStat {
        public long id;
        public long listId;
        public int numberCorrect;
        public int numberIncorrect;
        public long elapsedTime;
        public long date;

        public SpellingListStat(long id, long listId, long date, long elapsedTime, int numberCorrect, int numberIncorrect){
            this.id = id;
            this.listId = listId;
            this.date = date;
            this.elapsedTime = elapsedTime;
            this.numberCorrect = numberCorrect;
            this.numberIncorrect = numberIncorrect;
        }
    }
}
