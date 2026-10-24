package top.dext.centurybridge.step.v1_21_4.facade;

import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 1.21.3's NoOpFrameDecoder (classRenames), gone in 1.21.4: a pipeline handler doing nothing of its own, a plain
 * ChannelInboundHandlerAdapter (1.21.3's class body).
 */
public class NoOpFrameDecoder extends ChannelInboundHandlerAdapter {
    public NoOpFrameDecoder() {
    }
}
