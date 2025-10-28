package ru.bmstu.iu7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bmstu.iu7.API.model.AUser;
import ru.bmstu.iu7.impl.SpringUserRepository;
import ru.bmstu.iu7.impl.model.User;
import ru.bmstu.iu7.testdata.UserMother;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    SpringUserRepository m_springUserRepository;

    User u;
    AUser au;

    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        u = UserMother.lenaUser();
        au = UserMother.lenaAUser();
        userRepository = new UserRepository(m_springUserRepository);
    }


    @Test
    void createUser_success() throws Exception {
        Mockito.when(m_springUserRepository.save(Mockito.any(User.class))).thenReturn(u);

        AUser user = userRepository.createUser("lena", "1234", 45, true);

        assertEquals(au.getId(), user.getId());
    }

    @Test
    void createUser_failure() {
        Mockito.when(m_springUserRepository.save(Mockito.any(User.class)))
                .thenThrow(new RuntimeException("DB error"));


        assertThrows(RuntimeException.class,
                () -> userRepository.createUser("lena", "1234", 45, true));
    }

    @Test
    void createUser_invalidName_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userRepository.createUser("", "1234", 25, true));

        assertEquals("Name cannot be empty", exception.getMessage());
    }

    @Test
    void createUser_invalidAge_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userRepository.createUser("lena", "1234", -5, true));

        assertEquals("Age must be positive", exception.getMessage());
    }


    @Test
    void findUser_success() throws Exception {
        Mockito.when(m_springUserRepository.findByName("lena")).thenReturn(u);

        AUser user = userRepository.findUser("lena");

        assertEquals(au.getId(), user.getId());
    }

    @Test
    void findUser_notFound() throws Exception {
        Mockito.when(m_springUserRepository.findByName("lena")).thenReturn(null);

        AUser user = userRepository.findUser("lena");

        assertNull(user);
    }

    @Test
    void findUser_failure() {
        Mockito.when(m_springUserRepository.findByName("lena"))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class,
                () -> userRepository.findUser("lena"));
    }

    @Test
    void delete_success() throws Exception {
        userRepository.delete(1L);

        Mockito.verify(m_springUserRepository).deleteById(1L);
    }

    @Test
    void delete_failure() {
        Mockito.doThrow(new RuntimeException("DB error"))
                .when(m_springUserRepository).deleteById(1L);

        assertThrows(RuntimeException.class,
                () -> userRepository.delete(1L));
    }


    @Test
    void update_success() throws Exception {
        Mockito.when(m_springUserRepository.save(Mockito.any(User.class))).thenReturn(u);

        AUser result = userRepository.update(au);

        assertEquals(u.getId(), result.getId());
    }

    @Test
    void update_failure() {
        Mockito.when(m_springUserRepository.save(Mockito.any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class,
                () -> userRepository.update(au));
    }


    @Test
    void findAll_failure_returnsEmptyList() {
        Mockito.when(m_springUserRepository.findAll())
                .thenThrow(new RuntimeException("DB error"));

        List<AUser> result = userRepository.findAll();

        assertTrue(result.isEmpty());
    }
}
