/**
 * Filename:  Objects.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 *
 * Purpose:  This class represents POJO (plain old Java object) data structures for each of the
 * main application data objects, such as users, spelling lists, and individual words.  All data
 * transfer to / from the main application database occurs through objects instantiated from this
 * class.
 */


package com.example.spelltest;

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

    public static class Word {
        public long id;
        public long list_id;
        public String spelling;

        public Word (long id, long list_id, String spelling){
            this.id = id;
            this.list_id = list_id;
            this.spelling = spelling;
        }

    }


}
