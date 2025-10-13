package ru.bmstu.iu7.API.model;

import java.util.List;

public class AInformation {

    Long id;
    List<AVariantAnswer> variant_answers;
    List<AExtendedAnswer> extended_answers;

    public AInformation(Long id, List<AVariantAnswer> variant_answers, List<AExtendedAnswer> extended_answers) {
        this.id = id;
        this.variant_answers = variant_answers;
        this.extended_answers = extended_answers;
    }

    public AInformation(List<AVariantAnswer> variant_answers, List<AExtendedAnswer> extended_answers) {
        this.variant_answers = variant_answers;
        this.extended_answers = extended_answers;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {this.id = id; }

    public List<AExtendedAnswer> getExtendedAnswers() {
        return this.extended_answers;
    }
    public void setExtendedAnswers(List<AExtendedAnswer> extendedAnswers) {
        this.extended_answers = extendedAnswers;
    }
    public List<AVariantAnswer> getVariantAnswers() {
        return this.variant_answers;
    }
    public String toString() {
        return String.valueOf(id);
    }
}
