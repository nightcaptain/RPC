package part2.Server.netty.nettyInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import part2.Server.netty.handler.NettyRPCServerHandler;
import part2.Server.provider.ServiceProvider;
import part2.common.serializer.myCode.MyDecoder;
import part2.common.serializer.myCode.MyEncoder;
import part2.common.serializer.mySerializer.JsonSerializer;

@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
    /*
        ChannelPipeline pipeline = ch.pipeline();
        //消息格式 【长度】【消息体】，解决粘包问题
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));//可以处理的最大消息长度 长度字段的偏移量，长度字段的长度，长度调整值，跳过的初始字节数
        //计算当前待发送消息的长度，写入到前四个字节中
        pipeline.addLast(new LengthFieldPrepender(4));

        //使用Java序列化方法，netty的自带的解码编码支持传输这种结构
        pipeline.addLast(new ObjectEncoder());
        //使用了Netty中的ObjectDecoder，它用于将字节流解码为 Java 对象。
        //在ObjectDecoder的构造函数中传入了一个ClassResolver 对象，用于解析类名并加载相应的类。
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));
    */
        ChannelPipeline pipeline = ch.pipeline();
        //使用自定义的编/解码器
        pipeline.addLast(new MyEncoder(new JsonSerializer()));
        pipeline.addLast(new MyDecoder());
        pipeline.addLast(new NettyRPCServerHandler(serviceProvider));
    }
}
