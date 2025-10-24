

package ru.bmstu.iu7;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.iu7.API.dto.*;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.src.controllers.ReqCacheController;
import jakarta.servlet.http.HttpSession;
import ru.bmstu.iu7.util.JwtUtil;

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

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        try {
            LabService.mainManager.getM_user_manager().delete(id);
        }
        catch (Exception e) {

        }
        return ResponseEntity.ok("");
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Object> putUser(@PathVariable Long id, @RequestBody PutUserRequest crs) {
        try {
            // Найти пользователя по ID
            AUser user = LabService.userRepository.findById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Полностью обновляем поля
            user.setName(crs.getUsername());
            user.setPassword(crs.getPassword());
            user.setAge(crs.getAge());
            user.setGender(crs.isGender());
            user.setRole(crs.getRole());

            // Сохраняем обратно в репозиторий
            LabService.userRepository.saveUser(user);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user");
        }
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable Long id, @RequestBody UserPatch crs) {
        try {
            // Найти пользователя по ID
            AUser user = LabService.userRepository.findById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Обновляем только непустые/не null поля
            if (crs.getName() != null && !crs.getName().isBlank()) user.setName(crs.getName());
            if (crs.getPassword() != null && !crs.getPassword().isBlank()) user.setPassword(crs.getPassword());
            if (crs.getAge() != null && crs.getAge() > 0) user.setAge(crs.getAge());
            if (crs.getGender() != null) user.setGender(crs.getGender());

            // Сохраняем обратно
            LabService.userRepository.saveUser(user);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user");
        }
    }



    @GetMapping("/users/{id}")
    public AUser getUser(@PathVariable Long id) {
        AUser user = LabService.userRepository.findById(id);
        return user;
    }

    @DeleteMapping("/users/{id}/clearquests")
    public ResponseEntity<Object> deleteAllUserQuests(@PathVariable Long id) {
        try {
            LabService.mainManager.getM_que_manager().delete_user_questionnaires(id);
        }
        catch (Exception e) {

        }
        return ResponseEntity.ok("");
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegisterRequest crs) {
        try {
            AUser user = LabService.mainManager.getM_user_manager()
                    .register(crs.getUsername(), crs.getPassword(), crs.getAge(), crs.isGender());

            if (user == null) throw new Exception("Registration failed");

            LabService.currentUser = user;
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> map = new HashMap<>();
            map.put("error", "registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest crs) {
        try {
            AUser user = LabService.mainManager.getM_user_manager()
                    .authorize(crs.getName(), crs.getPassword());

            if (user == null || !crs.getPassword().equals(user.getPassword()))
                throw new Exception("Invalid credentials");

            // Генерация JWT
            String token = JwtUtil.generateToken(user.getName(), user.getId());

            Map<String, String> response = new HashMap<>();
            response.put("user", user.getName());
            response.put("id", user.getId().toString());
            response.put("token", token); // добавляем токен

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logoutUser() {
        LabService.currentUser = null;
        return ResponseEntity.ok("");
    }

    @PostMapping("/quest")
    public ResponseEntity<Object> submitQuest(@RequestBody List<QuestionSubmission> data) throws Exception {
        if (LabService.currentUser == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<AQuestion> qs = LabService.mainManager.getM_que_manager().get_all_questions();

        if (qs.size() != data.size()) {
            return new ResponseEntity<>("Количество ответов не совпадает с количеством вопросов", HttpStatus.BAD_REQUEST);
        }

        List<AVariantAnswer> variantAnswers = new ArrayList<>();
        List<AExtendedAnswer> extendedAnswers = new ArrayList<>();
        List<AVariantAnswer> otherVariantAnswers = new ArrayList<>();
        List<AExtendedAnswer> otherExtendedAnswers = new ArrayList<>();

        for (int i = 0; i < qs.size(); i++) {
            AQuestion q = qs.get(i);
            QuestionSubmission dto = data.get(i);

            if ("EXT".equalsIgnoreCase(dto.getType())) {
                extendedAnswers.add(new AExtendedAnswer(0L, q, dto.getWeight1(), dto.getAnswer1(), new ArrayList<>()));
                otherExtendedAnswers.add(new AExtendedAnswer(0L, q, dto.getWeight2(), dto.getAnswer2(), new ArrayList<>()));
            } else {
                ATag tag1 = LabService.mainManager.getM_que_manager().find_tag(dto.getAnswer1());
                ATag tag2 = LabService.mainManager.getM_que_manager().find_tag(dto.getAnswer2());
                variantAnswers.add(new AVariantAnswer(0L, dto.getWeight1(), tag1, q));
                otherVariantAnswers.add(new AVariantAnswer(0L, dto.getWeight2(), tag2, q));
            }
        }

        AInformation inf = new AInformation(0L, variantAnswers, extendedAnswers);
        AInformation otherInf = new AInformation(0L, otherVariantAnswers, otherExtendedAnswers);
        AQuestionnaire questionnaire = LabService.mainManager.getM_que_manager().create(LabService.currentUser, inf, otherInf);

        if (questionnaire == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.ok().build();
    }





    @GetMapping("/quests")
    public List<AQuestionnaire> getUserQuests() throws Exception {
        if (LabService.currentUser == null)
            return new ArrayList<>();
        if (Objects.equals(LabService.currentUser.getRole(), "user")) {
            List<AQuestionnaire> list = LabService.mainManager.getM_que_manager().
                    get_user_questionnaies(LabService.currentUser.getId());
            return list;
        }
        else
            return LabService.mainManager.getM_que_manager().get_all_questionnaires();
    }

    @GetMapping("/quests/{id}/friends")
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

    // Удалить анкету (Admin)
    @DeleteMapping("/quests/{id}")
    public ResponseEntity<Object> deleteQuestionnaire(@PathVariable Long id) {
        try {
            if (Objects.equals(LabService.currentUser.getRole(), "admin")) {
                AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
                LabService.mainManager.getM_que_manager().delete_questionnaire(q);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete questionnaire");
        }
        return ResponseEntity.ok("Questionnaire deleted");
    }

    // Пометить анкету цензурированной (Censor)
    @PostMapping("/quests/{id}/censor/")
    public ResponseEntity<Object> censorQuestionnaire(@PathVariable Long id) {
        try {
            if (Objects.equals(LabService.currentUser.getRole(), "censor")) {
                AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
                LabService.mainManager.getM_que_manager().censor(q);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to censor questionnaire");
        }
        return ResponseEntity.ok("Questionnaire censored");
    }

    // Добавить пользователя в избранное
    @PostMapping("/quests/{id}/fav/{friendId}")
    public ResponseEntity<Object> addFav(@PathVariable Long id, @PathVariable Long friendId) {
        try {
            AQuestionnaire activeQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            LabService.mainManager.getM_que_manager().set_active_questionnaire(activeQuest);
            AQuestionnaire friendQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(friendId);
            LabService.mainManager.getM_que_manager().add_fav(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add to favorites");
        }
        return ResponseEntity.ok("Added to favorites");
    }

    // Добавить пользователя в черный список
    @PostMapping("/quests/{id}/black/{friendId}")
    public ResponseEntity<Object> addBlack(@PathVariable Long id, @PathVariable Long friendId) {
        try {
            AQuestionnaire activeQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            LabService.mainManager.getM_que_manager().set_active_questionnaire(activeQuest);
            AQuestionnaire friendQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(friendId);
            LabService.mainManager.getM_que_manager().add_black(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add to blacklist");
        }
        return ResponseEntity.ok("Added to blacklist");
    }

    // Удалить пользователя из избранного
    @DeleteMapping("/quests/{id}/fav/{friendId}")
    public ResponseEntity<Object> delFav(@PathVariable Long id, @PathVariable Long friendId) {
        try {
            AQuestionnaire friendQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(friendId);
            AQuestionnaire activeQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            LabService.mainManager.getM_rec_manager().recommended_questionnaires(activeQuest, List.of(friendQuest));
            LabService.mainManager.getM_que_manager().del_fav(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove from favorites");
        }
        return ResponseEntity.ok("Removed from favorites");
    }

    // Удалить пользователя из черного списка
    @DeleteMapping("/quests/{id}/black/{friendId}")
    public ResponseEntity<Object> delBlack(@PathVariable Long id, @PathVariable Long friendId) {
        try {
            AQuestionnaire friendQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(friendId);
            AQuestionnaire activeQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            LabService.mainManager.getM_rec_manager().recommended_questionnaires(activeQuest, List.of(friendQuest));
            LabService.mainManager.getM_que_manager().del_black(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove from blacklist");
        }
        return ResponseEntity.ok("Removed from blacklist");
    }

    // Возвращаем актуальную анкету после установки
    @PostMapping("/quests/{id}/activate")
    public ResponseEntity<AQuestionnaire> setActiveQuestionnaire(@PathVariable Long id) {
        try {
            ReqCacheController.m_list.clear();
            ReqCacheController.running = false;
            AQuestionnaire activeQuest = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
            LabService.mainManager.getM_que_manager().set_active_questionnaire(activeQuest);


            return ResponseEntity.ok(activeQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/questions")
    public List<AQuestion> getQuestions() throws Exception {
        List<AQuestion> qs = LabService.mainManager.getM_que_manager().get_all_questions();
        return qs;
    }


    //для отображения


//    @GetMapping("/quests/{id}")
//    public ResponseEntity<AQuestionnaire> findQuestionnaire(@PathVariable Long id) {
//        try {
//            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
//            if (q != null) {
//                if (q.getUser() != null) {
//                    q.getUser().getName();
//                    q.getUser().getAge();
//                    q.getUser().isGender();
//                }
//
//                if (q.getBlackList() != null)
//                    q.getBlackList().size();
//
//                if (q.getFavList() != null)
//                    q.getFavList().size();
//
//                if (q.getInformation() != null) {
//                    if (q.getInformation().getExtendedAnswers() != null)
//                        q.getInformation().getExtendedAnswers().size();
//                    if (q.getInformation().getVariantAnswers() != null)
//                        q.getInformation().getVariantAnswers().size();
//                }
//
//                if (q.getSearchInformation() != null) {
//                    if (q.getSearchInformation().getExtendedAnswers() != null)
//                        q.getSearchInformation().getExtendedAnswers().size();
//                    if (q.getSearchInformation().getVariantAnswers() != null)
//                        q.getSearchInformation().getVariantAnswers().size();
//                }
//            }
//
//            return ResponseEntity.ok(q);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//
//
//    }
//
//    // Получить базовую информацию анкеты (ID и пользователь)
//    @GetMapping("/quests/{id}/maininfo")
//    public ResponseEntity<Map<String, Object>> getQuestionnaireBasic(@PathVariable Long id) {
//        try {
//            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
//            if (q == null) return ResponseEntity.notFound().build();
//
//            Map<String, Object> result = new HashMap<>();
//            result.put("id", q.getId());
//            result.put("user", q.getUser());
//
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    // Получить черный список анкеты
//    @GetMapping("/quests/{id}/black")
//    public ResponseEntity<List<AQuestionnaire>> getBlackList(@PathVariable Long id) {
//        try {
//            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
//            if (q == null) return ResponseEntity.notFound().build();
//            List<AQuestionnaire> blackList = q.getBlackList() != null ? q.getBlackList() : new ArrayList<>();
//            return ResponseEntity.ok(blackList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    // Получить избранное
//    @GetMapping("/quests/{id}/fav")
//    public ResponseEntity<List<AQuestionnaire>> getFavList(@PathVariable Long id) {
//        try {
//            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
//            if (q == null) return ResponseEntity.notFound().build();
//            List<AQuestionnaire> favList = q.getFavList() != null ? q.getFavList() : new ArrayList<>();
//            return ResponseEntity.ok(favList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    // Получить вариантные ответы
//    @GetMapping("/quests/{id}/variantanswers/")
//    public ResponseEntity<List<AVariantAnswer>> getVariantAnswers(@PathVariable Long id) {
//        try {
//            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
//            if (q == null || q.getInformation() == null) return ResponseEntity.notFound().build();
//            List<AVariantAnswer> answers = q.getInformation().getVariantAnswers() != null ? q.getInformation().getVariantAnswers() : new ArrayList<>();
//            return ResponseEntity.ok(answers);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    // Получить развёрнутые ответы
//    @GetMapping("/quests/{id}/extendedanswers")
//    public ResponseEntity<List<AExtendedAnswer>> getExtendedAnswers(@PathVariable Long id) {
//        try {
//            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
//            if (q == null || q.getInformation() == null) return ResponseEntity.notFound().build();
//            List<AExtendedAnswer> answers = q.getInformation().getExtendedAnswers() != null ? q.getInformation().getExtendedAnswers() : new ArrayList<>();
//            return ResponseEntity.ok(answers);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    // Вариантные ответы другого человека
//    @GetMapping("/quests/{id}/search/variantanswers/")
//    public ResponseEntity<List<AVariantAnswer>> getSearchVariantAnswers(@PathVariable Long id) {
//        try {
//            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
//            if (q == null || q.getSearchInformation() == null) return ResponseEntity.notFound().build();
//            List<AVariantAnswer> answers = q.getSearchInformation().getVariantAnswers() != null
//                    ? q.getSearchInformation().getVariantAnswers()
//                    : new ArrayList<>();
//            return ResponseEntity.ok(answers);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    // Развёрнутые ответы другого человека
//    @GetMapping("/quests/{id}/search/extendedanswers/")
//    public ResponseEntity<List<AExtendedAnswer>> getSearchExtendedAnswers(@PathVariable Long id) {
//        try {
//            AQuestionnaire q = LabService.mainManager.getM_que_manager().findQuestionnaire(id);
//            if (q == null || q.getSearchInformation() == null) return ResponseEntity.notFound().build();
//            List<AExtendedAnswer> answers = q.getSearchInformation().getExtendedAnswers() != null
//                    ? q.getSearchInformation().getExtendedAnswers()
//                    : new ArrayList<>();
//            return ResponseEntity.ok(answers);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }





}
