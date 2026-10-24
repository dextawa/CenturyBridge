package top.dext.centurybridge.facade.v1_20_2.authlib;

/** authlib 4.0.43's com.mojang.authlib.yggdrasil.response.Response (gone in 5.0.47): a Yggdrasil answer's error fields */
public class Response {
    private String error;
    private String errorMessage;
    private String cause;

    public Response() {
    }

    public String getError() {
        return error;
    }

    public String getCause() {
        return cause;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    protected void setError(String error) {
        this.error = error;
    }

    protected void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    protected void setCause(String cause) {
        this.cause = cause;
    }
}
