package com.sunbasedata.intern.customer_table;

public class LoginDetails {
    private String login_id;
    private String password;
    public String getLogin_id() {
        return login_id;
    }
    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public LoginDetails(String login_id, String password) {
        this.login_id = login_id;
        this.password = password;
    }
    public LoginDetails() {
    }
    
}
