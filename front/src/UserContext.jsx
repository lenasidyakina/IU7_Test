import { createContext, useState, useEffect } from "react";
import Backend from "./Backend";

export const UserContext = createContext();

export function UserProvider({ children }) {
    const [user, setUser] = useState(null);

    useEffect(() => {
        // при монтировании пробуем восстановить пользователя из localStorage
        const storedUser = localStorage.getItem("user");
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
    }, []);

    const login = async (username, password) => {
        const resp = await Backend.login(username, password);
        setUser(resp.data);
        localStorage.setItem("user", JSON.stringify(resp.data));
        return resp.data;
    };

    const logout = async () => {
        await Backend.logout();
        setUser(null);
        localStorage.removeItem("user");
    };

    const register = async (username, password) => {
        const resp = await Backend.register(username, password);
        setUser(resp.data);
        localStorage.setItem("user", JSON.stringify(resp.data));
        return resp.data;
    };

    return (
        <UserContext.Provider value={{ user, setUser, login, logout, register }}>
            {children}
        </UserContext.Provider>
    );
}
