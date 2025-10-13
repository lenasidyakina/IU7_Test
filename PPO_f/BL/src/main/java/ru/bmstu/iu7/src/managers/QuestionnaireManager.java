package ru.bmstu.iu7.src.managers;

import ru.bmstu.iu7.API.AppLogger;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.src.controllers.QuestionnaireController;

import java.util.List;

public class QuestionnaireManager {
    private final QuestionnaireController m_questionnaireController;
    private final AppLogger logger;

    public QuestionnaireManager(QuestionnaireController controller, AppLogger applogger) {
        this.m_questionnaireController = controller;
        this.logger = applogger;
        if (logger != null) logger.info("QuestionnaireManager initialized");
    }

    public QuestionnaireManager(QuestionnaireController controller) {
        this(controller, null);
    }

    public void set_active_questionnaire(AQuestionnaire quest) {
        try {
            m_questionnaireController.set_active_questionnaire(quest);
            if (logger != null) {
                if (quest != null) {
                    logger.info("Set active questionnaire with id {}", quest.getId());
                } else {
                    logger.info("Set active questionnaire to null");
                }
            }
        } catch (Exception e) {
            if (logger != null) {
                String id = quest != null ? String.valueOf(quest.getId()) : "null";
                logger.error("Failed to set active questionnaire {}: {}", id, e.getMessage(), e);
            }
        }
    }


    public AQuestionnaire create(AUser user, AInformation information, AInformation search_information) {
        try {
            if (user == null || information == null || search_information == null) {
                return null;
            }
            if (logger != null) logger.info("Creating questionnaire for user {}", user.getName());
            AQuestionnaire quest = m_questionnaireController.create_questionnaire(user, information, search_information);
            if (logger != null) logger.info("Questionnaire created with id {}", quest.getId());
            return quest;
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to create questionnaire for user {}: {}", user.getName(), e.getMessage(), e);
            return null;
        }
    }

    public void add_fav(AQuestionnaire questionnaire) {
        try {
            m_questionnaireController.add_fav(questionnaire);
            if (logger != null) logger.info("Added questionnaire {} to favorites", questionnaire.getId());
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to add questionnaire {} to favorites: {}", questionnaire.getId(), e.getMessage(), e);
        }
    }

    public void del_fav(AQuestionnaire questionnaire) {
        try {
            m_questionnaireController.del_fav(questionnaire);
            if (logger != null) logger.info("Removed questionnaire {} from favorites", questionnaire.getId());
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to remove questionnaire {} from favorites: {}", questionnaire.getId(), e.getMessage(), e);
        }
    }

    public void add_black(AQuestionnaire questionnaire) {
        try {
            m_questionnaireController.add_black(questionnaire);
            if (logger != null) logger.info("Added questionnaire {} to blacklist", questionnaire.getId());
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to add questionnaire {} to blacklist: {}", questionnaire.getId(), e.getMessage(), e);
        }
    }

    public void del_black(AQuestionnaire questionnaire) {
        try {
            m_questionnaireController.del_black(questionnaire);
            if (logger != null) logger.info("Removed questionnaire {} from blacklist", questionnaire.getId());
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to remove questionnaire {} from blacklist: {}", questionnaire.getId(), e.getMessage(), e);
        }
    }

    public void delete(AQuestionnaire questionnaire) {
        try {
            m_questionnaireController.delete(questionnaire);
            if (logger != null) logger.info("Deleted questionnaire {}", questionnaire.getId());
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to delete questionnaire {}: {}", questionnaire.getId(), e.getMessage(), e);
        }
    }

    public void censor(AQuestionnaire questionnaire) {
        try {
            m_questionnaireController.censor(questionnaire);
            if (logger != null) logger.info("Censored questionnaire {}", questionnaire.getId());
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to censor questionnaire {}: {}", questionnaire.getId(), e.getMessage(), e);
        }
    }

    public List<AQuestion> get_all_questions() {
        try {
            if (logger != null) logger.info("Fetching all questions");
            return m_questionnaireController.get_all_questions();
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to fetch all questions: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<AQuestionnaire> get_all_questionnaires() {
        try {
            if (logger != null) logger.info("Fetching all questionnaires");
            return m_questionnaireController.get_all_questionnaires();
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to fetch all questionnaires: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public void delete_user_questionnaires(Long userId) {
        try {
            m_questionnaireController.delete_user_questionnaires(userId);
            if (logger != null) logger.info("Deleted all questionnaires for user with id {}", userId);
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to delete questionnaires for user {}: {}", userId, e.getMessage(), e);
        }
    }

    public void delete_questionnaire(AQuestionnaire questionnaire) {
        try {
            m_questionnaireController.delete_questionnaire(questionnaire);
            if (logger != null) logger.info("Deleted questionnaire {}", questionnaire.getId());
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to delete questionnaire {}: {}", questionnaire.getId(), e.getMessage(), e);
        }
    }

    public List<AQuestionnaire> get_user_questionnaies(Long id) {
        try {
            if (logger != null) logger.info("Fetching questionnaires for user with id {}", id);
            return m_questionnaireController.get_user_questionnaies(id);
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to fetch questionnaires for user {}: {}", id, e.getMessage(), e);
            return List.of();
        }
    }

    public AQuestion append_question(boolean kind, String text, List<ATag> tags) {
        try {
            if (logger != null) logger.info("Appending question: '{}' with {} tags", text, tags.size());
            return m_questionnaireController.append_question(kind, text, tags);
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to append question '{}': {}", text, e.getMessage(), e);
            return null;
        }
    }

    public ATag append_tag(String name) {
        try {
            if (logger != null) logger.info("Appending tag '{}'", name);
            return m_questionnaireController.append_tag(name);
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to append tag '{}': {}", name, e.getMessage(), e);
            return null;
        }
    }

    public ATag find_tag(String name) {
        try {
            if (logger != null) logger.info("Finding tag '{}'", name);
            return m_questionnaireController.find_tag(name);
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to find tag '{}': {}", name, e.getMessage(), e);
            return null;
        }
    }

    public void clear_req_cache() {
        try {
            m_questionnaireController.clear_req_cache();
            if (logger != null) logger.info("Cleared request cache");
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to clear request cache: {}", e.getMessage(), e);
        }
    }


    public AQuestionnaire findQuestionnaire(Long id) {
        try {
            if (logger != null) logger.info("Finding questionnaire with id {}", id);
            AQuestionnaire quest =  m_questionnaireController.findQuestionnaire(id);
            if (quest == null) throw new Exception();
            return quest;
        } catch (Exception e) {
            if (logger != null) logger.error("Failed to find questionnaire {}: {}", id, e.getMessage(), e);
            return null;
        }
    }
}
