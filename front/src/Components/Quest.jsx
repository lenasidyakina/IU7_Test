import {useNavigate} from "react-router-dom";
import Backend from "../Backend";
import {useState, useEffect} from 'react';

export default function Quest() {
    const nav = useNavigate();
    const [questions, setQuestions] = useState([]);
    const [otherQuestions, setOtherQuestions] = useState([]);
    const [answers, setAnswers] = useState([]);

    useEffect(() => {
        Backend.getQuestions()
            .then(resp => {
                setQuestions(resp.data);
                setOtherQuestions(resp.data);
            })
            .catch(err => {
                //alert("Logout error")
            });
    }, []);

    function handleSubmit(e) {
        const answerArray = [];
        let inputElements = document.getElementsByClassName('answer');

        for (let i = 0; i < inputElements.length; i++) {
            const input = inputElements[i];
            const ansType = input.tagName === 'INPUT' ? 'EXT' : 'VAR';
            answerArray.push([ansType, input.value, undefined, undefined, undefined]);
        }

        inputElements = document.getElementsByClassName('weight');
        for (let i = 0; i < inputElements.length; i++) {
            const input = inputElements[i];
            answerArray[i][2] = input.value;
        }

        inputElements = document.getElementsByClassName('answer-other');
        for (let i = 0; i < inputElements.length; i++) {
            const input = inputElements[i];
            answerArray[i][3] = input.value;
        }

        inputElements = document.getElementsByClassName('weight-other');
        for (let i = 0; i < inputElements.length; i++) {
            const input = inputElements[i];
            answerArray[i][4] = input.value;
        }

        Backend.submitQuest(answerArray)
            .then(resp => {
                nav("/home");
            })
            .catch(err => {
                alert("Backend is not available");
            });
    }

    return (
        <div>
            <h2>Your questionnaire</h2>
            <div className="m-4">
                <div className="row my-2 me-0">
                    <table className="table table-sm">
                        <thead className="thead-light">
                        <tr>
                            <th>Question</th>
                            <th>Answer</th>
                            <th>Weight</th>
                        </tr>
                        </thead>
                        <tbody>
                        {questions && questions.map((question, index) =>
                            <tr key={index}>
                                <td>{question.question}</td>
                                <td>
                                    {/* Если расширенный вопрос — текстовое поле */}
                                    {question.is_extended && (
                                        <input
                                            type="text"
                                            className="answer form-control"
                                            id={"input" + index}
                                        />
                                    )}

                                    {/* Если НЕ расширенный — выбор из тегов */}
                                    {!question.is_extended && (
                                        <select className="answer form-select" id={"select" + index}>
                                            {question.tags && question.tags.map((tag, idx) =>
                                                <option key={idx} value={tag.name}>{tag.name}</option>
                                            )}
                                        </select>
                                    )}
                                </td>
                                <td>
                                    <select className="weight form-select" id={"selectw" + index}>
                                        {[...Array(10)].map((_, i) =>
                                            <option key={i + 1} value={i + 1}>{i + 1}</option>
                                        )}
                                    </select>
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>

            <h2>Other questionnaire</h2>
            <div className="m-4">
                <div className="row my-2 me-0">
                    <table className="table table-sm">
                        <thead className="thead-light">
                        <tr>
                            <th>Question</th>
                            <th>Answer</th>
                            <th>Weight</th>
                        </tr>
                        </thead>
                        <tbody>
                        {otherQuestions && otherQuestions.map((question, index) =>
                            <tr key={index}>
                                <td>{question.question}</td>
                                <td>
                                    {question.is_extended && (
                                        <input
                                            type="text"
                                            className="answer-other form-control"
                                            id={"input2" + index}
                                        />
                                    )}

                                    {!question.is_extended && (
                                        <select className="answer-other form-select" id={"select2" + index}>
                                            {question.tags && question.tags.map((tag, idx) =>
                                                <option key={idx} value={tag.name}>{tag.name}</option>
                                            )}
                                        </select>
                                    )}
                                </td>
                                <td>
                                    <select className="weight-other form-select" id={"selectw" + index}>
                                        {[...Array(10)].map((_, i) =>
                                            <option key={i + 1} value={i + 1}>{i + 1}</option>
                                        )}
                                    </select>
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>

            <div>
                <button className="btn btn-primary" onClick={handleSubmit}>Submit</button>
            </div>
        </div>
    );
}
