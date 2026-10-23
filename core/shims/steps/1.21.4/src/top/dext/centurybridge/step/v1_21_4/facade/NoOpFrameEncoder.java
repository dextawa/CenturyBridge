package top.dext.centurybridge.step.v1_21_4.facade;

import io.netty.channel.ChannelOutboundHandlerAdapter;

/**
 * 1.21.3's NoOpFrameEncoder (classRenames), gone in 1.21.4: a pipeline handler doing nothing of its own, a plain
 * ChannelOutboundHandlerAdapter (1.21.3's class body).
 */
public class NoOpFrameEncoder extends ChannelOutboundHandlerAdapter {
    public NoOpFrameEncoder() {
    }
}
