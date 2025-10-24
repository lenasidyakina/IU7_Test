package ru.bmstu.iu7.impl.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Questionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne()
    User user;
    @OneToMany(fetch = FetchType.EAGER)
    List<Questionnaire> black_list;
    @OneToMany(fetch = FetchType.EAGER)
    //@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Questionnaire> fav_list;

    @ManyToOne(cascade = CascadeType.ALL)
    Information information;
    @ManyToOne(cascade = CascadeType.ALL)
    Information search_information;


    boolean censored = Boolean.FALSE;

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) { this.id = id; }
    public User getUser() {
        return this.user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Information getInformation() {
        return this.information;
    }
    public void setInformation(Information information) {
        this.information = information;
    }
    public Information getSearchInformation() {
        return this.search_information;
    }
    public void setSearchInformation(Information searchInformation) {
        this.search_information = searchInformation;
    }

    public boolean isCensored() {
        return this.censored;
    }
    public void setCensored(boolean censored) {
        this.censored = censored;
    }
    public List<Questionnaire> getBlackList() {
        return this.black_list;
    }

    public void setBlackList(List<Questionnaire> blackList) {
        this.black_list = (List<Questionnaire>)blackList;
    }

    public List<Questionnaire> getFavList() {
        return this.fav_list;
    }

    public void setFavList(List<Questionnaire> favList) {
        this.fav_list = favList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Questionnaire that = (Questionnaire) o;
        return id == that.id;
    }

    @Override
    public String toString() {
        return information.toString();
    }

    public void addBlack(Questionnaire black) {
        this.black_list.add( black);
    }

    public void addFav(Questionnaire fav) {
        this.fav_list.add( fav);
    }
}
