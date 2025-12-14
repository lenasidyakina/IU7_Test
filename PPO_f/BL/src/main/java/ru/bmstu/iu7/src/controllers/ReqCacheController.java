package ru.bmstu.iu7.src.controllers;

import ru.bmstu.iu7.API.model.AQuestionnaire;

import java.util.ArrayList;
import java.util.List;

public class ReqCacheController {

    public static List<AQuestionnaire> m_list = new ArrayList<>();
    public static boolean running = false;

    public ReqCacheController() { }

    public void delete_from_cache(AQuestionnaire q) {
        m_list.remove(q);
    }

    public List<AQuestionnaire> get_req_cache() {
        return m_list;
    }

    public void setM_req_cache(List<AQuestionnaire> aQuestionnaires) {
        m_list = aQuestionnaires;
    }

    public void clearAll() {
        m_list.clear();
        running = false;
    }
}
