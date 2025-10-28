package ru.bmstu.iu7.src.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ru.bmstu.iu7.API.model.AQuestionnaire;
import ru.bmstu.iu7.src.testdata.QuestionnaireMother;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Random.class)
class ReqCacheControllerTest {

    private ReqCacheController controller;

    private List<AQuestionnaire> sampleList;
    private AQuestionnaire lenaQuestionnaire;
    private AQuestionnaire iraQuestionnaire;

    @BeforeEach
    void setUp() {
        controller = new ReqCacheController();
        ReqCacheController.m_list = new ArrayList<>();
        ReqCacheController.running = true;

        lenaQuestionnaire = QuestionnaireMother.lenaQuestionnaire();
        iraQuestionnaire = QuestionnaireMother.iraQuestionnaire();
        sampleList = List.of(lenaQuestionnaire, iraQuestionnaire);
    }

    @Test
    void setM_req_cache_positive() {
        controller.setM_req_cache(sampleList);
        List<AQuestionnaire> cached = controller.get_req_cache();

        assertEquals(2, cached.size());
        assertEquals("Lena", cached.get(0).getUser().getName());
        assertEquals("Ira", cached.get(1).getUser().getName());
    }

    @Test
    void setM_req_cache_nullList_negative() {
        controller.setM_req_cache(null);

        assertThrows(NullPointerException.class, () -> controller.get_req_cache().size());
    }

    @Test
    void delete_from_cache_positive() {
        ReqCacheController.m_list.add(lenaQuestionnaire);

        controller.delete_from_cache(lenaQuestionnaire);

        assertTrue(ReqCacheController.m_list.isEmpty());
    }

    @Test
    void delete_from_cache_notPresent_negative() {
        controller.delete_from_cache(iraQuestionnaire);

        assertTrue(ReqCacheController.m_list.isEmpty());
    }

    @Test
    void delete_from_cache_null_doesNothing() {
        ReqCacheController.m_list.add(lenaQuestionnaire);

        controller.delete_from_cache(null);

        assertEquals(1, ReqCacheController.m_list.size());
        assertTrue(ReqCacheController.m_list.contains(lenaQuestionnaire));
    }

    @Test
    void clearAll_positive() {
        ReqCacheController.m_list.add(lenaQuestionnaire);
        ReqCacheController.running = true;

        controller.clearAll();

        assertTrue(ReqCacheController.m_list.isEmpty());
        assertFalse(ReqCacheController.running);
    }

    @Test
    void clearAll_emptyList_negative() {
        ReqCacheController.m_list.clear();
        ReqCacheController.running = true;

        controller.clearAll();

        assertTrue(ReqCacheController.m_list.isEmpty());
        assertFalse(ReqCacheController.running);
    }

    @Test
    void get_req_cache_positive() {
        controller.setM_req_cache(sampleList);

        List<AQuestionnaire> cached = controller.get_req_cache();

        assertNotNull(cached);
        assertEquals(2, cached.size());
        assertEquals("Lena", cached.get(0).getUser().getName());
        assertEquals("Ira", cached.get(1).getUser().getName());
    }

    @Test
    void get_req_cache_nullList_negative() {
        controller.setM_req_cache(null);

        assertNull(controller.get_req_cache());
    }

}
