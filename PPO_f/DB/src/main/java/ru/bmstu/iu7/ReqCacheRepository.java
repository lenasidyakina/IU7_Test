package ru.bmstu.iu7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bmstu.iu7.API.IReqCacheRepository;
import ru.bmstu.iu7.API.model.AQuestionnaire;
import ru.bmstu.iu7.API.model.AReqCache;
import ru.bmstu.iu7.impl.ModelFactory;
import ru.bmstu.iu7.impl.SpringReqCacheRepository;
import ru.bmstu.iu7.impl.model.ReqCache;

import java.util.List;

public class ReqCacheRepository implements IReqCacheRepository {

    private final SpringReqCacheRepository m_springReqCacheRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReqCacheRepository.class);

    public ReqCacheRepository(SpringReqCacheRepository springReqCacheRepository) {
        this.m_springReqCacheRepository = springReqCacheRepository;
        logger.info("ReqCacheRepository initialized");
    }

    static int n = 0;

    @Override
    public AReqCache insert(double Factor, AQuestionnaire questionnaire1, AQuestionnaire questionnaire2) {
        try {
            logger.info("Inserting cache entry for questionnaires {} and {}",
                    questionnaire1.getId(), questionnaire2.getId());
            ReqCache rc = new ReqCache();
            rc.setFactor(Factor);
            rc.setQuestionnaire1(ModelFactory.AQuestionnaire2Questionnaire(questionnaire1));
            rc.setQuestionnaire2(ModelFactory.AQuestionnaire2Questionnaire(questionnaire2));

            List<ReqCache> rq = m_springReqCacheRepository.findByexistsByQuestionnaire1_Id_AndQuestionnaire2_Id(
                    questionnaire1.getId(), questionnaire2.getId());

            if (rq.isEmpty()) {
               m_springReqCacheRepository.insertCachePair(questionnaire1.getId(), questionnaire2.getId(), rc.getFactor());
               logger.info("insert record " + (n++));
                return ModelFactory.ReqCache2AReqCache(rc);
            } else {
                logger.info("Cache entry already exists for questionnaires {} and {}",
                        questionnaire1.getId(), questionnaire2.getId());
            }
        } catch (Exception e) {
            logger.error("Failed to insert cache entry for questionnaires {} and {}: {}",
                    questionnaire1.getId(), questionnaire2.getId(), e.getMessage(), e);
            throw e;
        }
        return null;
    }

    @Override
    public void delete(AQuestionnaire questionnaire) {
        try {
            Long id = questionnaire.getId();
            logger.info("Deleting cache entries for questionnaire {}", questionnaire.getId());
            m_springReqCacheRepository.deleteQuest1(id);
        } catch (Exception e) {
            logger.error("Failed to delete cache entries for questionnaire {}: {}",
                    questionnaire.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<AReqCache> findAll(AQuestionnaire questionnaire) {
        try {
            logger.info("Fetching all cache entries for questionnaire {}", questionnaire.getId());
            return ModelFactory.ReqCaches2AReqCaches(
                    m_springReqCacheRepository.findAllByQuestionnaire1_Id(
                            questionnaire.getId()));
        } catch (Exception e) {
            logger.error("Failed to fetch cache entries for questionnaire {}: {}",
                    questionnaire.getId(), e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public void ClearAll() {
        try {
            logger.info("Clearing all cache entries");
            m_springReqCacheRepository.deleteAll();
        } catch (Exception e) {
            logger.error("Failed to clear all cache entries: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteOneCache(Long id_active_quest, Long id_quest) {
        try {
            logger.info("Clearing all cache entries");
            m_springReqCacheRepository.deleteByQuestionnaire1_IdAndQuestionnaire2_Id(id_active_quest, id_quest);
        } catch (Exception e) {
            logger.error("Failed to clear all cache entries: {}", e.getMessage(), e);
            throw e;
        }
    }
}
