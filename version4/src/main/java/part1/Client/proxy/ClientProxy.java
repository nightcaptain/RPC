package part1.Client.proxy;

import part1.Client.circuitBreaker.CircuitBreaker;
import part1.Client.circuitBreaker.CircuitBreakerProvider;
import part1.Client.retry.guavaRetry;
import part1.Client.rpcClient.RpcClient;
import part1.Client.rpcClient.impl.NettyRpcClient;
import part1.Client.serviceCenter.ZKServiceCenter;
import part1.Client.serviceCenter.serviceCenter;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ClientProxy implements InvocationHandler {
    //传入参数service接口的class对象，反射封装成一个request
    private RpcClient rpcClient;
    private serviceCenter ServiceCenter;
    private CircuitBreakerProvider CircuitBreakerProvider;
    public ClientProxy() throws InterruptedException {
        ServiceCenter = new ZKServiceCenter();
        rpcClient = new NettyRpcClient(ServiceCenter);
        CircuitBreakerProvider = new CircuitBreakerProvider();
    }

    //jdk动态代理
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构建request
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramTypes(method.getParameterTypes()).build();
        //获取熔断器
        CircuitBreaker circuitBreaker = CircuitBreakerProvider.getCircuitBreaker(method.getName());
        //判断熔断器是否允许请求经过
        if (!circuitBreaker.allowRequest()) {
            //这里可以针对熔断做特殊处理，返回特殊值
            return null;
        }
        //SimpleSocketRpcClient NettyRpcClient
        RpcResponse response ;
        //后续添加逻辑，为保持幂等性，只对白名单上的服务进行重试
        if (ServiceCenter.checkRetry(request.getInterfaceName())) {
            //调用retry框架进行重试操作
            response = new guavaRetry().sendServiceWithRetry(request, rpcClient);
        } else {
            //只调用一次
            response = rpcClient.sendRequest(request);
        }
        //记录response的状态，上报给熔断器
        if (response.getCode() == 200) {
            circuitBreaker.recordSuccess();
        }
        if(response.getCode() == 500){
            circuitBreaker.recordFailure();
        }
        //打印request内容
        return response.getData();
    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}
