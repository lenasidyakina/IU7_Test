package ru.bmstu.iu7.API.model;

public class AVariantAnswer
{
    Long id;
    int weight;
    ATag tag;
    AQuestion question;
    public AVariantAnswer(Long id, int weight, ATag tag, AQuestion question) {
        this.id = id;
        this.weight = weight;
        this.tag = tag;
        this.question = question;
    }
    public AVariantAnswer(int weight, ATag tag, AQuestion question) {
        this.weight = weight;
        this.tag = tag;
        this.question = question;
    }
    public Long getId() {
        return id;
    }
    public void setQuestion(AQuestion question) {
        this.question = question;
    }
    public AQuestion getQuestion() {
        return question;
 }
    public void setTag(ATag tag) {
        this.tag = tag;
    }
    public int getWeight() {
        return weight;
    }
    public ATag getTag() {
        return tag;
    }
}
