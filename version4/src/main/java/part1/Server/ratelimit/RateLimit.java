package part1.Server.ratelimit;

public interface RateLimit {
    //获取访问许可
    boolean getToken();
}
