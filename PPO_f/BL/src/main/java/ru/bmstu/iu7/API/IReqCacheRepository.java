package ru.bmstu.iu7.API;

import ru.bmstu.iu7.API.model.AQuestionnaire;
import ru.bmstu.iu7.API.model.AReqCache;

import java.util.List;

public interface IReqCacheRepository {
    AReqCache insert(double Factor,
                            AQuestionnaire questionnaire1, AQuestionnaire questionnaire2)  throws Exception ;
    void delete(AQuestionnaire questionnaire) throws Exception;

    List<AReqCache> findAll(AQuestionnaire questionnaire) throws Exception;
    void ClearAll();
    void deleteOneCache(Long id_active_quest, Long id_quest);
}
