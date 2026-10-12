package top.dext.centurybridge.step.v1_20_3.rt;

/**
 * 1.20.2's signed-chat API on 1.20.3: SignedMessageChain.Decoder's REJECT_ALL and unsigned(UUID), and
 * SignedMessageValidator's boolean contract (1.20.3 answers the validated message, or null when it is
 * not valid).
 */
public final class CbChat {

    // ------------------------------------------------------------------ SignedMessageChain.Decoder (class_7610$class_7611)

    /**
     * was SignedMessageChain.Decoder.REJECT_ALL (field_40848), 1.20.2's: every message fails to decode with
     * "chat.disabled.missingProfileKey" (not a chain break)
     */
    public static final net.minecraft.class_7610.class_7611 field_40848 = (signature, body) -> {
        throw new net.minecraft.class_7610.class_7825(net.minecraft.class_2561.method_43471("chat.disabled.missingProfileKey"), false);
    };

    /**
     * Decoder.unsigned(UUID) of 1.20.2 accepted every unsigned message; 1.20.3's takes whether secure chat is
     * enforced and rejects then, as REJECT_ALL did: never, for the 1.20.2 call.
     */
    public static java.util.function.BooleanSupplier cbNotEnforcing() {
        return () -> false;
    }

    // ------------------------------------------------------------------ SignedMessageValidator (class_7615)

    /** 1.20.2's SignedMessageValidator: whether the message is valid (a mod's validator, or its lambda) */
    @FunctionalInterface
    public interface CbLegacyValidator {
        boolean method_45048(net.minecraft.class_7471 message);
    }

    /** a 1.20.2 call: valid when 1.20.3's validator answers the message */
    public static boolean cbValid(net.minecraft.class_7471 validated) {
        return validated != null;
    }

    /** 1.20.3's updateAndValidate on a 1.20.2 validator: the message when it says valid, else null */
    public static net.minecraft.class_7471 cbValidated(CbLegacyValidator validator, net.minecraft.class_7471 message) {
        return validator.method_45048(message) ? message : null;
    }

    /** a 1.20.2 validator lambda as 1.20.3's validator */
    public static net.minecraft.class_7615 cbValidator(CbLegacyValidator validator) {
        return message -> cbValidated(validator, message);
    }

    private CbChat() {
    }
}
