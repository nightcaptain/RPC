package part3.Client.rpcClient;

import part3.common.Message.RpcRequest;
import part3.common.Message.RpcResponse;

public interface RpcClient {
    //定义底层通信的方法
    RpcResponse sendRequest(RpcRequest request);
}
