package ru.bmstu.iu7;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.iu7.API.*;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.dto.*;
import ru.bmstu.iu7.src.controllers.ReqCacheController;
import ru.bmstu.iu7.util.JwtUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "http://localhost:3000")
public class Controller {

    @GetMapping("/users")
    public List<AUser> getUsers() {
        List<AUser> users = Main.userRepository.findAll();
        return users;
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        try {
            Main.mainManager.getM_user_manager().delete(id);
        } catch (Exception e) {
            // Игнорируем ошибку
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Object> putUser(@PathVariable Long id, @RequestBody PutUserRequest crs) {
        try {
            AUser user = Main.userRepository.findById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            user.setName(crs.getUsername());
            user.setPassword(crs.getPassword());
            user.setAge(crs.getAge());
            user.setGender(crs.isGender());
            user.setRole(crs.getRole());

            Main.userRepository.saveUser(user);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update user");
        }
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable Long id, @RequestBody UserPatch crs) {
        try {
            AUser user = Main.userRepository.findById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            if (crs.getName() != null && !crs.getName().isBlank()) {
                user.setName(crs.getName());
            }
            if (crs.getPassword() != null && !crs.getPassword().isBlank()) {
                user.setPassword(crs.getPassword());
            }
            if (crs.getAge() != null && crs.getAge() > 0) {
                user.setAge(crs.getAge());
            }
            if (crs.getGender() != null) {
                user.setGender(crs.getGender());
            }

            Main.userRepository.saveUser(user);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update user");
        }
    }

    @GetMapping("/users/{id}")
    public AUser getUser(@PathVariable Long id) {
        return Main.userRepository.findById(id);
    }

    @DeleteMapping("/users/{id}/clearquests")
    public ResponseEntity<Object> deleteAllUserQuests(@PathVariable Long id) {
        try {
            Main.mainManager.getM_que_manager().delete_user_questionnaires(id);
        } catch (Exception e) {
            // Игнорируем ошибку
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegisterRequest crs) {
        try {
            AUser user = Main.mainManager.getM_user_manager()
                    .register(
                            crs.getUsername(),
                            crs.getPassword(),
                            crs.getAge(),
                            crs.isGender()
                    );

            if (user == null) throw new Exception("Registration failed");

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
            AUser user = Main.mainManager.getM_user_manager()
                    .authorize(crs.getName(), crs.getPassword());

            if (user == null || !crs.getPassword().equals(user.getPassword())) {
                throw new Exception("Invalid credentials");
            }

            String token = JwtUtil.generateToken(user.getName(), user.getId());

            Map<String, String> response = new HashMap<>();
            response.put("user", user.getName());
            response.put("id", user.getId().toString());
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Object> logoutUser() {
        return ResponseEntity.ok("");
    }

    @PostMapping("/quest")
    public ResponseEntity<Object> submitQuest(@RequestBody List<QuestionSubmission> data,
                                              HttpServletRequest request) throws Exception {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String token = header.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        AUser currentUser = Main.userRepository.findById(userId);
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<AQuestion> qs = Main.mainManager.getM_que_manager().get_all_questions();
        if (qs.size() != data.size()) {
            return new ResponseEntity<>(
                    "Количество ответов не совпадает с количеством вопросов",
                    HttpStatus.BAD_REQUEST
            );
        }

        List<AVariantAnswer> variantAnswers = new ArrayList<>();
        List<AExtendedAnswer> extendedAnswers = new ArrayList<>();
        List<AVariantAnswer> otherVariantAnswers = new ArrayList<>();
        List<AExtendedAnswer> otherExtendedAnswers = new ArrayList<>();

        for (int i = 0; i < qs.size(); i++) {
            AQuestion q = qs.get(i);
            QuestionSubmission dto = data.get(i);

            if ("EXT".equalsIgnoreCase(dto.getType())) {
                extendedAnswers.add(new AExtendedAnswer(
                        0L, q, dto.getWeight1(), dto.getAnswer1(), new ArrayList<>()
                ));
                otherExtendedAnswers.add(new AExtendedAnswer(
                        0L, q, dto.getWeight2(), dto.getAnswer2(), new ArrayList<>()
                ));
            } else {
                ATag tag1 = Main.mainManager.getM_que_manager().find_tag(dto.getAnswer1());
                ATag tag2 = Main.mainManager.getM_que_manager().find_tag(dto.getAnswer2());
                variantAnswers.add(new AVariantAnswer(0L, dto.getWeight1(), tag1, q));
                otherVariantAnswers.add(new AVariantAnswer(0L, dto.getWeight2(), tag2, q));
            }
        }

        AInformation inf = new AInformation(0L, variantAnswers, extendedAnswers);
        AInformation otherInf = new AInformation(0L, otherVariantAnswers, otherExtendedAnswers);
        AQuestionnaire questionnaire = Main.mainManager.getM_que_manager().create(currentUser, inf, otherInf);

        if (questionnaire == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/quests")
    public ResponseEntity<List<AQuestionnaire>> getUserQuests(HttpServletRequest request) throws Exception {
        AUser currentUser = getCurrentUserFromRequest(request);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
        }

        List<AQuestionnaire> list;
        if ("user".equals(currentUser.getRole())) {
            list = Main.mainManager.getM_que_manager().get_user_questionnaies(currentUser.getId());
        } else {
            list = Main.mainManager.getM_que_manager().get_all_questionnaires();
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/quests/{id}/friends")
    public ResponseEntity<List<AQuestionnaire>> getFriends(@PathVariable Long id,
                                                           HttpServletRequest request) throws Exception {
        AUser currentUser = getCurrentUserFromRequest(request);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
        }

        AQuestionnaire activeQuest = Main.mainManager.getM_que_manager().findQuestionnaire(id);
        Main.mainManager.getM_que_manager().set_active_questionnaire(activeQuest);

        List<AQuestionnaire> qs = Main.mainManager.getM_rec_manager().get_friends();
        List<AQuestionnaire> blackList = activeQuest.getBlackList();
        List<AQuestionnaire> favList = activeQuest.getFavList();

        qs.removeIf(q -> blackList.contains(q) || favList.contains(q));

        return ResponseEntity.ok(qs);
    }

    @DeleteMapping("/quests/{id}")
    public ResponseEntity<Object> deleteQuestionnaire(@PathVariable Long id,
                                                      HttpServletRequest request) {
        try {
            AUser currentUser = getCurrentUserFromRequest(request);
            if (currentUser == null || !"admin".equals(currentUser.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            AQuestionnaire q = Main.mainManager.getM_que_manager().findQuestionnaire(id);
            Main.mainManager.getM_que_manager().delete_questionnaire(q);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete questionnaire");
        }
    }

    @PostMapping("/quests/{id}/censor")
    public ResponseEntity<Object> censorQuestionnaire(@PathVariable Long id,
                                                      HttpServletRequest request) {
        try {
            AUser currentUser = getCurrentUserFromRequest(request);
            if (currentUser == null || !"censor".equals(currentUser.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            AQuestionnaire q = Main.mainManager.getM_que_manager().findQuestionnaire(id);
            Main.mainManager.getM_que_manager().censor(q);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to censor questionnaire");
        }
    }

    private AUser getCurrentUserFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.extractUserId(token);
            return Main.userRepository.findById(userId);
        }
        return null;
    }

    @PostMapping("/quests/{id}/fav/{friendId}")
    public ResponseEntity<Object> addFav(@PathVariable Long id, @PathVariable Long friendId) {
        try {
            AQuestionnaire activeQuest = Main.mainManager.getM_que_manager().findQuestionnaire(id);
            Main.mainManager.getM_que_manager().set_active_questionnaire(activeQuest);
            AQuestionnaire friendQuest = Main.mainManager.getM_que_manager().findQuestionnaire(friendId);
            Main.mainManager.getM_que_manager().add_fav(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add to favorites");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/quests/{id}/black/{friendId}")
    public ResponseEntity<Object> addBlack(@PathVariable Long id, @PathVariable Long friendId) {
        try {
            AQuestionnaire activeQuest = Main.mainManager.getM_que_manager().findQuestionnaire(id);
            Main.mainManager.getM_que_manager().set_active_questionnaire(activeQuest);
            AQuestionnaire friendQuest = Main.mainManager.getM_que_manager().findQuestionnaire(friendId);
            Main.mainManager.getM_que_manager().add_black(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add to blacklist");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/quests/{id}/fav/{friendId}")
    public ResponseEntity<Object> delFav(@PathVariable Long id, @PathVariable Long friendId) {
        try {
            AQuestionnaire friendQuest = Main.mainManager.getM_que_manager().findQuestionnaire(friendId);
            AQuestionnaire activeQuest = Main.mainManager.getM_que_manager().findQuestionnaire(id);
            Main.mainManager.getM_rec_manager().recommended_questionnaires(activeQuest, List.of(friendQuest));
            Main.mainManager.getM_que_manager().del_fav(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove from favorites");
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/quests/{id}/black/{friendId}")
    public ResponseEntity<Object> delBlack(@PathVariable Long id, @PathVariable Long friendId) {
        try {
            AQuestionnaire friendQuest = Main.mainManager.getM_que_manager().findQuestionnaire(friendId);
            AQuestionnaire activeQuest = Main.mainManager.getM_que_manager().findQuestionnaire(id);
            Main.mainManager.getM_rec_manager().recommended_questionnaires(activeQuest, List.of(friendQuest));
            Main.mainManager.getM_que_manager().del_black(friendQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove from blacklist");
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/quests/{id}/activate")
    public ResponseEntity<AQuestionnaire> setActiveQuestionnaire(@PathVariable Long id) {
        try {
            ReqCacheController.m_list.clear();
            ReqCacheController.running = false;
            AQuestionnaire activeQuest = Main.mainManager.getM_que_manager().findQuestionnaire(id);
            Main.mainManager.getM_que_manager().set_active_questionnaire(activeQuest);

            return ResponseEntity.ok(activeQuest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/questions")
    public List<AQuestion> getQuestions() throws Exception {
        return Main.mainManager.getM_que_manager().get_all_questions();
    }
}
