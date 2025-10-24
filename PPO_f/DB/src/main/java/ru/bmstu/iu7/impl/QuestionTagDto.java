package ru.bmstu.iu7.impl;

public class QuestionTagDto {
    private Integer questionId;
    private String questionText;
    private Integer tagId;
    private String tagName;

    public QuestionTagDto(Integer questionId, String questionText, Integer tagId, String tagName) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public String getTagName() { return tagName; }
}
