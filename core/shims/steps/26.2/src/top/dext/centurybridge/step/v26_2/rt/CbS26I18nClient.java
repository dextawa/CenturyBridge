package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.locale.Language;

/**
 * 26.1.2 I18n.exists(key): {@code language.has(key)}, I18n's language being the one Language.inject set. 26.2's I18n keeps
 * only get(key, args), which reads Language.getInstance() (the same injected language, javap of both); exists asks it the
 * same question.
 */
@Environment(EnvType.CLIENT)
public final class CbS26I18nClient {
    private CbS26I18nClient() {}

    /** I18n.exists(String) */
    public static boolean exists(String key) {
        return Language.getInstance().has(key);
    }
}
