import './App.css';
import { Routes, Route, BrowserRouter, Navigate } from "react-router-dom";
import { UserProvider, UserContext } from "./UserContext";
import Login from './Components/Login';
import Home from './Components/Home';
import Quest from "./Components/Quest";
import Friends from "./Components/Friends";
import AdminPanel from "./Components/AdminPanel";
import CensorPanel from "./Components/CensorPanel";
import Register from './Components/Register';
import QuestView from "./Components/QuestView";
import FavoritesView from "./Components/FavoritesView";
import BlacklistView from "./Components/BlacklistView";
import { useContext } from "react";

function ProtectedRoute({ children, roles }) {
    const { user } = useContext(UserContext);
    if (!user) return <Navigate to="/login" />;
    if (roles && !roles.includes(user.role)) return <Navigate to="/login" />;
    return children;
}

function App() {
    return (
        <UserProvider>
            <BrowserRouter>
                <div className="App">
                    <Routes>
                        <Route path="/login" element={<Login />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/home" element={
                            <ProtectedRoute roles={["user", "admin", "censor"]}>
                                <Home />
                            </ProtectedRoute>
                        }/>
                        <Route path="/quest" element={
                            <ProtectedRoute roles={["user"]}>
                                <Quest />
                            </ProtectedRoute>
                        }/>
                        <Route path="/friends" element={
                            <ProtectedRoute roles={["user"]}>
                                <Friends />
                            </ProtectedRoute>
                        }/>
                        <Route path="/admin" element={
                            <ProtectedRoute roles={["admin"]}>
                                <AdminPanel />
                            </ProtectedRoute>
                        }/>
                        <Route path="/censor" element={
                            <ProtectedRoute roles={["censor"]}>
                                <CensorPanel />
                            </ProtectedRoute>
                        }/>
                        <Route path="*" element={<Navigate to="/login" />} />
                        <Route path="/quest/:questId" element={
                            <ProtectedRoute roles={["user"]}>
                                <QuestView />
                            </ProtectedRoute>
                        }/>

                        <Route path="/friends/:questId" element={
                            <ProtectedRoute roles={["user"]}>
                                <Friends />
                            </ProtectedRoute>
                        }/>
                        <Route path="/favorites/:questId" element={
                            <ProtectedRoute roles={["user"]}>
                                <FavoritesView />
                            </ProtectedRoute>
                        } />

                        <Route path="/blacklist/:questId" element={
                            <ProtectedRoute roles={["user"]}>
                                <BlacklistView />
                            </ProtectedRoute>
                        } />

                    </Routes>
                </div>
            </BrowserRouter>
        </UserProvider>
    );
}

export default App;
