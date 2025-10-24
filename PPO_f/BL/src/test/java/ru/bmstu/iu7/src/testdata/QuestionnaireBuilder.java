package ru.bmstu.iu7.src.testdata;

import ru.bmstu.iu7.API.model.*;

import java.util.ArrayList;
import java.util.Arrays;

public class QuestionnaireBuilder {

    public static AInformation buildAInformationTemplate() {
        AVariantAnswer variantAnswer = new AVariantAnswer(
                2,
                new ATag(0L, "walking"),
                new AQuestion(0L, "Do you love walking or swimming?",
                        Arrays.asList(
                                new ATag(0L, "walking"),
                                new ATag(1L, "swimming")
                        ),
                        false)
        );

        AExtendedAnswer extendedAnswer = new AExtendedAnswer(
                new AQuestion(1L, "How do you like to spend your time?",
                        Arrays.asList(
                                new ATag(2L, "walking"),
                                new ATag(3L, "sleeping")
                        ),
                        true),
                0,
                "",
                new ArrayList<>()
        );

        return new AInformation(
                0L,
                new ArrayList<>(Arrays.asList(variantAnswer)),
                new ArrayList<>(Arrays.asList(extendedAnswer))
        );
    }
}
