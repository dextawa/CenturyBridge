package top.dext.centurybridge.rt.v1_20_2;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_4345;

/**
 * A Realms service failure as 1.20.1 described it. 1.20.1's RealmsServiceException carried the HTTP
 * status (httpResultCode), the response text (rawResponse) and, when that text was a JSON object, the
 * RealmsError parsed from it (its errorMsg and errorCode); 1.20.2's exception carries one RealmsError
 * value only. This record is that value for a failure a 1.20.1 mod builds (its RealmsServiceException
 * constructors, RealmsError.parse) and the 1.20.1 view of every exception a mod catches
 * (CbNetRealms.cbLegacyView).
 *
 * @param httpCode    1.20.1 httpResultCode; -1 for a bare RealmsError.parse result, which had none
 * @param rawResponse 1.20.1 rawResponse
 * @param errorMsg    the parsed RealmsError's errorMsg ("" when the JSON had none); null when 1.20.1
 *                    parsed no RealmsError (the exception's realmsError was null)
 * @param serviceCode the parsed RealmsError's errorCode (-1 when the JSON had none)
 */
@Environment(EnvType.CLIENT)
/* the service code component is not named errorCode: past the 26.1 wall RealmsError.method_21037 is errorCode()I, and a
 * record accessor of that name would be a duplicate method (ClassFormatError when the class loads) */
public record CbNetLegacyRealmsError(int httpCode, String rawResponse, String errorMsg, int serviceCode)
        implements class_4345 {

    /** whether 1.20.1 had a RealmsError object for this failure */
    public boolean parsed() {
        return errorMsg != null;
    }

    /** 1.20.2 errorCode(): the service's own code when it gave one, else the HTTP status (1.20.2's rule) */
    @Override
    public int method_21037() {
        return parsed() && serviceCode != -1 ? serviceCode : httpCode;
    }

    /**
     * 1.20.2 errorMessage(), what vanilla screens show: 1.20.1's RealmsGenericErrorScreen showed the
     * translated "mco.errorMessage.<code>" when the language had it, else the service's message, and
     * the raw response for a failure without a RealmsError.
     */
    @Override
    public class_2561 method_52657() {
        if (!parsed()) {
            return class_2561.method_30163(rawResponse);
        }
        String key = "mco.errorMessage." + serviceCode;
        return class_1074.method_4663(key) ? class_2561.method_43471(key) : class_2561.method_30163(errorMsg);
    }

    /**
     * 1.20.2 logMessage(), which 1.20.2's RealmsServiceException.getMessage() answers: 1.20.1's own
     * getMessage() text, so an exception a 1.20.1 mod throws reads as it did.
     */
    @Override
    public String method_21036() {
        if (!parsed()) {
            return String.format(Locale.ROOT, "Realms service error (%d) %s", httpCode, rawResponse);
        }
        String key = "mco.errorMessage." + serviceCode;
        String message = class_1074.method_4663(key) ? class_1074.method_4662(key) : errorMsg;
        return String.format(Locale.ROOT, "Realms service error (%d/%d) %s", httpCode, serviceCode, message);
    }
}
