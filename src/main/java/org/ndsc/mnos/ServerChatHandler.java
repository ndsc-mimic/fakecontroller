package org.ndsc.mnos;

import io.netty.channel.Channel;
import org.ndsc.mnos.fake.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.ndsc.mnos.fake.ZeroMQBaseConnector;
import org.onosproject.openflow.controller.Dpid;
import org.onosproject.openflow.controller.RoleState;
import org.projectfloodlight.openflow.protocol.*;
import org.onosproject.openflow.controller.OpenFlowController;
import org.slf4j.Logger;

import static org.projectfloodlight.openflow.protocol.OFType.*;
import static org.projectfloodlight.openflow.protocol.OFVersion.*;
import static org.slf4j.LoggerFactory.getLogger;


public class ServerChatHandler extends SimpleChannelInboundHandler<String> {
    private final Logger log = getLogger(getClass());
    public static final ChannelGroup group = new DefaultChannelGroup( GlobalEventExecutor.INSTANCE);

    ZeroMQBaseConnector connector;
    OpenFlowController controller;
    FakeController shimController;
    protected ServerChatHandler() {
        connector.setPort();
        connector.setAddress();
        shimController = new FakeController(connector,controller);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext arg0, String arg1)throws Exception {
        Channel channel = arg0.channel();
        OFMessage msg = change1(arg1);
        Dpid dpid = search(channel);
        switch (msg.getType()) {
            case PACKET_IN:
                shimController.sendOpenFlowMessageToCore(msg, msg.getXid(), dpid.value(), 0);
                break;
            case FLOW_REMOVED:
                shimController.sendOpenFlowMessageToCore(msg, msg.getXid(), dpid.value(), 0);
                break;
            case PORT_STATUS:
                shimController.sendOpenFlowMessageToCore(msg, msg.getXid(), dpid.value(), 0);
                break;
            case STATS_REPLY:
                if (shimController.containsXid(msg.getXid())) {
                    shimController.sendOpenFlowMessageToCore(msg, msg.getXid(), dpid.value(), shimController.getAndDeleteModuleId(msg.getXid()));
                }
                break;
            case ERROR:
                shimController.sendOpenFlowMessageToCore(msg, msg.getXid(), dpid.value(), 0);
                break;
            case BARRIER_REPLY:
                if (shimController.containsXid(msg.getXid())) {
                    shimController.sendOpenFlowMessageToCore(msg, msg.getXid(), dpid.value(), shimController.getAndDeleteModuleId(msg.getXid()));
                }
                break;
            case ECHO_REPLY:
                if (shimController.containsXid(msg.getXid())) {
                    shimController.sendOpenFlowMessageToCore(msg, msg.getXid(), dpid.value(), shimController.getAndDeleteModuleId(msg.getXid()));
                }
                break;
            default:
                if (shimController.containsXid(msg.getXid())) {
                    shimController.sendOpenFlowMessageToCore(msg, msg.getXid(), dpid.value(), shimController.getAndDeleteModuleId(msg.getXid()));
                } else {
                    shimController.sendOpenFlowMessageToCore(msg, msg.getXid(), dpid.value(), 0);
                }
                break;
        }
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        adhesivePlaster tapeConnect=new  adhesivePlaster(channel);
        group.add(channel);
        tapeConnect.tapeConnectedToTheTop= controller.getSwitch(tapeConnect.dpid);
        OFVersion version = tapeConnect.tapeConnectedToTheTop.factory().getVersion();
        Integer ofVersion = version.getWireVersion();
        shimController.setSupportedProtocol(ofVersion.byteValue());
        OFFeaturesReply featuresReply = shimController.getFeatureReply(tapeConnect.tapeConnectedToTheTop);
        controller.setRole(tapeConnect.dpid, RoleState.MASTER);
        shimController.sendOpenFlowMessageToCore(featuresReply,featuresReply.getXid(),tapeConnect.tapeConnectedToTheTop.getId(),0);

        //Create OFPortDescStatsReply for OF_13
        if (tapeConnect.tapeConnectedToTheTop.factory().getVersion() == OF_13) {
            OFPortDescStatsReply.Builder statsReplyBuilder = tapeConnect.tapeConnectedToTheTop.factory().buildPortDescStatsReply();
            statsReplyBuilder.setEntries(tapeConnect.tapeConnectedToTheTop.getPorts())
                    .setXid(0);
            OFPortDescStatsReply ofPortStatsReply = statsReplyBuilder.build();
            shimController.sendOpenFlowMessageToCore(ofPortStatsReply,ofPortStatsReply.getXid(),tapeConnect.tapeConnectedToTheTop.getId(),0);
        }
        log.info("Switch {} connected", tapeConnect.dpid);
        shimController.tapeConnectgroup.add(tapeConnect);
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        group.remove(channel);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println( "[" + ctx.channel().remoteAddress() + "]" + "exit the room");
        ctx.close().sync();
    }

    public OFMessage change1(String S){
        OFMessage message;
        OFVersion version;
        OFType type;
        String A,B,C;
        String[] aa = S.split("_");
        A = aa[0];
        B = aa[1];
        C = aa[2];
        long xid = Long.parseLong(C);

        switch (A){
            case "0":
                version = OF_10;
                break;
            case "1":
                version = OF_11;
                break;
            case "2":
                version = OF_12;
                break;
            case "3":
                version = OF_13;
                break;
            case "4":
                version = OF_14;
                break;
        }

        switch(B){
            case  "0":
                type = HELLO;
            break;
            case  "1":
                type = ERROR;
                break;
            case  "2":
                type = ECHO_REQUEST;
                break;
            case  "3":
                type = ECHO_REPLY;
                break;
            case  "4":
                type = EXPERIMENTER;
                break;
            case  "5":
                type = FEATURES_REQUEST;
                break;
            case  "6":
                type = FEATURES_REPLY;
                break;
            case  "7":
                type = GET_CONFIG_REQUEST;
                break;
            case  "8":
                type = GET_CONFIG_REPLY;
                break;
            case  "9":
                type = SET_CONFIG;
                break;
            case  "10":
                type = PACKET_IN;
                break;
            case  "11":
                type = FLOW_REMOVED;
                break;
            case  "12":
                type = PORT_STATUS;
                break;
            case  "13":
                type = PACKET_OUT;
                break;
            case  "14":
                type = FLOW_MOD;
                break;
            case  "15":
                type = PORT_MOD;
                break;
            case  "16":
                type = STATS_REQUEST;
                break;
            case  "17":
                type = STATS_REPLY;
                break;
            case  "18":
                type = BARRIER_REQUEST;
                break;
            case  "19":
                type = BARRIER_REPLY;
                break;
            case  "20":
                type = QUEUE_GET_CONFIG_REQUEST;
                break;
            case  "21":
                type = QUEUE_GET_CONFIG_REPLY;
                break;
            case  "22":
                type = GROUP_MOD;
                break;
            case  "23":
                type = TABLE_MOD;
                break;
            case  "24":
                type = ROLE_REQUEST;
                break;
            case  "25":
                type = ROLE_REPLY;
                break;
            case  "26":
                type = GET_ASYNC_REQUEST;
                break;
            case  "27":
                type = GET_ASYNC_REPLY;
                break;
            case  "28":
                type = SET_ASYNC;
                break;
            case  "29":
                type = METER_MOD;
                break;
            case  "30":
                type = ROLE_STATUS;
                break;
            case  "31":
                type = TABLE_STATUS;
                break;
            case  "32":
                type = REQUESTFORWARD;
                break;
            case  "33":
                type = BUNDLE_CONTROL;
                break;
            case  "34":
                type = BUNDLE_ADD_MESSAGE;
                break;
        }
        //message.set
        //message.set
        //message.set
        return message;
    }

    public String change2(OFMessage message){
        String S;
        OFVersion version=message.getVersion();               //openflow协议版本
        OFType type=message.getType();                //openflow消息类型
        long xid=message.getXid();
        String A = new String();
        String B = new String();
        String C = new String();
        C = Long.toString(xid);
        switch (version){
            case OF_10:
                A = "0";
                break;
            case OF_11:
                A = "1";
                break;
            case OF_12:
                A = "2";
                break;
            case OF_13:
                A = "3";
                break;
            case OF_14:
                A = "4";
                break;
        }
        switch(type){
            case HELLO:
                B = "0";
                break;
            case ERROR:
                B = "1";
                break;
            case ECHO_REQUEST:
                B = "2";
                break;
            case ECHO_REPLY:
                B = "3";
                break;
            case EXPERIMENTER:
                B = "4";
                break;
            case FEATURES_REQUEST:
                B = "5";
                break;
            case FEATURES_REPLY:
                B = "6";
                break;
            case GET_CONFIG_REQUEST:
                B = "7";
                break;
            case GET_CONFIG_REPLY:
                B = "8";
                break;
            case SET_CONFIG:
                B = "9";
                break;
            case PACKET_IN:
                B = "10";
                break;
            case FLOW_REMOVED:
                B = "11";
                break;
            case PORT_STATUS:
                B = "12";
                break;
            case PACKET_OUT:
                B = "13";
                break;
            case FLOW_MOD:
                B = "14";
                break;
            case PORT_MOD:
                B = "15";
                break;
            case STATS_REQUEST:
                B = "16";
                break;
            case STATS_REPLY:
                B = "17";
                break;
            case BARRIER_REQUEST:
                B = "18";
                break;
            case BARRIER_REPLY:
                B = "19";
                break;
            case QUEUE_GET_CONFIG_REQUEST:
                B = "20";
                break;
            case QUEUE_GET_CONFIG_REPLY:
                B = "21";
                break;
            case GROUP_MOD:
                B = "22";
                break;
            case TABLE_MOD:
                B = "23";
                break;
            case ROLE_REQUEST:
                B = "24";
                break;
            case ROLE_REPLY:
                B = "25";
                break;
            case GET_ASYNC_REQUEST:
                B = "26";
                break;
            case GET_ASYNC_REPLY:
                B = "27";
                break;
            case SET_ASYNC:
                B = "28";
                break;
            case METER_MOD:
                B = "29";
                break;
            case ROLE_STATUS:
                B = "30";
                break;
            case TABLE_STATUS:
                B = "31";
                break;
            case REQUESTFORWARD:
                B = "32";
                break;
            case BUNDLE_CONTROL:
                B = "33";
                break;
            case BUNDLE_ADD_MESSAGE:
                B = "34";
                break;
        }
        return A + "_" + B + "_" + C;
    }
    public Dpid search (Channel channel) {
        adhesivePlaster aa=new adhesivePlaster(channel);
        for (adhesivePlaster ad : shimController.tapeConnectgroup) {
            if (ad.tapeConnectedToTheBelow.id() == channel.id()) {
                return ad.dpid;
            }
        }
        return aa.dpid;
    }
}

