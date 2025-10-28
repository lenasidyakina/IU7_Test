package ru.bmstu.iu7.testdata;

import ru.bmstu.iu7.impl.model.Question;
import ru.bmstu.iu7.impl.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class QuestionBuilder {

    private Long id = 0L;
    private String questionText = "Default question?";
    private List<Tag> tags = new ArrayList<>();
    private boolean isExtended = false;

    public static QuestionBuilder defaultQuestion() {
        return new QuestionBuilder()
                .withQuestionText("Do you like walking or swimming?")
                .withIsExtended(false)
                .withTags(new ArrayList<>(List.of(new TagBuilder().withId(0L).withName("walking").build(),
                        new TagBuilder().withId(1L).withName("swimming").build())));
    }

    public QuestionBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public QuestionBuilder withQuestionText(String text) {
        this.questionText = text;
        return this;
    }

    public QuestionBuilder withTags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public QuestionBuilder withIsExtended(boolean isExtended) {
        this.isExtended = isExtended;
        return this;
    }

    public Question build() {
        Question q = new Question();
        q.setId(id);
        q.setQuestion(questionText);
        q.setTags(tags);
        q.setIs_extended(isExtended);
        return q;
    }
}
