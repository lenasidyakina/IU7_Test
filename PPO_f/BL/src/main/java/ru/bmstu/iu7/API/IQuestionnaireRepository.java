package ru.bmstu.iu7.API;

import ru.bmstu.iu7.API.model.*;

import java.util.List;

public interface IQuestionnaireRepository {

    List<? extends AQuestionnaire> findUserQuestionnaires(Long id);
    AQuestionnaire createQuestionnaire(AUser user,
                                              AInformation information,
                                              AInformation search_info);
    AQuestionnaire update(AQuestionnaire questionnaire);
    void delete(AQuestionnaire questionnaire);
    List<AQuestionnaire> get_page(int no, int size, Long user_id) throws Exception;
    AQuestionnaire findQuestionnaire(Long id);
    List<AQuestion> get_all_questions();
    List<AQuestionnaire> get_all_questionnaires();
    void deleteUserQuestionnaires(Long userId);
    void deleteQuestionnaire(AQuestionnaire questionnaire);
    AQuestion appendQuestion(boolean kind, String name, List<ATag> tags);
    ATag appendTag(String name);
    ATag findTag(String name);
}
