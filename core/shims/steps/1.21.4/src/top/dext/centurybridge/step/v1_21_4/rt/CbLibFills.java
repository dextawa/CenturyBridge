package top.dext.centurybridge.step.v1_21_4.rt;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.op.ReadOnlyOIDCProviderMetadata;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.Kernel32;
import java.util.Collections;
import java.util.List;
import oshi.hardware.CentralProcessor;

/**
 * Methods the 1.21.4 libraries' interfaces newly demand of an implementation (core/segments/damage-lib/1.21.3__1.21.4.tsv,
 * kind ABSTRACT; JOML's are rt/CbLibJomlFills), answered for a 1.21.3-era mod implementation as the 1.21.3 library
 * knew the situation:
 * <ul>
 * <li>OSHI 6.6.5 CentralProcessor.getFeatureFlags(): 6.4.10 read no CPU feature flags -- none (the empty list 6.6.5's own
 * AbstractCentralProcessor answers when its counts carry none).</li>
 * <li>oauth2-oidc-sdk 11.20.1 AuthenticationResponse.getIssuer / getJWTResponse / getResponseMode: 11.9.1's
 * AuthenticationResponse carried no issuer, no JWT-secured (JARM) form and no explicit response mode -- each absent (null,
 * the SDK's own "not present"). ReadOnlyOIDCProviderMetadata.supportsNativeSSO(): 11.9.1's metadata had no native SSO
 * support to declare -- false.</li>
 * <li>JNA platform 5.15 X11.XMoveWindow / XResizeWindow / XMoveResizeWindow / XRaiseWindow / XLowerWindow and Kernel32
 * .IsProcessorFeaturePresent: native functions of the bound library; a hand-written implementation of the binding that
 * predates them reaches them through JNA's own binding of the library (X11.INSTANCE, Kernel32.INSTANCE).</li>
 * </ul>
 */
public final class CbLibFills {
    private CbLibFills() {
    }

    public static List<String> getFeatureFlags(CentralProcessor self) {
        return Collections.emptyList();
    }

    public static Issuer getIssuer(AuthenticationResponse self) {
        return null;
    }

    public static JWT getJWTResponse(AuthenticationResponse self) {
        return null;
    }

    public static ResponseMode getResponseMode(AuthenticationResponse self) {
        return null;
    }

    public static boolean supportsNativeSSO(ReadOnlyOIDCProviderMetadata self) {
        return false;
    }

    public static int XMoveWindow(X11 self, X11.Display display, X11.Window window, int x, int y) {
        return X11.INSTANCE.XMoveWindow(display, window, x, y);
    }

    public static int XResizeWindow(X11 self, X11.Display display, X11.Window window, int width, int height) {
        return X11.INSTANCE.XResizeWindow(display, window, width, height);
    }

    public static int XMoveResizeWindow(X11 self, X11.Display display, X11.Window window, int x, int y, int width, int height) {
        return X11.INSTANCE.XMoveResizeWindow(display, window, x, y, width, height);
    }

    public static int XRaiseWindow(X11 self, X11.Display display, X11.Window window) {
        return X11.INSTANCE.XRaiseWindow(display, window);
    }

    public static int XLowerWindow(X11 self, X11.Display display, X11.Window window) {
        return X11.INSTANCE.XLowerWindow(display, window);
    }

    public static boolean IsProcessorFeaturePresent(Kernel32 self, int feature) {
        return Kernel32.INSTANCE.IsProcessorFeaturePresent(feature);
    }
}
