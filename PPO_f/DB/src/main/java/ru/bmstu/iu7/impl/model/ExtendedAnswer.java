package ru.bmstu.iu7.impl.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
public class ExtendedAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne()
    Question question;

    int weight;
    String answer;

    @OneToMany(fetch = FetchType.EAGER)
    List<Tag> tags;

    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id=id; }

    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public List<Tag> getTags() {
        return tags;
    }
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Question getQuestion() {
        return question;
    }
    public void setQuestion(Question question) {
        this.question = question;
    }

}
