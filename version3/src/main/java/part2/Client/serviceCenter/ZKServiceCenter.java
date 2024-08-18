package part2.Client.serviceCenter;

import com.sun.net.httpserver.Authenticator;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import part2.Client.cache.serviceCache;
import part2.Client.serviceCenter.ZkWatcher.watchZK;
import part2.Client.serviceCenter.balance.impl.ConsistencyHashBalance;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceCenter implements serviceCenter {
    //curator 提供的zookeeper客户端
    private CuratorFramework client;
    //zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";
    private static final String RETRY = "CanRetry";
    //serviceCache
    private serviceCache cache;

    //zookeeper客户端的初始化，并与zookeeper服务端进行连接
    @SneakyThrows
    public ZKServiceCenter() {
        //指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        //zookeeper的地址固定，不管是服务提供者还是消费者，都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime有关系
        //zk还会根据minSessionTimeout和maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20呗
        //使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("zookeeper 连接成功");
        //初始化本地缓存
        cache = new serviceCache();
        //加入zookeeper事件监听器
        watchZK watcher = new watchZK(client, cache);
        //监听启动
        watcher.watchToUpdate(ROOT_PATH);
    }



    //根据服务名（接口名）返回地址
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            //先从本地缓存中找
            List<String> serviceList = cache.getServcieFromCache(serviceName);
            //如果找不到，再去zookeeper中找
            //这种情况基本不会发生，或者说只会出现在初始化阶段
            if (serviceList == null) {
                serviceList = client.getChildren().forPath("/" + serviceName);
            }
            //负载均衡得到地址
            String address = new ConsistencyHashBalance().balance(serviceList);
            return parseAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //是否可以重试
    public boolean checkRetry(String serviceName) {
        boolean canRetry = false;
        try {
            List<String> serviceList = client.getChildren().forPath("/" + RETRY);
            for(String s : serviceList) {
                if(s.equals(serviceName)) {
                    System.out.println("服务" + serviceName + "在白名单上，可进行重试");
                    canRetry = true;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return canRetry;
    }

    //地址 -> XXX.XXX.XXX.XXX:port字符串
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }

    //字符串解析为地址
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }

}
