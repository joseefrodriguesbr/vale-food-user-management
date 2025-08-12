package br.inatel.pos.dm111.vfu.api.core;

public enum AppErrorCode {

    CONFLICTED_USER_EMAIL("user.email.conflicted", "Provided email is already in use!", 409);

    private String code;
    private String message;
    private int status;

    AppErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
