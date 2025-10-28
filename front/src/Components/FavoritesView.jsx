import { useEffect, useState } from "react";
import Backend from "../Backend";
import { useParams, useNavigate } from "react-router-dom";

export default function FavoritesView() {
    const { questId } = useParams();
    const nav = useNavigate();
    const [favorites, setFavorites] = useState([]);
    const [selectedQuest, setSelectedQuest] = useState(null);

    useEffect(() => {
        Backend.getQuestFavList(questId)
            .then(resp => setFavorites(resp.data))
            .catch(err => console.error(err));
    }, [questId]);

    const removeFavorite = (favId) => {
        Backend.delFav(questId, favId)
            .then(() => setFavorites(prev => prev.filter(f => f.id !== favId)))
            .catch(err => console.error(err));
    };

    const openQuest = async (id) => {
        try {
            const [
                basic,
                variant,
                extended,
                searchVariant,
                searchExtended
            ] = await Promise.all([
                Backend.getQuestBasic(id),
                Backend.getQuestVariantAnswers(id),
                Backend.getQuestExtendedAnswers(id),
                Backend.getSearchVariantAnswers(id),
                Backend.getSearchExtendedAnswers(id)
            ]);

            setSelectedQuest({
                ...basic.data,
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
            <h2>Favorites for Quest #{questId}</h2>
            <button className="btn btn-secondary mb-3" onClick={() => nav("/home")}>
                Back to Home
            </button>

            <table className="table table-sm">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Age</th>
                    <th>Gender</th>
                    <th>Quest</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {favorites.map(fav => (
                    <tr key={fav.id}>
                        <td>{fav.user?.name || `Quest #${fav.id}`}</td>
                        <td>{fav.user?.age || "-"}</td>
                        <td>{fav.user?.gender ? "Male" : "Female"}</td>
                        <td>
                            <button
                                className="btn btn-link p-0"
                                onClick={() => openQuest(fav.id)}
                            >
                                Open
                            </button>
                        </td>
                        <td>
                            <button
                                className="btn btn-danger btn-sm"
                                onClick={() => removeFavorite(fav.id)}
                            >
                                Remove
                            </button>
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
                                <h5 className="modal-title">
                                    {selectedQuest.user?.name || "Quest Info"}
                                </h5>
                                <button
                                    type="button"
                                    className="btn-close"
                                    onClick={closeModal}
                                ></button>
                            </div>
                            <div className="modal-body">
                                {selectedQuest.user && (
                                    <div className="mb-3">
                                        <h6>User Info:</h6>
                                        <p>Name: {selectedQuest.user.name}</p>
                                        <p>Age: {selectedQuest.user.age}</p>
                                        <p>Gender: {selectedQuest.user.gender ? "Male" : "Female"}</p>
                                    </div>
                                )}

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
                                <button className="btn btn-secondary" onClick={closeModal}>
                                    Close
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
