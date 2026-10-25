package top.dext.centurybridge.step.v1_21.rt;

/**
 * Runtime side of the Util.OS route (CbUtilOsMixin): 1.21's getOpenUriArguments(URI) ask, through the restored 1.20.6
 * getOpenUrlArguments(URL) when a URL stands for the URI with the same spelling (the two bodies then give the same strings).
 */
public final class CbUtilOs {
    private CbUtilOs() {
    }

    /** Util.OS with 1.20.6's getOpenUrlArguments(URL) restored on it (and on its overriding constants) */
    public interface Restored {
        String[] method_674(java.net.URL url);
    }

    /** the URL standing for the URI with the same spelling, or null */
    public static java.net.URL sameUrl(java.net.URI uri) {
        try {
            java.net.URL url = uri.toURL();
            return url.toString().equals(uri.toString()) ? url : null;
        } catch (java.net.MalformedURLException | IllegalArgumentException e) {
            return null;
        }
    }
}
