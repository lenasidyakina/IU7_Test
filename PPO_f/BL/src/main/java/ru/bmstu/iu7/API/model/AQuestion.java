package ru.bmstu.iu7.API.model;

import java.util.List;


public class AQuestion {
    Long id;
    String question;
    List<ATag> tags;
    boolean is_extended;

    public AQuestion(Long id, String question, List<ATag> tags, boolean kind) {
        this.id = id;
        this.question = question;
        this.tags = tags;
        this.is_extended = kind;
    }

    public AQuestion() {
    }


    public AQuestion(String question, List<ATag> tags, boolean kind) {
        this.id = id;
        this.question = question;
        this.tags = tags;
        this.is_extended = kind;
    }

    public boolean getIs_extended() {
        return is_extended;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ATag> getTags() {
        return tags;
    }

    public void setTags(List<ATag> tags) {
        this.tags = tags;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
