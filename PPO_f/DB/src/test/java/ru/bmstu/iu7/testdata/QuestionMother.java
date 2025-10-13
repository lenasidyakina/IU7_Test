package ru.bmstu.iu7.testdata;

import ru.bmstu.iu7.impl.model.Question;

public class QuestionMother {


    public static Question extendedQuestion() {
        return QuestionBuilder.defaultQuestion()
                .withId(2L)
                .withQuestionText("How do you spend your free time?")
                .withIsExtended(true)
                .build();
    }
}
