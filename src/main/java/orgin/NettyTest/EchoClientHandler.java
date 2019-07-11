package orgin.NettyTest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

@Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private String nickname;
	
	public EchoClientHandler(String nickname)
	{
		this.nickname=nickname;
	}
	
	public EchoClientHandler(){}
	
	//在到服务器的连接已经建立之后将被调用；
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(Unpooled.copiedBuffer(nickname+" 已进入房间", CharsetUtil.UTF_8));
	}
	
	//当从服务器接收到一条消息时被调用；
	@Override
	protected void messageReceived(ChannelHandlerContext arg0, ByteBuf bf) throws Exception {
        byte[] byteArray = new byte[bf.readableBytes()];  
        bf.readBytes(byteArray);  
        String result = new String(byteArray,"UTF-8");
		System.out.println(result);
	}

	//在处理过程中引发异常时被调用。
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
