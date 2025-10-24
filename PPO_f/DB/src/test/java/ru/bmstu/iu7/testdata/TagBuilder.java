package ru.bmstu.iu7.testdata;

import ru.bmstu.iu7.impl.model.Tag;

public class TagBuilder {
    private Long id = 0L;
    private String name = "default";

    public TagBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public TagBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public Tag build() {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return tag;
    }
}
