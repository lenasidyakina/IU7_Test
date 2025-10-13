package ru.bmstu.iu7.impl.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Question  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String question;
    @OneToMany(fetch = FetchType.EAGER)
    List<Tag> tags;
    boolean is_extended = false;

    public boolean getIs_extended() {
        return is_extended;
    }
    public void setIs_extended(boolean is_extended) {
        this.is_extended = is_extended;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
}
