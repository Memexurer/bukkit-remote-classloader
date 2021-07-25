package pl.cekcsecurity.server.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import pl.cekcsecurity.server.gate.LoaderContents;
import pl.cekcsecurity.server.gate.LoaderGateRequest;
import pl.cekcsecurity.server.gate.LoaderGateResponse;
import pl.cekcsecurity.server.gate.LoaderGatekeeper;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ProductNameHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final AttributeKey<Boolean> ALREADY_HANDLED = AttributeKey.newInstance("handled");
    private static final AttributeKey<CompletableFuture<LoaderGateResponse>> GATE_REQUEST = AttributeKey.newInstance("gate-request");

    private final LoaderGatekeeper gatekeeper;

    public ProductNameHandler(LoaderGatekeeper gatekeeper) {
        this.gatekeeper = gatekeeper;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (ctx.channel().hasAttr(GATE_REQUEST))
            throw new IllegalStateException();

        if (msg.readableBytes() > 32)
            throw new IllegalStateException();

        byte[] productNameRaw = new byte[msg.readableBytes()];
        msg.readBytes(productNameRaw);

        String productName = new String(productNameRaw);

        ctx.channel().attr(GATE_REQUEST).set(
                gatekeeper.request(
                        new LoaderGateRequest(productName,
                                ctx.channel().remoteAddress().toString().substring(1))
                ).whenComplete((loaderGateResponse, throwable) -> handle(loaderGateResponse, ctx))
        );
    }

    private void handle(LoaderGateResponse response, ChannelHandlerContext ctx) {
        if (!response.isAllowed()) {
            ctx.close();
            return;
        }

        LoaderContents contents = response.getContents();

        byte[] mainClassName = contents.getMainClassName().getBytes(StandardCharsets.UTF_8);

        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(mainClassName.length);
        buf.writeBytes(mainClassName);

        buf.writeInt(contents.getClasses().size());
        for(byte[] classEntry: contents.getClasses()){
            buf.writeInt(classEntry.length);
            buf.writeBytes(classEntry);
        }

        buf.writeInt(contents.getResources().size());
        for(Map.Entry<String, byte[]> resourceEntry: contents.getResources().entrySet()) {
            byte[] serializedName = resourceEntry.getKey().getBytes(StandardCharsets.UTF_8);
            buf.writeInt(serializedName.length);
            buf.writeBytes(serializedName);

            buf.writeInt(resourceEntry.getValue().length);
            buf.writeBytes(resourceEntry.getValue());
        }

        ctx.writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (ctx.channel().hasAttr(GATE_REQUEST))
            ctx.channel().attr(GATE_REQUEST).get().complete(null);
    }
}
