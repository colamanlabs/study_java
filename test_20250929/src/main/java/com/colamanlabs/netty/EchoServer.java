package com.colamanlabs.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Iterator;

@Slf4j
public class EchoServer
{

    private final String host;
    private final int port;

    private final int workerThreadCount;

    public EchoServer(String host, int port, int workerThreadCount)
    {
        this.host = host;
        this.port = port;
        this.workerThreadCount = workerThreadCount;
    }

    public void startAndBlock() throws InterruptedException
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(this.workerThreadCount);

        /*
        EventLoopGroup 에서 직접 thread 를 접근할 수는 없다.
         */
        Iterator<EventExecutor> iterator = workerGroup.iterator();
        int iterIndex = 0;
        while (iterator.hasNext())
        {
            log.info(String.format("[EchoServer/startAndBlock] iterIndex:[%d]\tEventExecutor:[%s]", iterIndex, iterator.next()));
            iterIndex = iterIndex + 1;
        }

        try
        {
            /*
            workerThread 는 쓰레드풀에서 계속 재활용 되지만,
            새로운 커넥션에는, 새로운 핸들러가 할당된다.
             */
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
