package part3.Client.proxy;

import lombok.AllArgsConstructor;
import part3.Client.rpcClient.RpcClient;
import part3.Client.rpcClient.impl.NettyRpcClient;
import part3.Client.rpcClient.impl.SimpleSocketRpcClient;
import part3.common.Message.RpcRequest;
import part3.common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    //传入参数service接口的class对象，反射封装成一个request
    private RpcClient rpcClient;
    public ClientProxy(String host, int port,int choose) {
        switch (choose) {
            case 1:
                rpcClient = new SimpleSocketRpcClient(host, port);
                break;
            case 2:
                rpcClient = new NettyRpcClient(host, port);
        }
    }

    //jdk动态代理
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构建request
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramTypes(method.getParameterTypes()).build();
        //SimpleSocketRpcClient NettyRpcClient
        RpcResponse response = rpcClient.sendRequest(request);
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}
