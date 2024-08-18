package part2.Server.provider;

import part2.Server.serviceRegister.ServiceRegister;
import part2.Server.serviceRegister.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

//本地服务存放器
public class ServiceProvider {
    //集合中存放服务的实例
    private Map<String,Object> interfaceProvider;

    private int port;
    private String host;
    //注册服务类
    private ServiceRegister serviceRegister;

    public ServiceProvider(String host,int port) {
        this.host = host;
        this.port = port;
        this.interfaceProvider = new HashMap<>();
        this.serviceRegister = new ZKServiceRegister();
    }

    //本地注册服务

    public void provideServiceInterface(Object service,boolean canRetry) {
        String serviceName = service.getClass().getName();
        Class<?>[] interfaceName = service.getClass().getInterfaces();

        for(Class<?> clazz : interfaceName) {
            //本机的映射表
            interfaceProvider.put(clazz.getName(),service);
            //在注册中心注册服务
            serviceRegister.register(clazz.getName(),new InetSocketAddress(host,port),canRetry);
        }

    }
    //获取服务实例
    public Object getService(String interfaceName) { return interfaceProvider.get(interfaceName); }
}
