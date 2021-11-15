package cn.w2n0.genghiskhan.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author 无量
 * date 2021/11/8 18:15
 */
public class CloneUtils {
    @SuppressWarnings("unchecked")

    public static <T> T clone(T obj) {
        T clonedObj = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            clonedObj = (T) ois.readObject();

            ois.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return clonedObj;
    }
}