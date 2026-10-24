package top.dext.centurybridge.step.v1_20_5.facade;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.util.AttributeKey;
import java.util.List;
import net.minecraft.class_2596;
import net.minecraft.class_8035;
import net.minecraft.class_8039;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinNet;

/**
 * 1.20.4's PacketBundlePacker(attributeKey): gathers a bundle's packets with the bundler of the protocol in a
 * channel attribute, read for every packet (rt/CbFinNet). A subclass of 1.20.5's packer (ctorRedirects,
 * superRenames); 1.20.4's body.
 */
public class LegacyBundlePacker extends class_8035 {

    /** bundlerInfoKey */
    public final AttributeKey<? extends BundlerInfoProvider> field_45675;
    /** currentBundler */
    private class_8039.class_8040 cbCurrentBundler;
    /** infoForCurrentBundler */
    private class_8039 cbInfoForCurrentBundler;

    public LegacyBundlePacker(AttributeKey<? extends BundlerInfoProvider> bundlerInfoKey) {
        super(CbFinNet.EMPTY_BUNDLER);
        this.field_45675 = bundlerInfoKey;
    }

    @Override
    protected void method_48318(ChannelHandlerContext ctx, class_2596<?> packet, List<Object> out) throws Exception {
        BundlerInfoProvider provider = ctx.channel().attr(this.field_45675).get();
        if (provider == null) {
            throw new DecoderException("Bundler not configured: " + packet);
        }
        class_8039 info = provider.method_48312();
        if (this.cbCurrentBundler != null) {
            if (this.cbInfoForCurrentBundler != info) {
                throw new DecoderException("Bundler handler changed during bundling");
            }
            class_2596<?> bundle = this.cbCurrentBundler.method_48328(packet);
            if (bundle != null) {
                this.cbInfoForCurrentBundler = null;
                this.cbCurrentBundler = null;
                out.add(bundle);
            }
        } else {
            class_8039.class_8040 bundler = info.method_48326(packet);
            if (bundler != null) {
                this.cbCurrentBundler = bundler;
                this.cbInfoForCurrentBundler = info;
            } else {
                out.add(packet);
            }
        }
    }
}
