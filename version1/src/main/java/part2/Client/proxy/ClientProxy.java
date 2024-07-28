package part2.Client.proxy;

import lombok.AllArgsConstructor;
import part2.Client.IOClient;
import part2.common.Message.RpcRequest;
import part2.common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    //传入参数service接口的class对象，反射封装成一个request
    private String host;
    private int port;

    //jdk动态代理
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构建request
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramTypes(method.getParameterTypes()).build();
        //IOClient.sendRequest 和服务端进行数据传输
        RpcResponse response = IOClient.sendRequest(host, port, request);
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}
