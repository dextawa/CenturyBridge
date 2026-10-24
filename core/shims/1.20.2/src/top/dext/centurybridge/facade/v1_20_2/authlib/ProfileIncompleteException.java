package top.dext.centurybridge.facade.v1_20_2.authlib;

/** authlib 4.0.43's com.mojang.authlib.yggdrasil.ProfileIncompleteException (gone in 5.0.47) */
public class ProfileIncompleteException extends RuntimeException {
    public ProfileIncompleteException() {
    }

    public ProfileIncompleteException(String message) {
        super(message);
    }

    public ProfileIncompleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfileIncompleteException(Throwable cause) {
        super(cause);
    }
}
