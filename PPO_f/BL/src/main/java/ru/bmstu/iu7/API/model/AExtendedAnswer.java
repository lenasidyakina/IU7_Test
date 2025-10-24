package ru.bmstu.iu7.API.model;


import java.util.List;

public class AExtendedAnswer  {

    Long id;
    AQuestion question;
    int weight;
    String answer;
    List<ATag> tags;

    public AExtendedAnswer(Long id, AQuestion question, int weight, String answer, List<ATag> tags) {
        this.id = id;
        this.question = question;
        this.weight = weight;
        this.answer = answer;
        this.tags = tags;
    }
    public AExtendedAnswer(AQuestion question, int weight, String answer, List<ATag> tags) {
        this.question = question;
        this.weight = weight;
        this.answer = answer;
        this.tags = tags;
    }


    public Long getId() {
        return id;
    }
    public int getWeight() {
        return weight;
    }
    public String getAnswer() {
        return answer;
    }
    public List<ATag> getTags() {
        return tags;
    }
    public void setTags(List<ATag> tags) {
        this.tags = tags;
    }
    public AQuestion getQuestion() {
        return question;
    }
    public void setQuestion(AQuestion question) {
        this.question = question;
    }

}
