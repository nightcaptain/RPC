package part1.common.serializer.mySerializer;

import java.io.*;

public class ObjectSerializer implements Serializer {
    //利用Java io 对象 ->字节数组
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            //一个对象输出流，用于将 Java 对象序列化为字节流，并将其连接到bos上
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            ////刷新 ObjectOutputStream，确保所有缓冲区中的数据都被写入到底层流中。
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    //字节数组到对象
    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    //0代表Java原生序列器
    @Override
    public  int getType() {
        return 0;
    }


}
