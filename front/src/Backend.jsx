import axios from 'axios'

const API_URL = 'http://localhost:9099/api/v1'

axios.interceptors.request.use(config => {
    config.headers["Pragma"] = "no-cache"
    config.headers["Cache-Control"] = "no-cache"
    return config
})

class Backend {
    // --- Аутентификация ---
    login(name, password) {
        return axios.post(`${API_URL}/login`, { name, password })
    }

    register(username, password, age, gender) {
        return axios.post(`${API_URL}/register`, {
            username,
            password,
            age,
            gender
        });
    }

    logout() {
        return axios.get(`${API_URL}/logout`)
    }

    // --- Анкеты ---
    getQuestions() {
        return axios.get(`${API_URL}/questions`)
    }

    submitQuest(answers) {
        return axios.post(`${API_URL}/submitquest`, answers)
    }

    getUserQuests() {
        return axios.get(`${API_URL}/userquests`)
    }

    // --- Друзья и списки ---
    getUserFriends(questId) {
        return axios.get(`${API_URL}/getfriends/${questId}`)
    }

    addFav(questId, friendId) {
        return axios.post(`${API_URL}/questionnaire/fav/${questId}/${friendId}`)
    }

    addBlack(questId, friendId) {
        return axios.post(`${API_URL}/questionnaire/black/${questId}/${friendId}`)
    }

    delFav(questId, friendId) {
        return axios.post(`${API_URL}/questionnaire/delFav/${questId}/${friendId}`)
    }

    delBlack(questId, friendId) {
        return axios.post(`${API_URL}/questionnaire/delBlack/${questId}/${friendId}`)
    }

    // --- Admin и Censor ---
    getAllQuestionnaires() {
        return axios.get(`${API_URL}/allquestionnaires`)
    }

    deleteQuestionnaire(id) {
        return axios.post(`${API_URL}/questionnaire/delete/${id}`)
    }

    censorQuestionnaire(id) {
        return axios.post(`${API_URL}/questionnaire/censor/${id}`)
    }

    // --- Новые эндпоинты для полной информации ---
    getQuestBasic(id) {
        return axios.get(`${API_URL}/questionnaire/basic/${id}`)
    }

    getQuestBlackList(id) {
        return axios.get(`${API_URL}/questionnaire/blacklist/${id}`)
    }

    getQuestFavList(id) {
        return axios.get(`${API_URL}/questionnaire/favlist/${id}`)
    }

    getQuestVariantAnswers(id) {
        return axios.get(`${API_URL}/questionnaire/variantanswers/${id}`)
    }

    getQuestExtendedAnswers(id) {
        return axios.get(`${API_URL}/questionnaire/extendedanswers/${id}`)
    }
    setActiveQuestionnaire(questId) {
        return axios.post(`${API_URL}/questionnaire/setActive/${questId}`);
    }
    // Вариантные ответы другого человека
    getSearchVariantAnswers(id) {
        return axios.get(`${API_URL}/questionnaire/search/variantanswers/${id}`);
    }

    // Развёрнутые ответы другого человека
    getSearchExtendedAnswers(id) {
        return axios.get(`${API_URL}/questionnaire/search/extendedanswers/${id}`);
    }
}

export default new Backend()
