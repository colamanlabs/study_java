package com.colamanlabs.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * A simple Netty handler that echoes received messages back to the sender.
 */
@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter
{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        log.debug("[EchoServerHandler/channelActive] this:{}\tremote:{}", this, ctx.channel()
                .remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        // Echo the received message back to the client
        if (msg instanceof ByteBuf)
        {
            ByteBuf buffer = (ByteBuf) msg;
            //            log.debug(String.format("[EchoServerHandler/channelRead] ByteBufUtil.prettyHexDump:[\n%s\n]", ByteBufUtil.prettyHexDump(buffer)));
        }
        else
        {
            log.debug("[EchoServerHandler/channelRead] msg:{}", msg);
        }
        ctx.write(msg); // write preserves reference for ByteBuf; echo back as-is
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
    {
        ctx.flush();
        ctx.close();
        log.debug("[EchoServerHandler/channelReadComplete/close] this:{}\tremote:{}", this, ctx.channel()
                .remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        log.error("[EchoServerHandler/exceptionCaught] error:", cause);
        ctx.close();
    }
}
