package top.dext.centurybridge.step.v1_20_5.facade;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.util.AttributeKey;
import java.util.List;
import net.minecraft.class_2596;
import net.minecraft.class_8036;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinNet;

/**
 * 1.20.4's PacketBundleUnpacker(attributeKey): splits a bundle with the bundler of the protocol in a channel
 * attribute, read for every packet (rt/CbFinNet). A subclass of 1.20.5's unpacker (ctorRedirects,
 * superRenames); 1.20.4's body.
 */
public class LegacyBundleUnpacker extends class_8036 {

    /** bundlerInfoKey */
    public final AttributeKey<? extends BundlerInfoProvider> field_45676;

    public LegacyBundleUnpacker(AttributeKey<? extends BundlerInfoProvider> bundlerInfoKey) {
        super(CbFinNet.EMPTY_BUNDLER);
        this.field_45676 = bundlerInfoKey;
    }

    @Override
    protected void method_48319(ChannelHandlerContext ctx, class_2596<?> packet, List<Object> out) throws Exception {
        BundlerInfoProvider provider = ctx.channel().attr(this.field_45676).get();
        if (provider == null) {
            throw new EncoderException("Bundler not configured: " + packet);
        }
        provider.method_48312().method_48327(packet, out::add);
    }
}
