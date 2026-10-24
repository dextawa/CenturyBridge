package top.dext.centurybridge.facade.v1_20_2.authlib;

import com.mojang.authlib.exceptions.InvalidCredentialsException;

/** authlib 4.0.43's com.mojang.authlib.exceptions.UserMigratedException (gone in 5.0.47) */
public class UserMigratedException extends InvalidCredentialsException {
    public UserMigratedException() {
    }

    public UserMigratedException(String message) {
        super(message);
    }

    public UserMigratedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserMigratedException(Throwable cause) {
        super(cause);
    }
}
