package com.colamanlabs.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class EchoServer
{

    private final String host;
    private final int port;

    public EchoServer(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public void startAndBlock() throws InterruptedException
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(host, port))
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>()
                    {
                        @Override
                        protected void initChannel(SocketChannel ch)
                        {
                            ch.pipeline()
                                    .addLast(new EchoServerHandler());
                        }
                    });

            log.info("[EchoServer/startAndBlock] Binding on {}:{}", host, port);
            ChannelFuture f = b.bind()
                    .sync();
            log.info("[EchoServer/startAndBlock] Server started and listening on {}", f.channel()
                    .localAddress());

            // Block until the server socket is closed.
            f.channel()
                    .closeFuture()
                    .sync();
        }
        finally
        {
            log.info("[EchoServer/startAndBlock] Shutting down event loops...");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
