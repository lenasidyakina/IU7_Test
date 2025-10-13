import Backend from "../Backend";
import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";

export default function Home() {
    const nav = useNavigate();
    const [quests, setQuests] = useState([]);
    const [selectedQuest, setSelectedQuest] = useState(null);
    const [userId, setUserId] = useState(null);

    // Загружаем анкеты пользователя
    useEffect(() => {
        Backend.getUserQuests()
            .then(resp => {
                const data = resp.data || [];
                setQuests(data);

                if (data.length > 0) {
                    // Получаем userId из первой анкеты
                    const uid = data[0].userId || "default";
                    setUserId(uid);

                    // Пытаемся достать сохранённую анкету для этого пользователя
                    const savedQuest = localStorage.getItem(`selectedQuest_${uid}`);
                    const initialQuest = savedQuest && data.find(q => String(q.id) === savedQuest)
                        ? savedQuest
                        : String(data[0].id);

                    setSelectedQuest(initialQuest);

                    // Устанавливаем активную анкету на бэкенде
                    Backend.setActiveQuestionnaire(initialQuest)
                        .catch(err => console.error("Error setting active quest:", err));
                }
            })
            .catch(err => console.error("Error loading quests", err));
    }, []);

    // Сохраняем выбранную анкету для текущего пользователя
    useEffect(() => {
        if (selectedQuest && userId) {
            localStorage.setItem(`selectedQuest_${userId}`, selectedQuest);
            Backend.setActiveQuestionnaire(selectedQuest)
                .then(resp => console.log("Active questionnaire set:", resp.data))
                .catch(err => console.error("Error setting active quest:", err));
        }
    }, [selectedQuest, userId]);

    const handleLogout = (e) => {
        e.preventDefault();
        Backend.logout()
            .then(() => {
                if (userId) localStorage.removeItem(`selectedQuest_${userId}`);
                nav("/login");
            })
            .catch(() => alert("Logout error"));
    };

    const handleQuest = () => nav("/quest");
    const handleFriends = () => selectedQuest && nav(`/friends/${selectedQuest}`);
    const handleCurrentQuest = () => selectedQuest && nav(`/quest/${selectedQuest}`);
    const handleFavorites = () => selectedQuest && nav(`/favorites/${selectedQuest}`);
    const handleBlacklist = () => selectedQuest && nav(`/blacklist/${selectedQuest}`);

    return (
        <div className="container">
            <h2>Home</h2>

            <div className="mb-3">
                <label className="form-label">Select your questionnaire:</label>
                <select
                    className="form-select"
                    value={selectedQuest || ""}
                    onChange={(e) => setSelectedQuest(e.target.value)}
                >
                    {quests.map((quest) => (
                        <option key={quest.id} value={String(quest.id)}>
                            {quest.title || `Quest #${quest.id}`}
                        </option>
                    ))}
                </select>
            </div>

            <div className="mb-2">
                <button
                    className="btn btn-success me-2"
                    onClick={handleCurrentQuest}
                    disabled={!selectedQuest}
                >
                    View Current Questionnaire
                </button>
            </div>

            <div className="mb-2">
                <button
                    className="btn btn-warning me-2"
                    onClick={handleFavorites}
                    disabled={!selectedQuest}
                >
                    View Favorites
                </button>
                <button
                    className="btn btn-dark"
                    onClick={handleBlacklist}
                    disabled={!selectedQuest}
                >
                    View Blacklist
                </button>
            </div>

            <div className="my-3">
                <button className="btn btn-primary" onClick={handleQuest}>
                    New Questionnaire
                </button>
            </div>

            <div className="my-3">
                <button
                    className="btn btn-info"
                    onClick={handleFriends}
                    disabled={!selectedQuest}
                >
                    Find Friends
                </button>
            </div>

            <div>
                <button className="btn btn-danger" onClick={handleLogout}>
                    Log out
                </button>
            </div>
        </div>
    );
}
