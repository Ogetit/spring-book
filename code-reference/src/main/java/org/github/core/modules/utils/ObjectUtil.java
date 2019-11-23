package org.github.core.modules.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.*;

/**
 * 转换对象
 *
 * @author zhanglei
 */
public class ObjectUtil {
    /**
     * 把对象存为文件
     *
     * @param file
     * @param o
     * @throws IOException
     */
    public static void obj2File(File file, Object o) throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
        } finally {
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(oos);
        }
    }

    /**
     * 从文件中读取对象
     *
     * @param file
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object file2Obj(File file) throws IOException, ClassNotFoundException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            Object serachParam = ois.readObject();
            return serachParam;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(ois);
        }
    }

    /**
     * 对象转byte
     *
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] obj2Byte(Object obj) throws IOException {
        ByteArrayOutputStream bo = null;
        ObjectOutputStream oo = null;
        byte[] bytes = null;
        try {
            bo = new ByteArrayOutputStream();
            oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            bytes = bo.toByteArray();
        } finally {
            IOUtils.closeQuietly(bo);
            IOUtils.closeQuietly(oo);
        }
        return bytes;
    }

    /**
     * byte转对象
     *
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object byte2Obj(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        Object object = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            object = ois.readObject();
        } finally {
            IOUtils.closeQuietly(bais);
            IOUtils.closeQuietly(ois);
        }
        return object;
    }

    /**
     * 把对象转为字符串，一般用来记录日志 ，只能转换单层的object ，如果object中包含其他object 或list 打印不出来
     *
     * @param object
     * @return
     */
    public static String obj2Str(Object object) {
        if (object == null) {
            return "";
        }
        return ReflectionToStringBuilder.toString(object, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
