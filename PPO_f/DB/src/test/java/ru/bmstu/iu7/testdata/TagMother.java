package ru.bmstu.iu7.testdata;

import ru.bmstu.iu7.API.model.ATag;
import ru.bmstu.iu7.impl.ModelFactory;
import ru.bmstu.iu7.impl.model.Tag;

public class TagMother {
    public static Tag defaultTag() {
        return new TagBuilder().withId(0L).withName("default").build();
    }

    public static ATag defaultATag() {
        return ModelFactory.Tag2ATag(new TagBuilder().withId(0L).withName("default").build());
    }

}
