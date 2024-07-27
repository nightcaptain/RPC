package part1.Server.server;

public interface RpcServer {
    //开启监听
    void start(int port) ;
    void stop();
}
