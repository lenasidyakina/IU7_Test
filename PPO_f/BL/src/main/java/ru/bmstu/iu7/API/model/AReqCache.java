package ru.bmstu.iu7.API.model;

public class AReqCache {

    private Long id;
    private AQuestionnaire questionnaire1;
    private AQuestionnaire questionnaire2;
    private double factor;

    public AReqCache(Long id, double factor, AQuestionnaire questionnaire1, AQuestionnaire questionnaire2) {
        this.id = id;
        this.factor = factor;
        this.questionnaire1 = questionnaire1;
        this.questionnaire2 = questionnaire2;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getFactor() {
        return this.factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public AQuestionnaire getQuestionnaire1() {
        return this.questionnaire1;
    }

    public void setQuestionnaire1(AQuestionnaire questionnaire1) {
        this.questionnaire1 = questionnaire1;
    }

    public AQuestionnaire getQuestionnaire2() {
        return this.questionnaire2;
    }

    public void setQuestionnaire2(AQuestionnaire questionnaire2) {
        this.questionnaire2 = questionnaire2;
    }
}
