package ru.bmstu.iu7.src.controllers;

import ru.bmstu.iu7.API.*;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.src.managers.RecManager;

import java.util.*;

public class QuestionnaireController {

    private final IML_port m_ml;
    private AQuestionnaire m_active_questionnaire;
    private final ReqCacheController m_req_cache;
    private final IQuestionnaireRepository m_questRepository;
    private final IReqCacheRepository req_cacheRepository;

    public QuestionnaireController(IML_port imlPort, IQuestionnaireRepository questRepository,
                                   IReqCacheRepository cacheRepository) {
        this.m_questRepository = questRepository;
        this.req_cacheRepository = cacheRepository;
        this.m_ml = imlPort;
        this.m_req_cache = new ReqCacheController();
    }

    public List<AQuestionnaire> get_user_questionnaies(Long id) throws Exception {
        return (List<AQuestionnaire>) m_questRepository.findUserQuestionnaires(id);
    }

    public void delete_questionnaire(AQuestionnaire questionnaire) {
        m_questRepository.deleteQuestionnaire(questionnaire);
    }

    public List<AQuestion> get_all_questions() throws Exception {
        return m_questRepository.get_all_questions();
    }

    public List<AQuestionnaire> get_all_questionnaires() throws Exception {
        return m_questRepository.get_all_questionnaires();
    }

    public void delete_user_questionnaires(Long userId) throws Exception {
        m_questRepository.deleteUserQuestionnaires(userId);
    }

    public void set_active_questionnaire(AQuestionnaire questionnaire) {
        this.m_active_questionnaire = questionnaire;
    }

    public AQuestionnaire get_active_questionnaire() {
        return this.m_active_questionnaire;
    }

    public AQuestion append_question(boolean kind, String text, List<ATag> tags) throws Exception {
        return m_questRepository.appendQuestion(kind, text, tags);
    }

    public ATag append_tag(String name) throws Exception {
        return m_questRepository.appendTag(name);
    }

    public ATag find_tag(String name) throws Exception {
        return m_questRepository.findTag(name);
    }

    public AQuestionnaire create_questionnaire(AUser user, AInformation information,
                                               AInformation search_information) throws Exception {
        if (user == null) {
            throw new Exception("User cannot be null");
        }

        List<AExtendedAnswer> extended_answers = information.getExtendedAnswers();
        for (AExtendedAnswer extendedAnswer : extended_answers) {
            String question = extendedAnswer.getQuestion().getQuestion();
            List<ATag> tags = extendedAnswer.getQuestion().getTags();
            List<ATag> list = m_ml.get_tags_names(
                    question,
                    extendedAnswer.getAnswer(),
                    tags
            );
            extendedAnswer.setTags(list);
        }
        information.setExtendedAnswers(extended_answers);

        List<AExtendedAnswer> extended_answers_s =
                (List<AExtendedAnswer>) search_information.getExtendedAnswers();
        for (AExtendedAnswer extendedAnswer : extended_answers_s) {
            extendedAnswer.setTags(
                    m_ml.get_tags_names(
                            extendedAnswer.getQuestion().getQuestion(),
                            extendedAnswer.getAnswer(),
                            (List<ATag>) extendedAnswer.getQuestion().getTags()
                    )
            );
        }
        search_information.setExtendedAnswers(extended_answers_s);

        AQuestionnaire questionnaire =
                m_questRepository.createQuestionnaire(user, information, search_information);

        m_active_questionnaire = questionnaire;
        return questionnaire;
    }

    public void add_fav(AQuestionnaire questionnaire) throws Exception {
        m_active_questionnaire.addFav(questionnaire);
        req_cacheRepository.deleteOneCache(m_active_questionnaire.getId(), questionnaire.getId());
        m_req_cache.delete_from_cache(questionnaire);
        m_questRepository.update(m_active_questionnaire);
    }

    public void del_fav(AQuestionnaire questionnaire) throws Exception {
        m_active_questionnaire.delFav(questionnaire);
        m_questRepository.update(m_active_questionnaire);
    }

    public void del_black(AQuestionnaire questionnaire) throws Exception {
        m_active_questionnaire.delBlack(questionnaire);
        m_questRepository.update(m_active_questionnaire);
    }

    public void add_black(AQuestionnaire questionnaire) throws Exception {
        m_active_questionnaire.addBlack(questionnaire);
        req_cacheRepository.deleteOneCache(m_active_questionnaire.getId(), questionnaire.getId());
        m_req_cache.delete_from_cache(questionnaire);
        m_questRepository.update(m_active_questionnaire);
    }

    public boolean is_in_black(AQuestionnaire questionnaire) {
        for (AQuestionnaire quest : questionnaire.getBlackList()) {
            if (Objects.equals(quest.getId(), questionnaire.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean is_in_fav(AQuestionnaire questionnaire) {
        for (AQuestionnaire quest : questionnaire.getFavList()) {
            if (Objects.equals(quest.getId(), questionnaire.getId())) {
                return true;
            }
        }
        return false;
    }

    public void delete(AQuestionnaire questionnaire) throws Exception {
        m_questRepository.delete(questionnaire);
    }

    public void censor(AQuestionnaire questionnaire) throws Exception {
        questionnaire.setCensored(true);
        m_questRepository.update(questionnaire);
    }

    public List<? extends AQuestionnaire> get_questionnairies(int no, int size, Long user_id) throws Exception {
        return m_questRepository.get_page(no, size, user_id);
    }

    public void add_in_cache_list(double harmonic_average_norm, AQuestionnaire quest) throws Exception {
        req_cacheRepository.insert(harmonic_average_norm, m_active_questionnaire, quest);
    }

    public void clear_req_cache() throws Exception {
        req_cacheRepository.ClearAll();
        m_req_cache.clearAll();
    }

    public void clear_req_cache_active_quest() throws Exception {
        req_cacheRepository.delete(m_active_questionnaire);
    }

    public void update_req_cache() throws Exception {
        List<AReqCache> req_cache = new ArrayList<>(req_cacheRepository.findAll(m_active_questionnaire));
        List<AQuestionnaire> req_questionnaires = new ArrayList<>();
        Collections.sort(req_cache, new Comparator<AReqCache>() {
            @Override
            public int compare(AReqCache o1, AReqCache o2) {
                double f1 = o1.getFactor();
                double f2 = o2.getFactor();
                return -Double.compare(f1, f2);
            }
        });

        int cnt = 0;
        for (var req : req_cache) {
            if (cnt++ > 500)
                break;
            req_questionnaires.add(m_questRepository.findQuestionnaire(req.getQuestionnaire2().getId()));
        }

        m_req_cache.setM_req_cache(req_questionnaires);
    }

    public List<AQuestionnaire> get_quest_in_cache() throws Exception {
        return m_req_cache.get_req_cache();
    }

    public AQuestionnaire findQuestionnaire(Long id) throws Exception {
        return m_questRepository.findQuestionnaire(id);
    }
}
