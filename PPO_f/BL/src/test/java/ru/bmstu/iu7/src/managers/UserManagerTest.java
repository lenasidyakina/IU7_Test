package ru.bmstu.iu7.src.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bmstu.iu7.API.IUserRepository;
import ru.bmstu.iu7.API.model.AUser;
import ru.bmstu.iu7.src.testdata.AUserMother;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith(MockitoExtension.class)
class UserManagerTest {

    @Mock
    IUserRepository i_user_repository;

    UserManager userManager;
    AUser lena;

    @BeforeEach
    void setUp() {
        userManager = new UserManager(i_user_repository);
        lena = AUserMother.lenaAUser();
    }

    @Test
    void register_success() throws Exception {
        when(i_user_repository.findUser("Lena")).thenReturn(null);
        when(i_user_repository.createUser("Lena", "Lena12345", 20, true)).thenReturn(lena);

        AUser result = userManager.register("Lena", "Lena12345", 20, true);

        assertNotNull(result);
        assertEquals("Lena", result.getName());
        verify(i_user_repository).findUser("Lena");
        verify(i_user_repository).createUser("Lena", "Lena12345", 20, true);
    }

    @Test
    void register_userAlreadyExists_returnsNull() throws Exception {
        when(i_user_repository.findUser("Lena")).thenReturn(lena);

        AUser result = userManager.register("Lena", "Lena12345", 20, true);

        assertNull(result);
        verify(i_user_repository).findUser("Lena");
        verify(i_user_repository, never()).createUser(anyString(), anyString(), anyInt(), anyBoolean());
    }

    @Test
    void register_exception_returnsNull() throws Exception {
        when(i_user_repository.findUser("Lena")).thenThrow(new RuntimeException("DB error"));

        AUser result = userManager.register("Lena", "Lena12345", 20, true);

        assertNull(result);
        verify(i_user_repository).findUser("Lena");
    }

    @Test
    void authorize_success() throws Exception {
        when(i_user_repository.findUser("Lena")).thenReturn(lena);

        AUser result = userManager.authorize("Lena", "Lena12345");

        assertNotNull(result);
        assertEquals("Lena", result.getName());
        verify(i_user_repository).findUser("Lena");
    }

    @Test
    void authorize_wrongPassword_returnsNull() throws Exception {
        when(i_user_repository.findUser("Lena")).thenReturn(lena);
        lena.setPassword("WrongPassword");

        AUser result = userManager.authorize("Lena", "Lena12345");

        assertNull(result);
        verify(i_user_repository).findUser("Lena");
    }

    @Test
    void authorize_userNotFound_returnsNull() throws Exception {
        when(i_user_repository.findUser("Lena")).thenReturn(null);

        AUser result = userManager.authorize("Lena", "Lena12345");

        assertNull(result);
        verify(i_user_repository).findUser("Lena");
    }

    @Test
    void authorize_exception_returnsNull() throws Exception {
        when(i_user_repository.findUser("Lena")).thenThrow(new RuntimeException("DB error"));

        AUser result = userManager.authorize("Lena", "Lena12345");

        assertNull(result);
        verify(i_user_repository).findUser("Lena");
    }

    @Test
    void delete_success() throws Exception {
        doNothing().when(i_user_repository).delete(1L);

        userManager.delete(1L);

        verify(i_user_repository).delete(1L);
    }

    @Test
    void delete_exception_doesNotThrow() throws Exception {
        doThrow(new RuntimeException("DB error")).when(i_user_repository).delete(1L);

        assertDoesNotThrow(() -> userManager.delete(1L));
        verify(i_user_repository).delete(1L);
    }
}
