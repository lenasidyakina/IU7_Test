package ru.bmstu.iu7.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.iu7.impl.model.Questionnaire;
import ru.bmstu.iu7.impl.model.ReqCache;

import java.util.List;

@Repository
public interface SpringReqCacheRepository extends JpaRepository<ReqCache, Long>{
    @Transactional
    void deleteAllByQuestionnaire1(Questionnaire questionnaire);

    @Transactional
    @Modifying
    @Query("DELETE FROM ReqCache WHERE questionnaire1.id = ?1")
    void deleteQuest1(Long id);

    @Query("SELECT u FROM ReqCache u WHERE u.questionnaire1.id = ?1")
    List<ReqCache> findAllByQuestionnaire1_Id(Long questionnaire1Id);

    @Query("SELECT u FROM ReqCache u WHERE u.questionnaire1.id = ?1 AND u.questionnaire2.id = ?2")
    List<ReqCache> findByexistsByQuestionnaire1_Id_AndQuestionnaire2_Id(
            Long questionnaire1Id, Long questionnaire2Id);


    @Transactional
    void deleteByQuestionnaire1_IdAndQuestionnaire2_Id(
            Long questionnaire1Id, Long questionnaire2Id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO req_cache ( questionnaire1_id, questionnaire2_id, factor) VALUES (:questionnaire1Id, :questionnaire2Id, :Factor)",
    nativeQuery = true)
    void insertCachePair(@Param("questionnaire1Id") Long questionnaire1Id,
                         @Param("questionnaire2Id") Long questionnaire2Id,
                         @Param("Factor") double Factor);
}
