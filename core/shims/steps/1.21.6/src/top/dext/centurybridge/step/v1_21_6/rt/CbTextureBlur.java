package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * AbstractTexture's 1.21.5 blur field (field_5205), which 1.21.6 dropped: CbTextureBlurMixin declares it again on
 * AbstractTexture, CbTextureReloadBlurMixin sets it where 1.21.5's ReloadableTexture.apply did.
 */
public interface CbTextureBlur {

    void cb$setBlur(boolean blur);
}
