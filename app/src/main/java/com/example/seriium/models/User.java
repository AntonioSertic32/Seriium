package com.example.seriium.models;

import java.util.List;

public class User {

    private String ime;
    private String prezime;
    private List<UserSerie> serije;

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public List<UserSerie> getSerije() {
        return serije;
    }

    public void setSerije(List<UserSerie> serije) {
        this.serije = serije;
    }

    public User() {}

    public User(String ime, String prezime, List<UserSerie> serije) {
        this.ime = ime;
        this.prezime = prezime;
        this.serije = serije;
    }

}
