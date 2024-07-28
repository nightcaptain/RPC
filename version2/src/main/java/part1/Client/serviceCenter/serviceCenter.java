package part1.Client.serviceCenter;

import java.net.InetSocketAddress;

public interface serviceCenter {
    //查询，根据服务名查找地址
    InetSocketAddress serviceDiscovery(String serviceName);
}
