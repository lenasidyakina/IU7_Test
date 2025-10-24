package ru.bmstu.iu7.API.dto;

// DTO
public class QuestionSubmission {
    private String type;     // "EXT" или "VAR"
    private String answer1;
    private int weight1;
    private String answer2;
    private int weight2;

    // геттеры и сеттеры
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAnswer1() { return answer1; }
    public void setAnswer1(String answer1) { this.answer1 = answer1; }

    public int getWeight1() { return weight1; }
    public void setWeight1(int weight1) { this.weight1 = weight1; }

    public String getAnswer2() { return answer2; }
    public void setAnswer2(String answer2) { this.answer2 = answer2; }

    public int getWeight2() { return weight2; }
    public void setWeight2(int weight2) { this.weight2 = weight2; }
}



