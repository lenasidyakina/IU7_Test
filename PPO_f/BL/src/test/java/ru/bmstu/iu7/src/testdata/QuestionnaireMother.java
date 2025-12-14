package ru.bmstu.iu7.src.testdata;

import ru.bmstu.iu7.API.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionnaireMother {

    private static AInformation buildAInformationWithUserAnswer(long infoId, String userAnswerText,
                                                                List<ATag> answerTags) {
        AInformation info = QuestionnaireBuilder.buildAInformationTemplate();
        info.setId(infoId);

        AExtendedAnswer userExtended = new AExtendedAnswer(
                info.getExtendedAnswers().get(0).getQuestion(),
                2,
                userAnswerText,
                answerTags
        );

        info.setExtendedAnswers(Arrays.asList(userExtended));
        return info;
    }

    public static AQuestionnaire lenaQuestionnaire() {
        AInformation info = buildAInformationWithUserAnswer(
                2L,
                "I like walking my dog and sleeping",
                Arrays.asList(new ATag(2L, "walking"))
        );

        AInformation infoSearch = buildAInformationWithUserAnswer(
                3L,
                "I like walking my dog and sleeping",
                Arrays.asList(new ATag(3L, "sleeping"))
        );

        AUser user = new AUser(0L, "Lena", "Lena12345");
        return new AQuestionnaire(2L, user, info, infoSearch, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static AQuestionnaire iraQuestionnaire() {
        AInformation info = buildAInformationWithUserAnswer(
                0L,
                "I like walking my dog.",
                Arrays.asList(new ATag(2L, "walking"))
        );

        AInformation infoSearch = buildAInformationWithUserAnswer(
                1L,
                "I like walking my dog.",
                Arrays.asList(new ATag(3L, "sleeping"))
        );

        AUser user = new AUser(1L, "Ira", "12345");
        return new AQuestionnaire(1L, user, info, infoSearch, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static AQuestionnaire differentVariantQuestionnaire() {
        AInformation info = buildAInformationWithUserAnswer(
                4L,
                "I like swimming and reading.",
                Arrays.asList(new ATag(4L, "swimming"))
        );
        AInformation infoSearch = buildAInformationWithUserAnswer(
                5L,
                "I like swimming and reading.",
                Arrays.asList(new ATag(5L, "reading"))
        );
        AUser user = new AUser(2L, "Different", "diff123");
        return new AQuestionnaire(2L, user, info, infoSearch, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static AQuestionnaire fullMatchQuestionnaire() {
        AInformation info = buildAInformationWithUserAnswer(
                6L,
                "I love coding and chess.",
                Arrays.asList(new ATag(6L, "coding"))
        );
        AInformation infoSearch = buildAInformationWithUserAnswer(
                7L,
                "I love coding and chess.",
                Arrays.asList(new ATag(7L, "chess"))
        );
        AUser user = new AUser(3L, "FullMatch", "full123");
        return new AQuestionnaire(3L, user, info, infoSearch, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static AQuestionnaire fullMatchQuestionnaireOther() {
        AInformation info = buildAInformationWithUserAnswer(
                6L,
                "I love coding and chess.",
                Arrays.asList(new ATag(7L, "chess"))
        );
        AInformation infoSearch = buildAInformationWithUserAnswer(
                7L,
                "I love coding and chess.",
                Arrays.asList(new ATag(6L, "coding"))
        );
        AUser user = new AUser(3L, "FullMatch", "full123");
        return new AQuestionnaire(3L, user, info, infoSearch, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static AQuestionnaire otherQuestionnaire() {
        AInformation info = buildAInformationWithUserAnswer(
                8L,
                "I enjoy hiking.",
                Arrays.asList(new ATag(8L, "hiking"))
        );
        AInformation infoSearch = buildAInformationWithUserAnswer(
                9L,
                "I enjoy hiking.",
                Arrays.asList(new ATag(9L, "mountains"))
        );
        AUser user = new AUser(4L, "Other", "other123");
        return new AQuestionnaire(4L, user, info, infoSearch, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static List<AQuestionnaire> getTwoQuestionnaires() {
        return Arrays.asList(iraQuestionnaire(), lenaQuestionnaire());
    }


    public static List<AQuestionnaire> getTwoQuestionnairesReversed() {
        List<AQuestionnaire> list = getTwoQuestionnaires();
        List<AQuestionnaire> reversed = new ArrayList<>(list);
        java.util.Collections.reverse(reversed);
        return reversed;
    }

    public static AInformation infoWalking() {
        return buildAInformationWithUserAnswer(
                10L,
                "I like walking my dog.",
                Arrays.asList(new ATag(2L, "walking"))
        );
    }

    public static AInformation infoSleeping() {
        return buildAInformationWithUserAnswer(
                11L,
                "I like Sleeping in the pool.",
                Arrays.asList(new ATag(7L, "Sleeping"))
        );
    }

    public static AInformation infoSwimming() {
        return buildAInformationWithUserAnswer(
                12L,
                "I like Swimming in the pool.",
                Arrays.asList(new ATag(6L, "Swimming"))
        );
    }

    public static AQuestionnaire lenaQuestionnaireWithInfo(AInformation info1, AInformation info2) {
        AUser user = new AUser(0L, "Lena", "Lena12345");
        return new AQuestionnaire(
                0L,
                user,
                info1,
                info2,
                new ArrayList<>(),
                new ArrayList<>(),
                false
        );
    }
}
