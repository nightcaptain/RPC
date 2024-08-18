package part2.common.Message;

//通过注解来减少样板代码，提高开发效率

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data //为类生成所有字段的 getter 和 setter 方法，equals、hashCode 以及 toString 方法
@Builder //支持使用构建器模式创建对象
public class RpcRequest implements Serializable{
    //服务器类名，客户端只知道接口
    private String interfaceName;
    //调用的方法名
    private String methodName;
    //参数列表
    private Object[] params;
    //参数类型
    private Class<?>[] paramTypes;
}
