package ru.bmstu.iu7.src.managers;

import ru.bmstu.iu7.API.AppLogger;
import ru.bmstu.iu7.API.model.*;
import ru.bmstu.iu7.src.controllers.QuestionnaireController;
import ru.bmstu.iu7.src.controllers.ReqCacheController;

import java.util.*;

public class RecManager {
    private final QuestionnaireController m_controller;
    private static boolean is_update_running = false;
    private final Object sync = new Object();
    private final AppLogger logger;

    public RecManager(QuestionnaireController controller) {
        this(controller, null);
    }

    public RecManager(QuestionnaireController controller, AppLogger applogger) {
        this.m_controller = controller;
        this.logger = applogger;
        logInfo("RecManager initialized");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (m_controller.get_active_questionnaire() != null) {
                    doGetFriends();
                    ReqCacheController.running = true;
                }
            }
        };
    }

    public void doGetFriends() {
        logInfo("Get Friends");
        logInfo("ScheduledTask started for updating recommendations");
        try {
            AQuestionnaire current = m_controller.get_active_questionnaire();
            if (current == null) return;

            m_controller.clear_req_cache_active_quest();

            int page = 0;
            int pageSize = 1000;
            while (true) {
                List<AQuestionnaire> all = (List<AQuestionnaire>) m_controller.get_questionnairies(
                        page, pageSize, current.getUser().getId());
                List<AQuestionnaire> filtered = filterQuestionnaires(current, all);
                if (filtered.isEmpty()) break;
                recommended_questionnaires(current, filtered);
                logInfo("Processed page {} for recommendations", page);
                page++;
            }

            m_controller.update_req_cache();
            logInfo("Request cache updated successfully");
        } catch (Exception e) {
            logInfo("ScheduledTask finished");
        }
    }

    private List<AQuestionnaire> filterQuestionnaires(AQuestionnaire current, List<AQuestionnaire> all) {
        List<AQuestionnaire> result = new ArrayList<>();
        List<AQuestionnaire> black_list = current.getBlackList();
        List<AQuestionnaire> fav_list = current.getFavList();
        for (AQuestionnaire q : all) {
            if (!black_list.contains(q) && !fav_list.contains(q)) {
                result.add(q);
            }
        }
        return result;
    }

    public List<AQuestionnaire> get_friends() {
        try {
            AQuestionnaire current = m_controller.get_active_questionnaire();
            if (current == null) {
                logWarn("No active questionnaire found when getting friends");
                return Collections.emptyList();
            }
            if (!ReqCacheController.running) {
                ReqCacheController.running = true;
                doGetFriends();
            }
            logInfo("Returning cached friends list");
            return m_controller.get_quest_in_cache();
        } catch (Exception e) {
            logError("Failed to get friends", e);
            return Collections.emptyList();
        }
    }

    public int information_comparison(AInformation self, AInformation other) {
        try {
            logInfo(":self {} other {}", self.getId(), other.getId());
            logInfo("Comparing information between two questionnaires");

            int similarityVar = computeVariantSimilarity(self.getVariantAnswers(), other.getVariantAnswers());
            int similarityExt = computeExtendedSimilarity(self.getExtendedAnswers(), other.getExtendedAnswers());

            int total = similarityVar + similarityExt;
            logInfo("Information comparison result: {}", total);
            return total;
        } catch (Exception e) {
            logError("Error during information comparison", e);
            return 0;
        }
    }

    private int computeVariantSimilarity(List<AVariantAnswer> a1, List<AVariantAnswer> a2) {
        int similarity = 0;
        for (int i = 0; i < Math.min(a1.size(), a2.size()); i++) {
            ATag tag1 = a1.get(i).getTag();
            ATag tag2 = a2.get(i).getTag();
            if (tag1 != null && tag1.equals(tag2)) similarity += a1.get(i).getWeight();
        }
        return similarity;
    }

    private int computeExtendedSimilarity(List<AExtendedAnswer> a1, List<AExtendedAnswer> a2) {
        int similarity = 0;
        for (int i = 0; i < Math.min(a1.size(), a2.size()); i++) {
            Set<ATag> tagsSet = new HashSet<>(a1.get(i).getTags());
            int matches = 0;
            for (ATag tag : a2.get(i).getTags()) {
                if (tagsSet.contains(tag)) {
                    matches++;
                    tagsSet.remove(tag);
                }
            }
            similarity += matches * a1.get(i).getWeight();
        }
        return similarity;
    }

    int count = 0;

    public void recommended_questionnaires(AQuestionnaire current, List<AQuestionnaire> all) {
        try {
            logInfo("Generating recommended questionnaires");
            count = 0;
            for (AQuestionnaire questionnaire : all) {
                processSingleRecommendation(current, questionnaire);
            }
        } catch (Exception e) {
            logError("Error generating recommended questionnaires", e);
        }
    }

    private void processSingleRecommendation(AQuestionnaire current, AQuestionnaire questionnaire) {
        try {
            if (Objects.equals(current.getUser().getId(), questionnaire.getUser().getId())) return;

            int firstSim = information_comparison(current.getInformation(), questionnaire.getSearchInformation());
            int secondSim = information_comparison(current.getSearchInformation(), questionnaire.getInformation());

            double harmonicNorm = computeHarmonicNorm(firstSim, secondSim, 1, 1);

            if (shouldAddToCache(harmonicNorm, questionnaire)) {
                logInfo("count = " + (count++));
                m_controller.add_in_cache_list(harmonicNorm, questionnaire);
                logInfo("Added questionnaire {} to recommendation cache with score {}",
                        questionnaire.getId(), harmonicNorm);
            }
        } catch (Exception e) {
            logError("Error processing single recommendation", e);
        }
    }

    private double computeHarmonicNorm(int firstSim, int secondSim, int countExtTags, int countVarTags) {
        if (firstSim + secondSim == 0) return 0;
        double harmonic = (2.0 * firstSim * secondSim) / (firstSim + secondSim);
        double maxHarmonic = 10 * (countExtTags + countVarTags);
        return harmonic / maxHarmonic;
    }

    private boolean shouldAddToCache(double norm, AQuestionnaire q) {
        return norm >= 0 && !m_controller.is_in_black(q) && !m_controller.is_in_fav(q);
    }

    private void logInfo(String msg, Object... args) {
        if (logger != null) logger.info(msg, args);
    }

    private void logWarn(String msg, Object... args) {
        if (logger != null) logger.warn(msg, args);
    }

    private void logError(String msg, Exception e) {
        if (logger != null) logger.error(msg, e);
    }
}
