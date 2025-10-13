package ru.bmstu.iu7.impl.model;

import jakarta.persistence.*;

@Entity
public class VariantAnswer
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    int weight;

    @ManyToOne()
    Tag tag;

    @ManyToOne()
    Question question;

    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id = id; }
    public void setWeight(int weight) {
        this.weight = weight;
 }
    public void setQuestion(Question question) {
        this.question = (Question)question;
    }
    public Question getQuestion() {
        return question;
 }
    public void setTag(Tag tag) {
        this.tag = tag;
    }
    public int getWeight() {
        return weight;
    }
    public Tag getTag() {
        return tag;
    }
}
