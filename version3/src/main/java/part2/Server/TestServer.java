package part2.Server;

import part2.Server.provider.ServiceProvider;
import part2.Server.server.RpcServer;
import part2.Server.server.impl.NettyRPCRPCServer;
import part2.common.service.Impl.UserServiceImpl;
import part2.common.service.UserService;

public class

TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService, true);

        //ThreadPoolRPCRPCServer  SimpleRPCRPCServer   NettyRPCRPCServe
        RpcServer rpcServer = new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
