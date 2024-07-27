package part1.Server;

import part1.Server.server.RpcServer;
import part1.common.service.Impl.UserServiceImpl;
import part1.common.service.UserService;
import part1.Server.server.impl.SimpleRPCRPCServer;
import part1.Server.server.impl.ThreadPoolRPCRPCServer;
import part1.Server.provider.ServiceProvider;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);
        //ThreadPoolRPCRPCServer  SimpleRPCRPCServer
        RpcServer rpcServer = new ThreadPoolRPCRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
