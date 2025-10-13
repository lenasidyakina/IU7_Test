package ru.bmstu.iu7.impl.model;


import jakarta.persistence.*;

@Entity
public class ReqCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne()
    Questionnaire questionnaire1;

    @ManyToOne()
    Questionnaire questionnaire2;

    double Factor;


    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id=id; }

    public double getFactor() {
        return this.Factor;
    }
    public void setFactor(double factor) {
        this.Factor = factor;
    }

    public Questionnaire getQuestionnaire1() {
        return questionnaire1;
    }
    public void setQuestionnaire1(Questionnaire questionnaire1) { this.questionnaire1 = questionnaire1; }
    public Questionnaire getQuestionnaire2() {
        return questionnaire2;
    }
    public void setQuestionnaire2(Questionnaire questionnaire2) { this.questionnaire2 = questionnaire2; }


}
