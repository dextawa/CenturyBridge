package top.dext.centurybridge.step.v1_21.rt;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import com.mojang.logging.LogUtils;
import net.minecraft.class_156;
import org.slf4j.Logger;

/**
 * Util: 1.20.6's Util.OS.openUrl(URL). Wired from s6world.json.
 */
public final class CbWorldUtil {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 1.20.6 Util.OS.openUrl(url). 1.21's openUri(uri) is 1.20.6's openUrl with a URI in place of the URL: the
     * same launcher command per platform (rundll32 url.dll,FileProtocolHandler / open / xdg-open), built from
     * the address's string, "file:" turned into "file://" for the last, the process's streams closed, a failure
     * logged. A URL that is no valid URI (1.20.6 passed its string as it was) runs 1.20.6's command here.
     */
    public static void openUrl(class_156.class_158 os, URL url) {
        try {
            os.method_673(url.toURI());
            return;
        } catch (URISyntaxException notAUri) {
            // below: 1.20.6's own command line
        }
        String s = url.toString();
        String[] command;
        if (os == class_156.class_158.field_1133) {
            command = new String[] {"rundll32", "url.dll,FileProtocolHandler", s};
        } else if (os == class_156.class_158.field_1137) {
            command = new String[] {"open", s};
        } else {
            command = new String[] {"xdg-open", "file".equals(url.getProtocol()) ? s.replace("file:", "file://") : s};
        }
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
        } catch (IOException e) {
            LOGGER.error("Couldn't open url '{}'", url, e);
        }
    }

    private CbWorldUtil() {
    }
}
