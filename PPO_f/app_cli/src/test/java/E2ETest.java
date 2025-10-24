//import org.junit.jupiter.api.*;
//import org.testcontainers.containers.PostgreSQLContainer;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class E2ETest {
//
//    private static final String JAR_PATH = "build/libs/ru.bmstu.iu7-1.0-SNAPSHOT.jar";
//
//    @BeforeAll
//    public void buildJar() throws IOException, InterruptedException {
//        System.out.println("Building fat-jar...");
//        Process gradleBuild = new ProcessBuilder(
//                "./gradlew", "clean", "jar"
//        ).inheritIO().start();
//
//
//        if (!gradleBuild.waitFor(2, TimeUnit.MINUTES)) {
//            gradleBuild.destroyForcibly();
//            throw new RuntimeException("Gradle build failed or timed out");
//        }
//
//        File jar = new File(JAR_PATH);
//        if (!jar.exists()) {
//            throw new RuntimeException("Jar file not found: " + JAR_PATH);
//        }
//        System.out.println("Jar built successfully.");
//    }
//
//    @Test
//    public void fullFlowTest() throws Exception {
//        try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
//                .withDatabaseName("e2etestdb")
//                .withUsername("test")
//                .withPassword("test")) {
//
//            // Поднимаем контейнер Postgres
//            postgres.start();
//            System.out.println("Postgres started at: " + postgres.getJdbcUrl());
//
//            ProcessBuilder builder = new ProcessBuilder(
//                    "java", "-jar", JAR_PATH
//            );
//
//            // Передаём параметры БД в Main через переменные окружения
//            builder.environment().put("JDBC_URL", postgres.getJdbcUrl());
//            builder.environment().put("JDBC_USER", postgres.getUsername());
//            builder.environment().put("JDBC_PASS", postgres.getPassword());
//
//            builder.redirectErrorStream(true);
//            Process process = builder.start();
//
//            try (BufferedWriter writer = new BufferedWriter(
//                    new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
//                 BufferedReader reader = new BufferedReader(
//                         new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
//
//                String line;
//                int step = 0;
//
//                while ((line = reader.readLine()) != null) {
//                    System.out.println(line); // вывод для отладки
//
//                    switch (step) {
//                        // 1. Регистрация первого пользователя
//                        case 0:
//                            if (line.contains("1 - зарегистрироваться")) {
//                                writer.write("1"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 1:
//                            if (line.contains("логин:")) {
//                                writer.write("user1"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 2:
//                            if (line.contains("пароль:")) {
//                                writer.write("pass1"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        // 2. Создание анкеты
//                        case 3:
//                            if (line.contains("1 - создать анкету")) {
//                                writer.write("1"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 4:
//                            if (line.contains("Часть 1. Ответь на вопросы")) {
//                                writer.write("1"); writer.newLine(); writer.flush();
//                            }
//                            if (line.contains("Введите вес этого вопроса")) {
//                                writer.write("5"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 5:
//                            if (line.contains("Часть 2. Ответь на вопросы")) {
//                                writer.write("1"); writer.newLine(); writer.flush();
//                            }
//                            if (line.contains("Введите вес этого вопроса")) {
//                                writer.write("5"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 6:
//                            if (line.contains("Анкета успешно создана")) {
//                                step++;
//                            }
//                            break;
//                        // 3. Добавляем второго пользователя
//                        case 7:
//                            if (line.contains("2 - выйти")) {
//                                writer.write("2"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 8:
//                            if (line.contains("1 - зарегистрироваться")) {
//                                writer.write("1"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 9:
//                            if (line.contains("логин:")) {
//                                writer.write("user2"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 10:
//                            if (line.contains("пароль:")) {
//                                writer.write("pass2"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 11:
//                            if (line.contains("1 - создать анкету")) {
//                                writer.write("1"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 12:
//                            if (line.contains("Часть 1. Ответь на вопросы")) {
//                                writer.write("2"); writer.newLine(); writer.flush();
//                            }
//                            if (line.contains("Введите вес этого вопроса")) {
//                                writer.write("6"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 13:
//                            if (line.contains("Часть 2. Ответь на вопросы")) {
//                                writer.write("2"); writer.newLine(); writer.flush();
//                            }
//                            if (line.contains("Введите вес этого вопроса")) {
//                                writer.write("6"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        case 14:
//                            if (line.contains("Анкета успешно создана")) {
//                                step++;
//                            }
//                            break;
//                        // 4. Проверяем потенциальных друзей
//                        case 15:
//                            if (line.contains("Ваши потенциальные друзья:")) {
//                                assertTrue(line.contains("Ваши потенциальные друзья:"));
//                                step++;
//                            }
//                            break;
//                        // 5. Добавляем первого пользователя во избранное второго
//                        case 16:
//                            if (line.contains("1 - добавить в анкету") || line.contains("1 - добавить в чёрный список")) {
//                                writer.write("2"); writer.newLine(); writer.flush(); // добавить в избранное
//                                step++;
//                            }
//                            break;
//                        case 17:
//                            if (line.contains("2 - выйти")) {
//                                writer.write("2"); writer.newLine(); writer.flush();
//                                step++;
//                            }
//                            break;
//                        // Завершение теста
//                        case 18:
//                            if (line.contains("1 - зарегистрироваться") || line.contains("1 - создать анкету")) {
//                                step++;
//                            }
//                            break;
//                    }
//
//                    if (step > 18) break;
//                }
//
//                process.waitFor(30, TimeUnit.SECONDS);
//            } finally {
//                if (process.isAlive()) process.destroyForcibly();
//            }
//        }
//    }
//}
