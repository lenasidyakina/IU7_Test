package ru.bmstu.iu7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.bmstu.iu7.API.model.AQuestionnaire;
import ru.bmstu.iu7.API.model.AReqCache;
import ru.bmstu.iu7.impl.SpringReqCacheRepository;
import ru.bmstu.iu7.impl.model.ReqCache;
import ru.bmstu.iu7.testdata.QuestionnaireMother;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Random.class)
class ReqCacheRepositoryTest {

    @Mock
    SpringReqCacheRepository m_springReqCacheRepository;

    @InjectMocks
    ReqCacheRepository reqCacheRepository;

    AQuestionnaire q1;
    AQuestionnaire q2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        q1 = QuestionnaireMother.lenaQuestionnaire();
        q2 = QuestionnaireMother.ivanQuestionnaire();
    }

    @Test
    void insert_success() {
        Mockito.when(m_springReqCacheRepository
                        .findByexistsByQuestionnaire1_Id_AndQuestionnaire2_Id(q1.getId(), q2.getId()))
                .thenReturn(Collections.emptyList());

        double factor = 0.75;
        AReqCache result = reqCacheRepository.insert(factor, q1, q2);

        assertNotNull(result);
        assertEquals(factor, result.getFactor());
        assertEquals(q1.getId(), result.getQuestionnaire1().getId());
        assertEquals(q2.getId(), result.getQuestionnaire2().getId());

        Mockito.verify(m_springReqCacheRepository)
                .insertCachePair(q1.getId(), q2.getId(), factor);
    }

    @Test
    void insert_success_whenCacheDoesNotExist() {
        double factor = 0.75;
        Mockito.when(m_springReqCacheRepository
                        .findByexistsByQuestionnaire1_Id_AndQuestionnaire2_Id(q1.getId(), q2.getId()))
                .thenReturn(Collections.emptyList());

        reqCacheRepository.insert(factor, q1, q2);

        Mockito.verify(m_springReqCacheRepository)
                .insertCachePair(q1.getId(), q2.getId(), factor);
    }


    @Test
    void insert_alreadyExists_returnsNull() {
        Mockito.when(m_springReqCacheRepository
                        .findByexistsByQuestionnaire1_Id_AndQuestionnaire2_Id(q1.getId(), q2.getId()))
                .thenReturn(List.of(new ReqCache()));

        AReqCache result = reqCacheRepository.insert(0.5, q1, q2);

        assertNull(result);
    }

    @Test
    void insert_failure_returnsNull() {
        Mockito.when(m_springReqCacheRepository
                        .findByexistsByQuestionnaire1_Id_AndQuestionnaire2_Id(q1.getId(), q2.getId()))
                .thenThrow(new RuntimeException("DB error"));


        assertThrows(RuntimeException.class,
                () -> reqCacheRepository.insert(0.5, q1, q2));
    }


    @Test
    void delete_success() {
        reqCacheRepository.delete(q1);

        Mockito.verify(m_springReqCacheRepository).deleteQuest1(q1.getId());
    }

    @Test
    void delete_failure_doesNotThrow() {
        Mockito.doThrow(new RuntimeException("DB error"))
                .when(m_springReqCacheRepository).deleteQuest1(q1.getId());

        assertThrows(RuntimeException.class,
                () -> reqCacheRepository.delete(q1));
    }


    @Test
    void findAll_success() {
        Mockito.when(m_springReqCacheRepository.findAllByQuestionnaire1_Id(q1.getId()))
                .thenReturn(List.of(new ReqCache()));

        List<AReqCache> result = reqCacheRepository.findAll(q1);

        assertFalse(result.isEmpty());
    }

    @Test
    void findAll_failure_returnsEmptyList() {
        Mockito.when(m_springReqCacheRepository.findAllByQuestionnaire1_Id(q1.getId()))
                .thenThrow(new RuntimeException("DB error"));

        List<AReqCache> result = reqCacheRepository.findAll(q1);

        assertTrue(result.isEmpty());
    }

    @Test
    void clearAll_success() {
        reqCacheRepository.ClearAll();

        Mockito.verify(m_springReqCacheRepository).deleteAll();
    }

    @Test
    void clearAll_failure_doesNotThrow() {
        Mockito.doThrow(new RuntimeException("DB error"))
                .when(m_springReqCacheRepository).deleteAll();

        assertThrows(RuntimeException.class,
                () -> reqCacheRepository.ClearAll());
    }

    @Test
    void deleteOneCache_success() {
        reqCacheRepository.deleteOneCache(0L, 1L);

        Mockito.verify(m_springReqCacheRepository)
                .deleteByQuestionnaire1_IdAndQuestionnaire2_Id(0L, 1L);
    }

    @Test
    void deleteOneCache_failure_doesNotThrow() {
        Mockito.doThrow(new RuntimeException("DB error"))
                .when(m_springReqCacheRepository)
                .deleteByQuestionnaire1_IdAndQuestionnaire2_Id(0L, 1L);

        assertThrows(RuntimeException.class,
                () -> reqCacheRepository.deleteOneCache(0L, 1L));
    }
}
