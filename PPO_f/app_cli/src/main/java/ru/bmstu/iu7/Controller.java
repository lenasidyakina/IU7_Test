
/*
package ru.bmstu.iu7;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.src.controllers.ReqCacheController;

import java.util.*;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins="http://localhost:3000")
public class Controller {

    @GetMapping("/users")
    public List<AUser> getUsers() {
        List<AUser> users = LabService.userRepository.findAll();
        return users;
    }

    @GetMapping("/questions")
    public List<AQuestion> getQuestions() throws Exception {
        List<AQuestion> qs = LabService.mainManager.getM_que_manager().get_all_questions();
        return qs;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody Map<String, Object> crs) {
        try {
            String name = (String) crs.get("username");
            String password = (String) crs.get("password");
            int age = (int) crs.get("age");
            boolean gender = (boolean) crs.get("gender"); // true = male, false = female

            AUser user = LabService.mainManager.getM_user_manager().register(name, password, age, gender);

            if (user == null) {
                throw new Exception("Registration failed");
            }

            LabService.currentUser = user;
            return new ResponseEntity<Object>(user, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> map = new HashMap<>();
            map.put("error", "registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }



    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody Map<String, String> crs) {
        try {
            String name = crs.get("name");
            String password = crs.get("password");
            AUser user = LabService.mainManager.getM_user_manager().authorize(name, password);
            if ((user == null) || !password.equals(user.getPassword()))
                throw new Exception();
            LabService.currentUser = user;
            return new ResponseEntity<Object>(user, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/user/delete")
    public ResponseEntity<Object> deleteUser(long id) {
        try {
            LabService.mainManager.getM_user_manager().delete(id);
        }
        catch (Exception e) {

        }
        return ResponseEntity.ok("");
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logoutUser() {
        LabService.currentUser = null;
        return ResponseEntity.ok("");
    }

    @PostMapping("/deleteuserquests")
    public ResponseEntity<Object> deleteAllUserQuests(long userId) {
        try {
            LabService.mainManager.getM_que_manager().delete_user_questionnaires(userId);
        }
        catch (Exception e) {

        }
        return ResponseEntity.ok("");
    }

    @PostMapping("/createquest")
    public ResponseEntity<Object> createQuest(long userId) {
        try {
            LabService.mainManager.getM_que_manager().delete_user_questionnaires(userId);
        }
        catch (Exception e) {

        }
        return ResponseEntity.ok("");
    }

    @GetMapping("/userquests")
    public List<AQuestionnaire> getUserQuests() throws Exception {
        if (LabService.currentUser == null)
            return new ArrayList<>();
        List<AQuestionnaire> list = LabService.mainManager.getM_que_manager().
                get_user_questionnaies(LabService.currentUser.getId());
        return list;
    }

    @GetMapping("/getfriends/{id}")
    public List<AQuestionnaire> getFriends(@PathVariable Long id) throws Exception {
        if (LabService.currentUser == null)
            return new ArrayList<>();
        AQuestionnaire activeQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
        LabService.mainManager.getM_que_manager().set_active_questionnaire(activeQuest);
        List<AQuestionnaire> qs = LabService.mainManager.getM_rec_manager().get_friends();
        List<AQuestionnaire> black_list = activeQuest.getBlackList();
        List<AQuestionnaire> fav_list = activeQuest.getFavList();
        Iterator<AQuestionnaire> it = qs.iterator();
        while (it.hasNext()) {
            AQuestionnaire q = it.next();
            if (black_list.contains(q) || fav_list.contains(q)) {
                it.remove();
            }
        }
        return qs;
    }

    @PostMapping("/submitquest")
    public ResponseEntity<Object> submitQuest(@RequestBody ArrayList<ArrayList<String>> data) throws Exception {
        if (LabService.currentUser == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<AQuestion> qs = LabService.mainManager.getM_que_manager().get_all_questions();

        List<AVariantAnswer> variantAnswers = new ArrayList<>();
        List<AExtendedAnswer> extendedAnswers = new ArrayList<>();
        List<AVariantAnswer> otherVariantAnswers = new ArrayList<>();
        List<AExtendedAnswer> otherExtendedAnswers = new ArrayList<>();

        int cnt = 0;
        for (AQuestion q : qs) {
            ArrayList<String> v = data.get(cnt++);
            if (v.get(0).equals("EXT")) {
                String answer = v.get(1);
                int weight = Integer.parseInt(v.get(2));
                List<ATag> tags = new ArrayList<>();
                AExtendedAnswer an = new AExtendedAnswer(0L, q, weight, answer, tags);
                extendedAnswers.add(an);

                String otherAnswer = v.get(3);
                int otherWeight = Integer.parseInt(v.get(4));
                List<ATag> otherTags = new ArrayList<>();
                AExtendedAnswer oan = new AExtendedAnswer(0L, q, otherWeight, otherAnswer, otherTags);
                otherExtendedAnswers.add(oan);
            }
            else {
                String answer = v.get(1);
                int weight = Integer.parseInt(v.get(2));
                ATag tag = LabService.mainManager.getM_que_manager().find_tag(answer);
                AVariantAnswer an = new AVariantAnswer(0L,weight, tag, q);
                variantAnswers.add(an);

                String otherAnswer = v.get(3);
                int otherWeight = Integer.parseInt(v.get(4));
                ATag otherTag = LabService.mainManager.getM_que_manager().find_tag(otherAnswer);
                AVariantAnswer oan = new AVariantAnswer(0L,otherWeight, otherTag, q);
                otherVariantAnswers.add(oan);
            }
        }
        AInformation inf = new AInformation(0L, variantAnswers, extendedAnswers);
        AInformation otherInf = new AInformation(0L, otherVariantAnswers, otherExtendedAnswers);
        AQuestionnaire questionnaire = LabService.mainManager.getM_que_manager().create(
                LabService.currentUser, inf, otherInf);
        if (questionnaire == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    // Получить все анкеты (для Admin и Censor)
    @GetMapping("/allquestionnaires")
    public List<AQuestionnaire> getAllQuestionnaires() throws Exception {
        return LabService.mainManager.getM_que_manager().get_all_questionnaires();
    }


    // Удалить анкету (Admin)
    @PostMapping("/questionnaire/delete/{id}")
    public ResponseEntity<Object> deleteQuestionnaire(@PathVariable Long id) {
        try {
            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            LabService.mainManager.getM_que_manager().delete_questionnaire(q);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete questionnaire");
        }
        return ResponseEntity.ok("Questionnaire deleted");
    }

    // Пометить анкету цензурированной (Censor)
    @PostMapping("/questionnaire/censor/{id}")
    public ResponseEntity<Object> censorQuestionnaire(@PathVariable Long id) {
        try {
            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            LabService.mainManager.getM_que_manager().censor(q);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to censor questionnaire");
        }
        return ResponseEntity.ok("Questionnaire censored");
    }

    // Добавить пользователя в избранное
    @PostMapping("/questionnaire/fav/{questId}/{friendId}")
    public ResponseEntity<Object> addFav(@PathVariable Long questId, @PathVariable Long friendId) {
        try {
            AQuestionnaire activeQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(questId);
            LabService.mainManager.getM_que_manager().set_active_questionnaire(activeQuest);
            AQuestionnaire friendQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(friendId);
            LabService.mainManager.getM_que_manager().add_fav(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add to favorites");
        }
        return ResponseEntity.ok("Added to favorites");
    }

    // Добавить пользователя в черный список
    @PostMapping("/questionnaire/black/{questId}/{friendId}")
    public ResponseEntity<Object> addBlack(@PathVariable Long questId, @PathVariable Long friendId) {
        try {
            AQuestionnaire activeQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(questId);
            LabService.mainManager.getM_que_manager().set_active_questionnaire(activeQuest);
            AQuestionnaire friendQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(friendId);
            LabService.mainManager.getM_que_manager().add_black(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add to blacklist");
        }
        return ResponseEntity.ok("Added to blacklist");
    }

    // Удалить пользователя из избранного
    @PostMapping("/questionnaire/delFav/{questId}/{friendId}")
    public ResponseEntity<Object> delFav(@PathVariable Long questId, @PathVariable Long friendId) {
        try {
            AQuestionnaire friendQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(friendId);
            AQuestionnaire activeQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(questId);
            LabService.mainManager.getM_rec_manager().recommended_questionnaires(activeQuest, List.of(friendQuest));
            LabService.mainManager.getM_que_manager().del_fav(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove from favorites");
        }
        return ResponseEntity.ok("Removed from favorites");
    }

    // Удалить пользователя из черного списка
    @PostMapping("/questionnaire/delBlack/{questId}/{friendId}")
    public ResponseEntity<Object> delBlack(@PathVariable Long questId, @PathVariable Long friendId) {
        try {
            AQuestionnaire friendQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(friendId);
            AQuestionnaire activeQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(questId);
            LabService.mainManager.getM_rec_manager().recommended_questionnaires(activeQuest, List.of(friendQuest));
            LabService.mainManager.getM_que_manager().del_black(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove from blacklist");
        }
        return ResponseEntity.ok("Removed from blacklist");
    }

    // Возвращаем актуальную анкету после установки
    @PostMapping("/questionnaire/setActive/{questId}")
    public ResponseEntity<AQuestionnaire> setActiveQuestionnaire(@PathVariable Long questId) {
        try {
            ReqCacheController.m_list.clear();
            ReqCacheController.running = false;
            AQuestionnaire activeQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(questId);
            LabService.mainManager.getM_que_manager().set_active_questionnaire(activeQuest);


            return ResponseEntity.ok(activeQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }



    @GetMapping("/questionnaire/{id}")
    public ResponseEntity<AQuestionnaire> findQuestionnaire(@PathVariable Long id) {
        try {
            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            if (q != null) {
                if (q.getUser() != null) {
                    q.getUser().getName();
                    q.getUser().getAge();
                    q.getUser().isGender();
                }

                if (q.getBlackList() != null)
                    q.getBlackList().size();

                if (q.getFavList() != null)
                    q.getFavList().size();

                if (q.getInformation() != null) {
                    if (q.getInformation().getExtendedAnswers() != null)
                        q.getInformation().getExtendedAnswers().size();
                    if (q.getInformation().getVariantAnswers() != null)
                        q.getInformation().getVariantAnswers().size();
                }

                if (q.getSearchInformation() != null) {
                    if (q.getSearchInformation().getExtendedAnswers() != null)
                        q.getSearchInformation().getExtendedAnswers().size();
                    if (q.getSearchInformation().getVariantAnswers() != null)
                        q.getSearchInformation().getVariantAnswers().size();
                }
            }

            return ResponseEntity.ok(q);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }
    // Получить базовую информацию анкеты (ID и пользователь)
    @GetMapping("/questionnaire/basic/{id}")
    public ResponseEntity<Map<String, Object>> getQuestionnaireBasic(@PathVariable Long id) {
        try {
            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            if (q == null) return ResponseEntity.notFound().build();

            Map<String, Object> result = new HashMap<>();
            result.put("id", q.getId());
            result.put("user", q.getUser());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Получить черный список анкеты
    @GetMapping("/questionnaire/blacklist/{id}")
    public ResponseEntity<List<AQuestionnaire>> getBlackList(@PathVariable Long id) {
        try {
            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            if (q == null) return ResponseEntity.notFound().build();
            List<AQuestionnaire> blackList = q.getBlackList() != null ? q.getBlackList() : new ArrayList<>();
            return ResponseEntity.ok(blackList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Получить избранное
    @GetMapping("/questionnaire/favlist/{id}")
    public ResponseEntity<List<AQuestionnaire>> getFavList(@PathVariable Long id) {
        try {
            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            if (q == null) return ResponseEntity.notFound().build();
            List<AQuestionnaire> favList = q.getFavList() != null ? q.getFavList() : new ArrayList<>();
            return ResponseEntity.ok(favList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Получить вариантные ответы
    @GetMapping("/questionnaire/variantanswers/{id}")
    public ResponseEntity<List<AVariantAnswer>> getVariantAnswers(@PathVariable Long id) {
        try {
            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            if (q == null || q.getInformation() == null) return ResponseEntity.notFound().build();
            List<AVariantAnswer> answers = q.getInformation().getVariantAnswers() != null ? q.getInformation().getVariantAnswers() : new ArrayList<>();
            return ResponseEntity.ok(answers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Получить развёрнутые ответы
    @GetMapping("/questionnaire/extendedanswers/{id}")
    public ResponseEntity<List<AExtendedAnswer>> getExtendedAnswers(@PathVariable Long id) {
        try {
            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            if (q == null || q.getInformation() == null) return ResponseEntity.notFound().build();
            List<AExtendedAnswer> answers = q.getInformation().getExtendedAnswers() != null ? q.getInformation().getExtendedAnswers() : new ArrayList<>();
            return ResponseEntity.ok(answers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Вариантные ответы другого человека
    @GetMapping("/questionnaire/search/variantanswers/{id}")
    public ResponseEntity<List<AVariantAnswer>> getSearchVariantAnswers(@PathVariable Long id) {
        try {
            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            if (q == null || q.getSearchInformation() == null) return ResponseEntity.notFound().build();
            List<AVariantAnswer> answers = q.getSearchInformation().getVariantAnswers() != null
                    ? q.getSearchInformation().getVariantAnswers()
                    : new ArrayList<>();
            return ResponseEntity.ok(answers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Развёрнутые ответы другого человека
    @GetMapping("/questionnaire/search/extendedanswers/{id}")
    public ResponseEntity<List<AExtendedAnswer>> getSearchExtendedAnswers(@PathVariable Long id) {
        try {
            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            if (q == null || q.getSearchInformation() == null) return ResponseEntity.notFound().build();
            List<AExtendedAnswer> answers = q.getSearchInformation().getExtendedAnswers() != null
                    ? q.getSearchInformation().getExtendedAnswers()
                    : new ArrayList<>();
            return ResponseEntity.ok(answers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





}
*/