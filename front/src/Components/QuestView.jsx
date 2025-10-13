import { useEffect, useState } from "react";
import Backend from "../Backend";
import { useParams, useNavigate } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';

export default function QuestView() {
    const { questId } = useParams();
    const navigate = useNavigate();

    const [quest, setQuest] = useState(null);

    useEffect(() => {
        if (!questId) return;

        const fetchQuest = async () => {
            try {
                const [
                    basic,
                    variant,
                    extended,
                    searchVariant,
                    searchExtended
                ] = await Promise.all([
                    Backend.getQuestBasic(questId),
                    Backend.getQuestVariantAnswers(questId),
                    Backend.getQuestExtendedAnswers(questId),
                    Backend.getSearchVariantAnswers(questId),
                    Backend.getSearchExtendedAnswers(questId)
                ]);

                setQuest({
                    ...basic.data,
                    variant_answers: variant.data,
                    extended_answers: extended.data,
                    search_variant_answers: searchVariant.data,
                    search_extended_answers: searchExtended.data
                });
            } catch (err) {
                console.error("Error loading quest:", err);
            }
        };

        fetchQuest();
    }, [questId]);

    const goBack = () => navigate("/home");

    if (!quest) return <div className="container mt-3">Loading...</div>;

    return (
        <div className="container mt-3">
            <h2>Questionnaire #{quest.id}</h2>

            {quest.user && (
                <div className="card p-3 mb-4">
                    <h4>{quest.user.name}</h4>
                    <p><strong>Age:</strong> {quest.user.age}</p>
                    <p><strong>Gender:</strong> {quest.user.gender ? "Male" : "Female"}</p>
                </div>
            )}

            <div className="mb-3">
                <h5>Variant Answers (Self)</h5>
                <ul className="list-group">
                    {quest.variant_answers?.map((v, idx) => (
                        <li key={idx} className="list-group-item">
                            Q: {v.question?.question} | Answer: {v.tag?.name} | Weight: {v.weight}
                        </li>
                    ))}
                </ul>
            </div>

            <div className="mb-3">
                <h5>Extended Answers (Self)</h5>
                <ul className="list-group">
                    {quest.extended_answers?.map((e, idx) => (
                        <li key={idx} className="list-group-item">
                            Q: {e.question?.question} | Answer: {e.answer} | Weight: {e.weight}
                        </li>
                    ))}
                </ul>
            </div>

            <div className="mb-3">
                <h5>Variant Answers (Other)</h5>
                <ul className="list-group">
                    {quest.search_variant_answers?.map((v, idx) => (
                        <li key={idx} className="list-group-item">
                            Q: {v.question?.question} | Answer: {v.tag?.name} | Weight: {v.weight}
                        </li>
                    ))}
                </ul>
            </div>

            <div className="mb-3">
                <h5>Extended Answers (Other)</h5>
                <ul className="list-group">
                    {quest.search_extended_answers?.map((e, idx) => (
                        <li key={idx} className="list-group-item">
                            Q: {e.question?.question} | Answer: {e.answer} | Weight: {e.weight}
                        </li>
                    ))}
                </ul>
            </div>

            <button className="btn btn-secondary" onClick={goBack}>Back to Home</button>
        </div>
    );
}
