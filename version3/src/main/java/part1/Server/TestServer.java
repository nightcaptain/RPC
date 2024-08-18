package part1.Server;

import part1.Server.provider.ServiceProvider;
import part1.Server.server.RpcServer;
import part1.Server.server.impl.NettyRPCRPCServer;
import part1.common.service.Impl.UserServiceImpl;
import part1.common.service.UserService;

public class

TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService);

        //ThreadPoolRPCRPCServer  SimpleRPCRPCServer   NettyRPCRPCServe
        RpcServer rpcServer = new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
