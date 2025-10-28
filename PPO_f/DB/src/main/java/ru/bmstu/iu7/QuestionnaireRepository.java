package ru.bmstu.iu7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.bmstu.iu7.API.IQuestionnaireRepository;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.impl.*;
import ru.bmstu.iu7.impl.model.*;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireRepository implements IQuestionnaireRepository {

    DBAPI api;
    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireRepository.class);

    public QuestionnaireRepository(DBAPI api) {
        this.api = api;
        logger.info("QuestionnaireRepository initialized");
    }

    @Override
    public List<AQuestionnaire> findUserQuestionnaires(Long id) {
        try {
            logger.info("Fetching questionnaires for user with id {}", id);
            return ModelFactory.Questionnaires2AQuestionnaires(api.m_questionnaireRepository.findByUserId(id));
        } catch (Exception e) {
            logger.error("Failed to fetch questionnaires for user {}: {}", id, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public AQuestionnaire createQuestionnaire(AUser user, AInformation information, AInformation search_info) {
        try {
            logger.info("Creating questionnaire for user {}", user.getName());
            AQuestionnaire questionnaire = new AQuestionnaire(0L, user, information, search_info,
                    new ArrayList<>(), new ArrayList<>(), false);
            Questionnaire q = ModelFactory.AQuestionnaire2QuestionnaireNew(questionnaire);
            q = api.m_questionnaireRepository.save(q);
            logger.info("Questionnaire created with id {}", q.getId());
            return ModelFactory.Questionnaire2AQuestionnaire(q);
        } catch (Exception e) {
            logger.error("Failed to create questionnaire for user {}: {}", user.getName(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public AQuestionnaire update(AQuestionnaire questionnaire) {
        try {
            logger.info("Updating questionnaire with id {}", questionnaire.getId());
            return ModelFactory.Questionnaire2AQuestionnaire(
                    api.m_questionnaireRepository.save(ModelFactory.AQuestionnaire2Questionnaire(questionnaire)));
        } catch (Exception e) {
            logger.error("Failed to update questionnaire with id {}: {}", questionnaire.getId(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void delete(AQuestionnaire questionnaire) {
        try {
            logger.info("Deleting questionnaire with id {}", questionnaire.getId());
            api.m_questionnaireRepository.delete(ModelFactory.AQuestionnaire2Questionnaire(questionnaire));
        } catch (Exception e) {
            logger.error("Failed to delete questionnaire with id {}: {}", questionnaire.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<AQuestionnaire> get_page(int pageNumber, int pageSize, Long user_id) throws Exception {
        try {
            logger.info("Fetching page {} of questionnaires excluding user {}", pageNumber, user_id);
            return ModelFactory.Questionnaires2AQuestionnaires(
                    api.m_questionnaireRepository
                            .findAllByUserIdNot(user_id,
                                    PageRequest.of(pageNumber, pageSize, Sort.by("id").descending()))
                            .toList()
            );
        } catch (Exception e) {
            logger.error("Failed to fetch page {} for user {}: {}", pageNumber, user_id, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public AQuestionnaire findQuestionnaire(Long id) {
        try {
            logger.info("Finding questionnaire with id {}", id);
            return ModelFactory.Questionnaire2AQuestionnaire(
                    api.m_questionnaireRepository.findById(id).orElse(null));
        } catch (Exception e) {
            logger.error("Failed to find questionnaire with id {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<AQuestion> get_all_questions() {
        try {
            logger.info("Fetching all questions");
            return ModelFactory.Questions2AQuestions(api.m_questionRepository.findAll());
        } catch (Exception e) {
            logger.error("Failed to fetch all questions: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public void deleteQuestionnaire(AQuestionnaire questionnaire) {
        try {
            logger.info("Deleting questionnaire with id {}", questionnaire.getId());
            api.m_questionnaireRepository.deleteByIdNative(questionnaire.getId());
        } catch (Exception e) {
            logger.error("Failed to delete questionnaire with id {}: {}", questionnaire.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<AQuestionnaire> get_all_questionnaires() {
        try {
            logger.info("Fetching all questionnaires");
            return ModelFactory.Questionnaires2AQuestionnaires(api.m_questionnaireRepository.findAll());
        } catch (Exception e) {
            logger.error("Failed to fetch all questionnaires: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public void deleteUserQuestionnaires(Long userId) {
        try {
            logger.info("Deleting all questionnaires for user with id {}", userId);
            api.m_questionnaireRepository.deleteByUserId(userId);
        } catch (Exception e) {
            logger.error("Failed to delete all questionnaires for user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AQuestion appendQuestion(boolean kind, String text, List<ATag> tags) {
        try {
            logger.info("Appending question: '{}' with {} tags", text, tags.size());
            Question question = api.m_questionRepository.findByQuestion(text);
            if (question == null) {
                question = api.m_questionRepository.save(
                        (Question) ModelFactory.makeQuestion(null, text, ModelFactory.ATags2Tags(tags), kind)
                );
                logger.info("Question created with id {}", question.getId());
            } else {
                logger.info("Question already exists with id {}", question.getId());
            }
            return ModelFactory.Question2AQuestion(question);
        } catch (Exception e) {
            logger.error("Failed to append question '{}': {}", text, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ATag appendTag(String name) {
        try {
            logger.info("Appending tag '{}'", name);
            Tag t = api.m_tagRepository.findByName(name);
            if (t == null) {
                t = api.m_tagRepository.save(ModelFactory.makeTag(null, name));
                logger.info("Tag created with id {}", t.getId());
            } else {
                logger.info("Tag already exists with id {}", t.getId());
            }
            return ModelFactory.Tag2ATag(t);
        } catch (Exception e) {
            logger.error("Failed to append tag '{}': {}", name, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ATag findTag(String name) {
        try {
            logger.info("Finding tag '{}'", name);
            return ModelFactory.Tag2ATag(api.m_tagRepository.findByName(name));
        } catch (Exception e) {
            logger.error("Failed to find tag '{}': {}", name, e.getMessage(), e);
            return null;
        }
    }


}
