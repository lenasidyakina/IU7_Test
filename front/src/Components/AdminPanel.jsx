import { useEffect, useState } from "react";
import Backend from "../Backend";
import { useNavigate } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';

export default function AdminPanel() {
    const [quests, setQuests] = useState([]);
    const [selectedQuest, setSelectedQuest] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        Backend.getAllQuestionnaires()
            .then(resp => setQuests(resp.data))
            .catch(err => console.error(err));
    }, []);

    const deleteQuest = (id) => {
        Backend.deleteQuestionnaire(id).then(() =>
            setQuests(prev => prev.filter(q => q.id !== id))
        );
    };

    const logout = () => {
        Backend.logout().then(() => {
            navigate("/login");
        });
    };

    const openQuest = async (id) => {
        try {
            const [
                basic,
                black,
                fav,
                variant,
                extended,
                searchVariant,
                searchExtended
            ] = await Promise.all([
                Backend.getQuestBasic(id),
                Backend.getQuestBlackList(id),
                Backend.getQuestFavList(id),
                Backend.getQuestVariantAnswers(id),
                Backend.getQuestExtendedAnswers(id),
                Backend.getSearchVariantAnswers(id),
                Backend.getSearchExtendedAnswers(id)
            ]);

            setSelectedQuest({
                ...basic.data,
                black_list: black.data,
                fav_list: fav.data,
                variant_answers: variant.data,
                extended_answers: extended.data,
                search_variant_answers: searchVariant.data,
                search_extended_answers: searchExtended.data
            });
        } catch (err) {
            console.error(err);
        }
    };

    const closeModal = () => setSelectedQuest(null);

    return (
        <div className="container mt-3">
            <h2>Admin Panel</h2>
            <button className="btn btn-secondary btn-sm mb-3" onClick={logout}>Log out</button>

            <table className="table table-sm">
                <thead>
                <tr>
                    <th>Quest ID</th>
                    <th>Censored</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {quests.map(q => (
                    <tr key={q.id}>
                        <td>
                            <button className="btn btn-link p-0" onClick={() => openQuest(q.id)}>
                                {q.id}
                            </button>
                        </td>
                        <td>{q.censored ? "Yes" : "No"}</td>
                        <td>
                            <button className="btn btn-danger btn-sm" onClick={() => deleteQuest(q.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {selectedQuest && (
                <div className="modal show d-block" tabIndex="-1">
                    <div className="modal-dialog modal-lg">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Quest ID: {selectedQuest.id}</h5>
                                <button type="button" className="btn-close" onClick={closeModal}></button>
                            </div>
                            <div className="modal-body">
                                {/* Информация о пользователе */}
                                {selectedQuest.user && (
                                    <div className="mb-3">
                                        <h6>User Info:</h6>
                                        <p>Name: {selectedQuest.user.name}</p>
                                        <p>Age: {selectedQuest.user.age}</p>
                                        <p>Gender: {selectedQuest.user.gender ? "Male" : "Female"}</p>
                                    </div>
                                )}

                                {/* Черный список */}
                                <div className="mb-3">
                                    <h6>Black List:</h6>
                                    <ul>
                                        {selectedQuest.black_list?.map(b => (
                                            <li key={b.id}>
                                                <button className="btn btn-link p-0" onClick={() => openQuest(b.id)}>
                                                    Quest {b.id}
                                                </button>
                                            </li>
                                        ))}
                                    </ul>
                                </div>

                                {/* Избранное */}
                                <div className="mb-3">
                                    <h6>Favorites:</h6>
                                    <ul>
                                        {selectedQuest.fav_list?.map(f => (
                                            <li key={f.id}>
                                                <button className="btn btn-link p-0" onClick={() => openQuest(f.id)}>
                                                    Quest {f.id}
                                                </button>
                                            </li>
                                        ))}
                                    </ul>
                                </div>

                                {/* Вариантные ответы себя */}
                                <div className="mb-3">
                                    <h6>Variant Answers (Self):</h6>
                                    <ul>
                                        {selectedQuest.variant_answers?.map((v, idx) => (
                                            <li key={idx}>
                                                Q: {v.question?.question} | Answer: {v.tag?.name} | Weight: {v.weight}
                                            </li>
                                        ))}
                                    </ul>
                                </div>

                                {/* Развёрнутые ответы себя */}
                                <div className="mb-3">
                                    <h6>Extended Answers (Self):</h6>
                                    <ul>
                                        {selectedQuest.extended_answers?.map((e, idx) => (
                                            <li key={idx}>
                                                Q: {e.question?.question} | Answer: {e.answer} | Weight: {e.weight}
                                            </li>
                                        ))}
                                    </ul>
                                </div>

                                {/* Вариантные ответы другого человека */}
                                <div className="mb-3">
                                    <h6>Variant Answers (Other):</h6>
                                    <ul>
                                        {selectedQuest.search_variant_answers?.map((v, idx) => (
                                            <li key={idx}>
                                                Q: {v.question?.question} | Answer: {v.tag?.name} | Weight: {v.weight}
                                            </li>
                                        ))}
                                    </ul>
                                </div>

                                {/* Развёрнутые ответы другого человека */}
                                <div className="mb-3">
                                    <h6>Extended Answers (Other):</h6>
                                    <ul>
                                        {selectedQuest.search_extended_answers?.map((e, idx) => (
                                            <li key={idx}>
                                                Q: {e.question?.question} | Answer: {e.answer} | Weight: {e.weight}
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button className="btn btn-secondary" onClick={closeModal}>Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
