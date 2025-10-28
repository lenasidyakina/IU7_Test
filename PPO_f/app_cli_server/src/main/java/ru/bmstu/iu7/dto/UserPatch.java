package ru.bmstu.iu7.dto;

public class UserPatch {
    private String name;       // может быть null, если не обновляем
    private String password;   // может быть null
    private Integer age;       // Integer вместо int, чтобы null означал "не менять"
    private Boolean gender;    // Boolean вместо boolean, чтобы null означал "не менять"

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }
}
