package orgin.NettyTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        // 配置客户端NIO线程组
    	EventLoopGroup group = new NioEventLoopGroup();
        try {    
        	System.out.println("输入昵称");
        	final String nickname = new BufferedReader(new InputStreamReader(System.in)).readLine();

        	// Bootstrap 是 Netty 提供的工厂类  启动器
            Bootstrap b = new Bootstrap();                              //  指定EventLoopGroup以处理客户端事件；需要适用于NIO的实现
            b.group(group)    
                 .channel(NioSocketChannel.class)                       //  适用于NIO传输的Channel类型
                 .remoteAddress(new InetSocketAddress(host, port))      //  设置服务器的InetSocketAddr-ess![](/api/storage/getbykey/screenshow?key=17043add7e9c14a5d3f7)                
                 .handler(new ChannelInitializer<SocketChannel>() {     //  在创建Channel时，向ChannelPipeline中添加一个Echo-ClientHandler实例
                 @Override
                public void initChannel(SocketChannel ch)
                    throws Exception {
	            	 //ByteBuf buf=Unpooled.copiedBuffer("$$".getBytes());
	            	 //ch.pipeline().addLast(new DelimiterBasedFrameDecoder(2048,buf));
	            	 //ch.pipeline().addLast(new StringDecoder());
	            	 ch.pipeline().addLast(new EchoClientHandler(nickname));
                    }
                });
            
            ChannelFuture f = b.connect().sync();       //  连接到远程节点，阻塞等待直到连接完成
            while (true) 
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String input = reader.readLine();
                if(input.equals("quit"))
                {
                	f.channel().writeAndFlush(Unpooled.copiedBuffer(nickname+" 退出房间", CharsetUtil.UTF_8));
                	f.channel().disconnect();
                }
                f.channel().writeAndFlush(Unpooled.copiedBuffer(nickname+":"+input, CharsetUtil.UTF_8));
            }
            //f.channel().closeFuture().sync();         //  阻塞，直到Channel关闭
        } finally {
            group.shutdownGracefully().sync();          //  关闭线程池并且释放所有的资源
        }
    }

    public static void main(String[] args) throws Exception {
        new EchoClient("127.0.0.1", 8081).start();
    }
}