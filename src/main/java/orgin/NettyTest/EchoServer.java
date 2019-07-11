package orgin.NettyTest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

//http://ifeve.com/netty5-user-guide/
public class EchoServer {

    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
    	//用来接收进来的连接  
        EventLoopGroup bossGroup = new NioEventLoopGroup(); 
        //用来处理已经被接收的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        	//ServerBootstrap 是一个启动NIO服务的辅助启动类。
            ServerBootstrap b = new ServerBootstrap(); 
            b.group(bossGroup, workerGroup) // b.group指定NioEventLoopGroup来接收处理新连接
             .channel(NioServerSocketChannel.class) // b.channel指定通道类型
             .childHandler(new ChannelInitializer<SocketChannel>() { 
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                	 // 处理粘包拆包问题  netty权威指南4.3有详细说明
                	 //ByteBuf buf=Unpooled.copiedBuffer("$$".getBytes());
                	 //ch.pipeline().addLast(new DelimiterBasedFrameDecoder(2048,buf));
                	 //ch.pipeline().addLast(new StringDecoder());
                     ch.pipeline().addLast(new EchoServerHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 1024)          
             .childOption(ChannelOption.SO_KEEPALIVE, true); 
            // b.bind设置绑定的端口
            // b.sync阻塞直至启动服务
            ChannelFuture f = b.bind(port).sync(); 
            // 等待服务端监听端口关闭  起到阻塞作用 直到main方法退出
            f.channel().closeFuture().sync();
        } finally {
        	// 释放线程池资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new EchoServer(8081).run();
    }
}