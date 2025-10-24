package ru.bmstu.iu7.testdata;

import ru.bmstu.iu7.API.model.AQuestionnaire;
import ru.bmstu.iu7.impl.ModelFactory;
import ru.bmstu.iu7.impl.model.Questionnaire;

public class QuestionnaireMother {
    public static AQuestionnaire lenaQuestionnaire() {
        return AQuestionnaireBuilder.defaultQuestionnaire()
                .withId(0L)
                .build();
    }

    public static AQuestionnaire ivanQuestionnaire() {
        return AQuestionnaireBuilder.defaultQuestionnaire()
                .withId(1L)
                .build();
    }
    public static Questionnaire lenaQuestionnaireEntity() {
        AQuestionnaire aQ = QuestionnaireMother.lenaQuestionnaire();
        return ModelFactory.AQuestionnaire2Questionnaire(aQ);
    }
}
