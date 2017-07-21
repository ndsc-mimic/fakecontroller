package org.ndsc.mnos;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.onosproject.openflow.controller.impl.OFMessageDecoder;

public class ServerIniterHandler extends  ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel arg0) throws Exception {
        ChannelPipeline pipeline = arg0.pipeline();
        pipeline.addLast("docode",new OFMessageDecoder());
        pipeline.addLast("encode",new StringEncoder());
        pipeline.addLast("chat",new ServerChatHandler());
    }
}
