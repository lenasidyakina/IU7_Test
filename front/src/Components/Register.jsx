import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Backend from "../Backend";

export default function Register() {
    const [username, setUsername] = useState("");
    const [age, setAge] = useState("");
    const [password, setPassword] = useState("");
    const [gender, setGender] = useState("male");
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Проверка возраста
        const numericAge = parseInt(age, 10);
        if (isNaN(numericAge) || numericAge < 0 || numericAge > 200) {
            alert("Please enter a valid age between 0 and 200");
            return;
        }

        try {
            const genderBool = gender === "male";
            await Backend.register(username, password, numericAge, genderBool);

            alert("Registration successful!");
            navigate("/login");
        } catch (err) {
            console.error(err);
            alert("Registration error");
        }
    };

    return (
        <div className="d-flex justify-content-center align-items-center vh-100">
            <div className="col-md-4">
                <h2 className="text-center mb-4">Register</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-group mb-3">
                        <label>Name</label>
                        <input
                            type="text"
                            className="form-control"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                            autoComplete="off"
                            required
                        />
                    </div>

                    <div className="form-group mb-3">
                        <label>Age</label>
                        <input
                            type="number"
                            className="form-control"
                            value={age}
                            onChange={e => setAge(e.target.value)}
                            min={0}
                            max={200}
                            required
                        />
                    </div>

                    <div className="form-group mb-3">
                        <label>Gender</label>
                        <select
                            className="form-control"
                            value={gender}
                            onChange={e => setGender(e.target.value)}
                            required
                        >
                            <option value="male">Male</option>
                            <option value="female">Female</option>
                        </select>
                    </div>

                    <div className="form-group mb-3">
                        <label>Password</label>
                        <input
                            type="password"
                            className="form-control"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            autoComplete="new-password"
                            required
                        />
                    </div>

                    <div className="text-center">
                        <button type="submit" className="btn btn-primary mt-2">Save</button>
                    </div>
                </form>
            </div>
        </div>
    );
}
