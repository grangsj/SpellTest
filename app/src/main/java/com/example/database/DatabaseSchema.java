package com.example.database;

public class DatabaseSchema {

    public static final class UserTable {
        public static final String NAME = "user";

        public static final class Cols {
            public static final String ID = "user_id";
            public static final String FIRST_NAME = "first_name";
            public static final String LAST_NAME = "last_name";
        }
    }

    public static final class WordTable {
        public static final String NAME = "word";

        public static final class Cols {
            public static final String ID = "word_id";
            public static final String LIST_ID = "list_id";
            public static final String SPELLING = "word_spelling";
            public static final String DEFINITION = "definition";
            public static final String EXAMPLE_SENTENCE = "example_sentence";
            public static final String DIFFICULTY_ID = "difficulty_id";
            public static final String TYPE = "type";
        }
    }

    public static final class DifficultyTable {
        public static final String NAME = "difficulty";

        public static final class Cols {
            public static final String ID = "word_stat_id";
            public static final String DESCRIPTION = "description";
        }
    }

    public static final class SpellingListTable {
        public static final String NAME = "spellingList";

        public static final class Cols {
            public static final String ID = "spelling_list_id";
            public static final String USER_ID = "user_id";
            public static final String NAME = "name";
            public static final String TYPE = "type";
            public static final String DIFFICULTY_ID = "difficulty_id";
        }
    }

    public static final class SpellingListStatTable {
        public static final String NAME = "spellingListStats";

        public static final class Cols {
            public static final String ID = "spelling_list_stat_id";
            public static final String LIST_ID = "spelling_list_id";
            public static final String DATE = "date";
            public static final String ELAPSED_TIME = "elapsed_time";
            public static final String NUMBER_CORRECT = "numberCorrect";
            public static final String NUMBER_INCORRECT = "numberIncorrect";

        }
    }



    /**
     public static final class UserStatTable {
     public static final String NAME = "user_stat";

     public static final class Cols {
     public static final String ID = "user_stat_id";
     public static final String USER_ID = "user_id";
     public static final String OVERALL_ACCURACY = "overall_accuracy";
     public static final String TOTAL_TESTS_TAKEN = "total_tests_taken";
     }
     }

     public static final class UserWordStatTable {
     public static final String NAME = "user_word_stat";

     public static final class Cols {
     public static final String ID = "user_word_stat_id";
     public static final String USER_STAT_ID = "user_stat_id";
     public static final String WORD_STAT_ID = "word_stat_id";
     public static final String USER_WORD_ACCURACY = "user_word_accuracy";
     }
     }

     public static final class WordStatTable {
     public static final String NAME = "word_stat";

     public static final class Cols {
     public static final String ID = "word_stat_id";
     public static final String WORD_ID = "word_id";
     public static final String OVERALL_ACCURACY = "overall_accuracy";
     }
     }
     **/

}
