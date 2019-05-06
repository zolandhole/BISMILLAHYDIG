package com.notio.ydig.models;

public class ModelUser {
    private int id;
    private String sumber_login, id_user, password;

    public ModelUser() {
    }

    public ModelUser(int id, String sumber_login, String id_user, String password) {
        this.id = id;
        this.sumber_login = sumber_login;
        this.id_user = id_user;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSumber_login() {
        return sumber_login;
    }

    public void setSumber_login(String sumber_login) {
        this.sumber_login = sumber_login;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
