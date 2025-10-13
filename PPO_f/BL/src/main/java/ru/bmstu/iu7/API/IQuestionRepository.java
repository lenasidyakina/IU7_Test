package ru.bmstu.iu7.API;

import ru.bmstu.iu7.API.model.AQuestion;
import ru.bmstu.iu7.API.model.ATag;

import java.util.List;

public interface IQuestionRepository {
    List<AQuestion> get_questions() throws Exception;
    List<ATag> get_question_tags(int id) throws Exception;
}
