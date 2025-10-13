import { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../UserContext";

export default function Login() {
    const { login, register } = useContext(UserContext);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const nav = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            const user = await register(username, password);
            if (user.role === "admin") nav("/admin");
            else if (user.role === "censor") nav("/censor");
            else nav("/home");
        } catch (err) { alert("Registration error") }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const user = await login(username, password);
            if (user.role === "admin") nav("/admin");
            else if (user.role === "censor") nav("/censor");
            else nav("/home");
        } catch (err) { alert("Authorization error") }
    };

    return (
        <div className="d-flex justify-content-center align-items-center vh-100">
            <div className="col-md-4">
                <h2 className="text-center mb-4">Login</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-group mb-3">
                        <label>User</label>
                        <input
                            type="text"
                            className="form-control"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                            autoComplete="new-text"
                        />
                    </div>
                    <div className="form-group mb-3">
                        <label>Password</label>
                        <input
                            type="password"
                            className="form-control"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            autoComplete="new-password"
                        />
                    </div>
                    <div className="form-group text-center">
                        <button type="submit" className="btn btn-primary">Log in</button>
                        <button
                            type="button"
                            className="btn btn-secondary ms-2"
                            onClick={() => nav("/register")}
                        >
                            Register
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
