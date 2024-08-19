package part1.Client;

import part1.Client.proxy.ClientProxy;
import part1.common.polo.User;
import part1.common.service.UserService;

public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        ClientProxy clientProxy = new ClientProxy();
        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);
        System.out.println("从服务端得到的user="+user.toString());

        User u= User.builder().id(100).userName("zzq").sex(true).build();
        Integer id = proxy.insertUser(u);
        System.out.println("向服务端插入user的id"+id);
    }
}
