package orgin.NettyTest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
@Sharable
public class EchoServerHandler extends ChannelHandlerAdapter { 

	public static final ChannelGroup group = new DefaultChannelGroup(
	        GlobalEventExecutor.INSTANCE);
	
	//有数据读取的时候调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { 
    	ByteBuf bb=(ByteBuf)msg;
    	Channel channel = ctx.channel();
        for (Channel ch : group) {
            if (ch == channel) {
			    //ch.writeAndFlush(bb.copy());
            } else {
                ch.writeAndFlush(bb.copy());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { 
        cause.printStackTrace();
        ctx.writeAndFlush("status:500 "+cause.getMessage());
        ctx.close();
    }
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        /*for (Channel ch : group) {
            ch.writeAndFlush(
            		Unpooled.copiedBuffer( "[" + channel.remoteAddress() + "] " + "进入", CharsetUtil.UTF_8));
        }*/
        group.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        /*for (Channel ch : group) {
            ch.writeAndFlush(
            		Unpooled.copiedBuffer( "[" + channel.remoteAddress() + "] " + "离开", CharsetUtil.UTF_8));
        }*/
        group.remove(channel);
    }

}