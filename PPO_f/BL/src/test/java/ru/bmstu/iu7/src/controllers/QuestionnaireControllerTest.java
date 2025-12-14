package ru.bmstu.iu7.src.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ru.bmstu.iu7.API.*;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.src.testdata.QuestionnaireMother;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.Random.class)
class QuestionnaireControllerTest {


    private QuestionnaireController controller;
    private FakeQuestionnaireRepository fakeRepo;
    private FakeReqCacheRepository fakeCacheRepo;
    private FakeMLPort fakeML;

    @BeforeEach
    void setUp() {
        fakeRepo = new FakeQuestionnaireRepository();
        fakeCacheRepo = new FakeReqCacheRepository();
        fakeML = new FakeMLPort();
        controller = new QuestionnaireController(fakeML, fakeRepo, fakeCacheRepo);
    }

    @Test
    void setAndGetActiveQuestionnaire_positive() {
        AQuestionnaire q = QuestionnaireMother.lenaQuestionnaire();

        controller.set_active_questionnaire(q);

        assertEquals(q, controller.get_active_questionnaire());
    }

    @Test
    void getActiveQuestionnaire_noActive_negative() {
        assertNull(controller.get_active_questionnaire());
    }

    @Test
    void appendQuestion_positive() throws Exception {
        AQuestion q = controller.append_question(true, "Question", List.of(new ATag(1L, "tag")));

        assertNotNull(q);
        assertEquals("Question", q.getQuestion());
    }

    @Test
    void appendQuestion_exception_negative() {
        fakeRepo.setThrowOnAppend(true);

        assertThrows(RuntimeException.class, () -> controller.append_question(true, "q", List.of()));
    }

    @Test
    void appendTag_positive() throws Exception {
        ATag tag = controller.append_tag("TagName");

        assertNotNull(tag);
        assertEquals("TagName", tag.getName());
    }

    @Test
    void appendTag_exception_negative() {
        fakeRepo.setThrowOnAppend(true);

        assertThrows(Exception.class, () -> controller.append_tag("TagName"));
    }


    @Test
    void createQuestionnaire_positive() throws Exception {
        AUser user = new AUser(0L, "User", "pass");
        AInformation info = QuestionnaireMother.infoWalking();
        AInformation search = QuestionnaireMother.infoSleeping();

        AQuestionnaire q = controller.create_questionnaire(user, info, search);

        assertNotNull(q);
        assertEquals(user, q.getUser());
        assertEquals(q, controller.get_active_questionnaire());
    }

    @Test
    void createQuestionnaire_nullUser_negative() {
        AInformation info = QuestionnaireMother.infoWalking();
        AInformation search = QuestionnaireMother.infoSleeping();

        assertThrows(Exception.class, () -> controller.create_questionnaire(null, info, search));
    }

    @Test
    void deleteQuestionnaire_positive() {
        AQuestionnaire q = QuestionnaireMother.lenaQuestionnaire();

        controller.delete_questionnaire(q);

        assertTrue(fakeRepo.deleted.contains(q));
    }

    @Test
    void deleteQuestionnaire_exception_negative() {
        fakeRepo.setThrowOnDelete(true);
        AQuestionnaire q = QuestionnaireMother.lenaQuestionnaire();

        assertThrows(RuntimeException.class, () -> controller.delete_questionnaire(q));
    }


    @Test
    void delFav_negative() throws Exception {
        AQuestionnaire q1 = QuestionnaireMother.lenaQuestionnaire();
        AQuestionnaire q2 = QuestionnaireMother.iraQuestionnaire();
        controller.set_active_questionnaire(q1);

        controller.del_fav(q2);

        assertFalse(q1.getFavList().contains(q2));
        assertTrue(fakeRepo.updated.contains(q1));
    }

    @Test
    void addFavAndCheck_positive() throws Exception {
        AQuestionnaire q1 = QuestionnaireMother.lenaQuestionnaire();
        AQuestionnaire q2 = QuestionnaireMother.iraQuestionnaire();
        q1.setId(1L);
        q2.setId(2L);
        controller.set_active_questionnaire(q1);

        controller.add_fav(q2);

        assertTrue(q1.getFavList().stream().anyMatch(q -> Objects.equals(q.getId(), q2.getId())));
        assertTrue(fakeCacheRepo.deleted.stream().anyMatch(q -> Objects.equals(q.getId(), q2.getId())));
        assertTrue(fakeRepo.updated.stream().anyMatch(q -> Objects.equals(q.getId(), q1.getId())));
    }

    @Test
    void addBlack_positive() throws Exception {
        AQuestionnaire q1 = QuestionnaireMother.lenaQuestionnaire();
        AQuestionnaire q2 = QuestionnaireMother.iraQuestionnaire();
        q1.setId(1L);
        q2.setId(2L);
        controller.set_active_questionnaire(q1);

        controller.add_black(q2);

        assertTrue(q1.getBlackList().stream().anyMatch(q -> Objects.equals(q.getId(), q2.getId())));
        assertTrue(fakeCacheRepo.deleted.stream().anyMatch(q -> Objects.equals(q.getId(), q2.getId())));
        assertTrue(fakeRepo.updated.stream().anyMatch(q -> Objects.equals(q.getId(), q1.getId())));
    }


    @Test
    void delBlack_negative() throws Exception {
        AQuestionnaire q1 = QuestionnaireMother.lenaQuestionnaire();
        AQuestionnaire q2 = QuestionnaireMother.iraQuestionnaire();
        controller.set_active_questionnaire(q1);

        controller.del_black(q2);

        assertFalse(q1.getBlackList().contains(q2));
        assertTrue(fakeRepo.updated.contains(q1));
    }

    @Test
    void censor_positive() throws Exception {
        AQuestionnaire q = QuestionnaireMother.lenaQuestionnaire();

        controller.censor(q);

        assertTrue(q.isCensored());
        assertTrue(fakeRepo.updated.contains(q));
    }

    @Test
    void censor_exception_negative() {
        fakeRepo.setThrowOnUpdate(true);
        AQuestionnaire q = QuestionnaireMother.lenaQuestionnaire();

        assertThrows(RuntimeException.class, () -> controller.censor(q));
    }

    @Test
    void addInCache_positive() throws Exception {
        AQuestionnaire active = QuestionnaireMother.lenaQuestionnaire();
        AQuestionnaire toAdd = QuestionnaireMother.iraQuestionnaire();
        controller.set_active_questionnaire(active);

        controller.add_in_cache_list(0.5, toAdd);

        assertTrue(fakeCacheRepo.insertCalled);
    }

    @Test
    void clearReqCache_positive() throws Exception {
        controller.clear_req_cache();

        assertTrue(fakeCacheRepo.clearAllCalled);
    }

    @Test
    void getUserQuestionnaires_positive() throws Exception {
        List<AQuestionnaire> list = controller.get_user_questionnaies(0L);

        assertEquals(fakeRepo.userQuestions.size(), list.size());
    }

    @Test
    void getUserQuestionnaires_exception_negative() {
        fakeRepo = new FakeQuestionnaireRepository();
        fakeRepo.setThrowOnFind(true);
        controller = new QuestionnaireController(fakeML, fakeRepo, fakeCacheRepo);

        assertThrows(Exception.class, () -> controller.get_user_questionnaies(0L));
    }

    @Test
    void getAllQuestions_positive() throws Exception {
        List<AQuestion> list = controller.get_all_questions();

        assertEquals(fakeRepo.questions.size(), list.size());
    }

    @Test
    void getAllQuestions_exception_negative() {
        fakeRepo.setThrowOnFind(true);

        assertThrows(Exception.class, () -> controller.get_all_questions());
    }

    @Test
    void findQuestionnaire_positive() throws Exception {
        AQuestionnaire q = controller.findQuestionnaire(0L);

        assertNotNull(q);
    }

    @Test
    void findQuestionnaire_exception_negative() {
        fakeRepo.setThrowOnFind(true);

        assertThrows(RuntimeException.class, () -> controller.findQuestionnaire(0L));
    }

    static class FakeQuestionnaireRepository implements IQuestionnaireRepository {
        boolean throwOnAppend = false, throwOnDelete = false, throwOnUpdate = false, throwOnFind = false;
        List<AQuestionnaire> deleted = new ArrayList<>();
        List<AQuestionnaire> updated = new ArrayList<>();
        List<AQuestion> questions = List.of(new AQuestion(), new AQuestion());
        List<AQuestionnaire> userQuestions = List.of(QuestionnaireMother.lenaQuestionnaire());

        void setThrowOnAppend(boolean b) { throwOnAppend = b; }
        void setThrowOnDelete(boolean b) { throwOnDelete = b; }
        void setThrowOnUpdate(boolean b) { throwOnUpdate = b; }
        void setThrowOnFind(boolean b) { throwOnFind = b; }

        @Override
        public AQuestion appendQuestion(boolean kind, String text, List<ATag> tags) {
            if (throwOnAppend) {
                throw new RuntimeException("error");
            }
            return new AQuestion(text, tags, kind);
        }


        @Override
        public ATag appendTag(String name) {
            if (throwOnAppend) {
                throw new RuntimeException("error");
            }
            return new ATag(0L, name);
        }

        @Override
        public ATag findTag(String name) { return new ATag(0L, name); }

        @Override
        public List<AQuestion> get_all_questions() {
            if (throwOnFind) {
                throw new RuntimeException("error");
            }
            return questions;
        }


        @Override
        public List<AQuestionnaire> get_all_questionnaires() { return userQuestions; }

        @Override
        public List<AQuestionnaire> findUserQuestionnaires(Long id) {
            if (throwOnFind) {
                throw new RuntimeException("error");
            }
            return userQuestions;
        }



        @Override
        public void delete(AQuestionnaire questionnaire) { if (throwOnDelete) deleted.add(questionnaire); }
        @Override
        public void deleteQuestionnaire(AQuestionnaire questionnaire) {
            if (throwOnDelete) {
                throw new RuntimeException("error");
            }
            deleted.add(questionnaire);
        }

        @Override
        public void deleteUserQuestionnaires(Long userId) { deleted.addAll(userQuestions); }
        @Override
        public AQuestionnaire createQuestionnaire(AUser user, AInformation info, AInformation search) {
            AQuestionnaire q = QuestionnaireMother.lenaQuestionnaire();
            q.setUser(user);
            return q;
        }

        @Override
        public AQuestionnaire update(AQuestionnaire questionnaire) {
            if (throwOnUpdate) {
                throw new RuntimeException("error");
            }
            updated.add(questionnaire);
            return questionnaire;
        }

        @Override
        public AQuestionnaire findQuestionnaire(Long id) {
            if (throwOnFind) {
                throw new RuntimeException("error");
            }
            return QuestionnaireMother.lenaQuestionnaire();
        }

        @Override
        public List<AQuestionnaire> get_page(int no, int size, Long user_id)
        { return List.of(QuestionnaireMother.lenaQuestionnaire()); }
    }

    static class FakeReqCacheRepository implements IReqCacheRepository {
        boolean insertCalled = false;
        boolean clearAllCalled = false;
        List<AQuestionnaire> deleted = new ArrayList<>();

        @Override
        public AReqCache insert(double harmonic_average_norm, AQuestionnaire active, AQuestionnaire quest) {
            insertCalled = true;
            return null;
        }

        @Override
        public void delete(AQuestionnaire active) {
            deleted.add(active);
        }

        @Override
        public void deleteOneCache(Long id1, Long id2) {
            AQuestionnaire q = new AQuestionnaire();
            q.setId(id2);
            deleted.add(q);
        }

        @Override
        public List<AReqCache> findAll(AQuestionnaire active) {
            return List.of();
        }

        @Override
        public void ClearAll() {
            clearAllCalled = true;
        }

    }

    static class FakeMLPort implements IML_port {
        @Override
        public List<ATag> get_tags_names(String question, String answer, List<ATag> tags) { return tags; }
    }
}
