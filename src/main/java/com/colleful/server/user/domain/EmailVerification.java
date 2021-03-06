package com.colleful.server.user.domain;

public class EmailVerification {

    private final String email;
    private final int code;
    private boolean isChecked;

    public EmailVerification(String email, int code) {
        this.email = email;
        this.code = code;
        this.isChecked = false;
    }

    public EmailVerification(String serialized) {
        String[] fields = serialized.split(":");
        this.email = fields[0];
        this.code = Integer.parseInt(fields[1]);
        this.isChecked = Boolean.parseBoolean(fields[2]);
    }

    public String getEmail() {
        return email;
    }

    public boolean verify(int code) {
        return this.code == code;
    }

    public boolean isNotChecked() {
        return !this.isChecked;
    }

    public void check() {
        this.isChecked = true;
    }

    @Override
    public String toString() {
        return email + ":" + code + ":" + isChecked;
    }
}
