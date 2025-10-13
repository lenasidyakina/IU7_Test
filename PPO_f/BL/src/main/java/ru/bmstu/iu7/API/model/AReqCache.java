package ru.bmstu.iu7.API.model;

public class AReqCache {

    Long id;
    AQuestionnaire questionnaire1;
    AQuestionnaire questionnaire2;
    double Factor;

    public AReqCache(Long id, double Factor, AQuestionnaire questionnaire1, AQuestionnaire questionnaire2) {
        this.id = id;
        this.Factor = Factor;
        this.questionnaire1 = questionnaire1;
        this.questionnaire2 = questionnaire2;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id=id; }

    public double getFactor() {
        return this.Factor;
    }
    public AQuestionnaire getQuestionnaire1() {
        return questionnaire1;
    }
    public AQuestionnaire getQuestionnaire2() {
        return questionnaire2;
    }


}

