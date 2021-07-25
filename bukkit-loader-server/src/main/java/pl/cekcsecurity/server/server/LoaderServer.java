package pl.cekcsecurity.server.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import pl.cekcsecurity.server.gate.LoaderGatekeeper;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class LoaderServer {
    private final LoaderGatekeeper gatekeeper;

    public LoaderServer(LoaderGatekeeper gatekeeper) {
        this.gatekeeper = gatekeeper;
    }

    public void start(InetSocketAddress bindAddress) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(bindAddress);

            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    System.out.println("██▅▇██▇▆▅▄▄▄▇");
                    socketChannel.pipeline().addLast(new ProductNameHandler(gatekeeper), new ReadTimeoutHandler(1, TimeUnit.MINUTES));
                }
            });
            System.out.println("Binding Xddd");
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }

    }
}
