package ru.bmstu.iu7.API.dto;
import java.util.List;

public class QuestSubmissionDTO {
    private List<QuestionAnswerDTO> mainAnswers;   // обычная информация
    private List<QuestionAnswerDTO> otherAnswers;  // дополнительная информация

    public List<QuestionAnswerDTO> getMainAnswers() { return mainAnswers; }
    public void setMainAnswers(List<QuestionAnswerDTO> mainAnswers) { this.mainAnswers = mainAnswers; }
    public List<QuestionAnswerDTO> getOtherAnswers() { return otherAnswers; }
    public void setOtherAnswers(List<QuestionAnswerDTO> otherAnswers) { this.otherAnswers = otherAnswers; }
}

