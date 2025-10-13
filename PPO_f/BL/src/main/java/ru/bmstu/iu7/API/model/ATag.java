package ru.bmstu.iu7.API.model;


import java.util.Objects;

public class ATag {
    Long id;
    String name;
    public ATag(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public ATag(String name) {
        this.name = name;
    }

    public ATag() {

    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ATag tag = (ATag) o;
        if (!Objects.equals(id, tag.id)) return false;
        return Objects.equals(name, tag.name);
    }
    public int hashCode() {
        return Objects.hash(name);
    }
}
