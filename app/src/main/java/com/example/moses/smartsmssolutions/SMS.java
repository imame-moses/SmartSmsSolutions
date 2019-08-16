package com.example.moses.smartsmssolutions;

public class SMS {

    private String username;
    private String password;
    private String sender;
    private String recipient;
    private String message;

    public SMS(User user, String sender, String recipients, String message){
        setUsername(user.getUsername());
        setPassword(user.getPassword());
        setSender(sender);
        setRecipient(recipients);
        setMessage(message);
    }

    public SMS(){}

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
