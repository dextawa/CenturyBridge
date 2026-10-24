package cb.slib;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.id.ClientID;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.xml.XmlFrameDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.ModInitializer;

/**
 * A 1.21.10-era mod using the game's own copies of jackson, nimbus and oauth2-oidc-sdk (dropped by 1.21.11, nested by the
 * layer) and netty 4.1 classes 4.2 removed. Each check prints "CB-SLIB ok|FAIL ..."; the last line is "CB-SLIB LIBS DONE".
 */
public class SlibLibs11Test implements ModInitializer {
    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-SLIB " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) FAILS.add(what);
    }

    static void run(String what, Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            check(false, what + " threw " + e);
            e.printStackTrace(System.out);
        }
    }

    static String str(Object frame) {
        ByteBuf b = (ByteBuf) frame;
        try {
            return b.toString(StandardCharsets.UTF_8);
        } finally {
            b.release();
        }
    }

    @Override
    public void onInitialize() {
        run("jackson", () -> {
            try {
                ObjectMapper m = new ObjectMapper();
                check("{\"a\":1}".equals(m.writeValueAsString(Map.of("a", 1))), "jackson writes JSON");
                JsonNode n = m.readTree("{\"x\":[1,2,3]}");
                check(n.get("x").size() == 3, "jackson reads JSON");
                System.out.println("CB-SLIB jackson-databind version " + com.fasterxml.jackson.databind.cfg.PackageVersion.VERSION
                    + ", jackson-core " + com.fasterxml.jackson.core.json.PackageVersion.VERSION);
            } catch (Exception e) {
                check(false, "jackson " + e);
            }
        });
        run("nimbus", () -> {
            try {
                JWTClaimsSet claims = new JWTClaimsSet.Builder().subject("steve").issuer("cb").build();
                String s = new PlainJWT(claims).serialize();
                check("steve".equals(PlainJWT.parse(s).getJWTClaimsSet().getSubject()), "nimbus-jose-jwt round trip");
            } catch (Exception e) {
                check(false, "nimbus " + e);
            }
            check("abc".equals(new ClientID("abc").getValue()), "oauth2-oidc-sdk ClientID");
        });
        run("netty xml", () -> {
            EmbeddedChannel ch = new EmbeddedChannel(new XmlFrameDecoder(1024));
            ch.writeInbound(Unpooled.copiedBuffer("  <a><b/></a><c x='1'/>", StandardCharsets.UTF_8));
            check("<a><b/></a>".equals(str(ch.readInbound())) && "<c x='1'/>".equals(str(ch.readInbound())), "XmlFrameDecoder splits documents");
            ch.finishAndReleaseAll();
        });
        run("netty varint", () -> {
            EmbeddedChannel enc = new EmbeddedChannel(new ProtobufVarint32LengthFieldPrepender());
            byte[] body = new byte[300];
            enc.writeOutbound(Unpooled.wrappedBuffer(body));
            ByteBuf framed = enc.readOutbound();
            check(framed.readableBytes() == 302 && (framed.getByte(0) & 0xFF) == 0xAC && framed.getByte(1) == 2, "varint length prefix of 300");
            EmbeddedChannel dec = new EmbeddedChannel(new ProtobufVarint32FrameDecoder());
            dec.writeInbound(framed);
            ByteBuf out = dec.readInbound();
            check(out.readableBytes() == 300, "ProtobufVarint32FrameDecoder reads the frame");
            out.release();
            enc.finishAndReleaseAll();
            dec.finishAndReleaseAll();
        });
        run("netty eventLoop", () -> {
            NioEventLoopGroup group = new NioEventLoopGroup(1);
            try {
                NioSocketChannel ch = new NioSocketChannel();
                group.register(ch).syncUninterruptibly();
                NioEventLoop loop = ch.eventLoop();
                check(loop != null, "a channel on a NioEventLoopGroup answers its NioEventLoop");
                ch.close().syncUninterruptibly();
            } finally {
                group.shutdownGracefully();
            }
        });
        System.out.println("CB-SLIB LIBS DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}
