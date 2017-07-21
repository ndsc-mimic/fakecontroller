package org.ndsc.mnos;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.ndsc.mnos.fake.ICoreListener;
import org.ndsc.mnos.fake.ZeroMQBaseConnector;
import org.onosproject.openflow.controller.OpenFlowController;
import org.onosproject.openflow.controller.OpenFlowSwitch;
import org.onosproject.openflow.controller.Dpid;
import org.ndsc.mnos.fake.*;

public class adhesivePlaster {
    public OpenFlowSwitch tapeConnectedToTheTop;
    public Channel tapeConnectedToTheBelow;
    public  Dpid dpid;
    //private static long dpID = 0;

    public adhesivePlaster(Channel tapeConnectedToTheBelow) {
        this.tapeConnectedToTheBelow = tapeConnectedToTheBelow;
    }


}