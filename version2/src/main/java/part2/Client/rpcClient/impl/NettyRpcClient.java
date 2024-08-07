package part2.Client.rpcClient.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import part2.Client.netty.nettyInitializer.NettyClientInitialzer;
import part2.Client.rpcClient.RpcClient;
import part2.Client.serviceCenter.ZKServiceCenter;
import part2.common.Message.RpcRequest;
import part2.common.Message.RpcResponse;

import java.net.InetSocketAddress;

public class NettyRpcClient implements RpcClient {

    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;

    private part2.Client.serviceCenter.serviceCenter serviceCenter;
    public NettyRpcClient() { this.serviceCenter = new ZKServiceCenter(); }
    //netty客户端初始化
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new NettyClientInitialzer());
        //NettyClientInitializer这里 配置netty对消息的处理机制
    }
    @Override
    public RpcResponse sendRequest(RpcRequest request) {

            //从注册中心获取host，port
            InetSocketAddress address = serviceCenter.serviceDiscovery(request.getInterfaceName());
            String host = address.getHostName();
            int port = address.getPort();
            try {
                //创建channelFuture对象，代表这一个操作事件，sync方法表示堵塞直到connect完成
                ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
                //channel表示一个连接的单位，类似socket
                Channel channel = channelFuture.channel();
                //发送数据
                channel.writeAndFlush(request);
                //sync()堵塞获取结果
                channel.closeFuture().sync();
                // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
                // AttributeKey是，线程隔离的，不会有线程安全问题。
                // 当前场景下选择堵塞获取结果
                // 其它场景也可以选择添加监听器的方式来异步获取结果 channelFuture.addListener...
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
                RpcResponse response = (RpcResponse) channel.attr(key).get();

                System.out.println(response);
                return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
