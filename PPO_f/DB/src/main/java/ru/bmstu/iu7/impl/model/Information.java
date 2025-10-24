package ru.bmstu.iu7.impl.model;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Information {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<VariantAnswer> variant_answers;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<ExtendedAnswer> extended_answers;

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) { this.id = id; }
    public List<ExtendedAnswer> getExtendedAnswers() {
        return this.extended_answers;
    }
    public void setExtendedAnswers(List<ExtendedAnswer> extendedAnswers) {
        this.extended_answers = extendedAnswers;
    }

    public List<VariantAnswer> getVariantAnswers() {
        return this.variant_answers;
    }
    public void setVariantAnswers(List<VariantAnswer> variantAnswers) {
        this.variant_answers = variantAnswers;
    }
    public String toString() {
        return String.valueOf(id);
    }
}
