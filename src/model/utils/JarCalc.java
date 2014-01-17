package model.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class JarCalc {
    public static String getJarDir(Class<?> aclass) {
        String path =  aclass.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            path = URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        path = new File(path).getPath();
        int pos = Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\"));
        path = path.substring(0, pos+1);
        return path;
    }
}
