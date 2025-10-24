package ru.bmstu.iu7.API.dto;

public class QuestionAnswerDTO {
    private String type;       // "EXT" или "VAR"
    private String answer;
    private int weight;

    // геттеры и сеттеры
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
}


