package ru.bmstu.iu7.testdata;

import ru.bmstu.iu7.API.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AQuestionnaireBuilder {
    private Long id = 0L;
    private AUser user = new AUser("Lena", "Lena12345");
    private AInformation info1;
    private AInformation info2;
    private List<AQuestionnaire> blackList = new ArrayList<>();
    private List<AQuestionnaire> favList = new ArrayList<>();
    private boolean deleted = false;

    public static AQuestionnaireBuilder defaultQuestionnaire() {
        return new AQuestionnaireBuilder()
                .withInfo1(createDefaultInfo(0L))
                .withInfo2(createDefaultInfo(1L));
    }

    private static AInformation createDefaultInfo(Long id) {
        return new AInformation(id,
                new ArrayList<>(Arrays.asList(
                        new AVariantAnswer(2,
                                new ATag(0L, "walking"),
                                new AQuestion(0L,
                                        "Do you love walking or swimming?",
                                        new ArrayList<>(Arrays.asList(
                                                new ATag(0L, "walking"),
                                                new ATag(1L, "swimming"))),
                                        false))
                )),
                new ArrayList<>(Arrays.asList(
                        new AExtendedAnswer(
                                new AQuestion(1L,
                                        "How do you like to spend your time? ",
                                        new ArrayList<>(Arrays.asList(
                                                new ATag(2L, "walking"),
                                                new ATag(3L, "sleeping"))),
                                        true),
                                2,
                                "I like walking my dog.",
                                new ArrayList<>(Arrays.asList(
                                        new ATag(2L, "walking"))))
                )));
    }

    public AQuestionnaireBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public AQuestionnaireBuilder withUser(AUser user) {
        this.user = user;
        return this;
    }

    public AQuestionnaireBuilder withInfo1(AInformation info1) {
        this.info1 = info1;
        return this;
    }

    public AQuestionnaireBuilder withInfo2(AInformation info2) {
        this.info2 = info2;
        return this;
    }

    public AQuestionnaire build() {
        return new AQuestionnaire(id, user, info1, info2, blackList, favList, deleted);
    }
}
