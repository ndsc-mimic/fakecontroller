package org.ndsc.mnos;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
public class App {
    private int port;

    public App(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new App(5000).run();

    }

    public void run() {
        EventLoopGroup acceptor = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.group(acceptor, worker);//设置循环线程组，前者用于处理客户端连接事件，后者用于处理网络IO
        bootstrap.channel(NioServerSocketChannel.class);//用于构造socketchannel工厂
        bootstrap.childHandler(new ServerIniterHandler());//为处理accept客户端的channel中的pipeline添加自定义处理函数
        try {
            Channel channel = bootstrap.bind(port).sync().channel();//绑定端口（实际上是创建serversocketchannnel，并注册到eventloop上），同步等待完成，返回相应channel
            System.out.println("server strart running in port:" + port);
            channel.closeFuture().sync();//在这里阻塞，等待关闭
            //
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally { //优雅退出
            acceptor.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}

/*import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.ndsc.mnos.fake.FakeController;
import org.ndsc.mnos.fake.ZeroMQBaseConnector;
import org.ndsc.mnos.fake.FakeDeviceListener;
import org.onosproject.openflow.controller.OpenFlowController;
import org.onosproject.openflow.controller.impl.*;
public class App {
    private final int port;
    public OpenFlowController controller;
    public ZeroMQBaseConnector coreConnector;
    public FakeController shimController;
    public FakeDeviceListener ofDeviceListener;
    public EchoServerHandler echoServerHandler;
    public OpenFlowController controller=new OpenFlowControllerImpl();
    public ZeroMQBaseConnector coreConnector=new ZeroMQBaseConnector();
    public FakeController shimController=new FakeController(coreConnector,controller);
    public FakeDeviceListener ofDeviceListener=new FakeDeviceListener(controller,shimController);
    public App(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(group) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(this.port)// 绑定监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("connected...; Client:" + ch.remoteAddress());
                            ch.pipeline().addLast("decoder", new StringDecoder());
                            ch.pipeline().addLast("encoder", new StringEncoder());
                            ch.pipeline().addLast(echoServerHandler=new EchoServerHandler()); // 客户端触发操作
                        }
                    });
            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
            System.out.println(App.class + " started and listen on " + cf.channel().localAddress());
            controller=new OpenFlowControllerImpl();
            //controller.getSwitch()
            coreConnector=new ZeroMQBaseConnector();
            shimController=new FakeController(coreConnector,controller);
            ofDeviceListener=new FakeDeviceListener(controller,shimController);
            cf.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
        }
    }

    public static void main(String[] args) throws Exception {

        new App(5000).start(); // 启动
    }
}*/