package com.zcx.cloud.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 用于操作类的工具类
 * @author ZCX
 *
 */
public class ClassUtil {
    //字节码后缀
    public static final String BYTE_CODE_SUFFIX = "class";
    //符号
    public static final String SYMBOL_SPOT = ".";
    //用于字符串分隔的.号
    public static final String SYMBOL_SPLIT_SPOT = "\\.";
    public static final String SYMBOL_SEPARATOR = "/";
    public static final String BLOCK_STRING = "";

    

    /**
     * 获取指定类或接口同包下的实现类以及接口
     * @param interfac
     * @return
     */
    public static List<Class<?>> getClassesByInterface(Class<?> interfac){
    	return getClassesByInterface(interfac, null);
    }
    
    /**
     * 获取指定包下指定类或接口的实现类以及接口
     * @param interfac 接口对象
     * @param pack 包名称
     * @return 类（接口）对象集合
     */
    public static List<Class<?>> getClassesByInterface(Class<?> interfac, String pack){
        List<Class<?>> classes = new ArrayList<>();
        try{
            //1.获取路径,默认接口的路径
            String path = BLOCK_STRING;
            if(Objects.isNull(pack)||pack.trim()==BLOCK_STRING){
                pack = interfac.getPackage().getName();
                path = interfac.getResource(SYMBOL_SPOT).getPath();
            }else{
                String relPath = pack.replace(SYMBOL_SPOT, SYMBOL_SEPARATOR);
                path = interfac.getResource(SYMBOL_SEPARATOR).getPath() + relPath;
            }
            //2.获取文件夹下的所有全类名
            List<String> classNames = getClassNamesByBasePath(path, pack);

            //3.通过反射获取类对象
            for (String className:classNames){
                Class<?> clazz = Class.forName(className);
                //判断是否实现了接口，不包含本身
                if(interfac.isAssignableFrom(clazz)&&!clazz.equals(interfac)){
                    classes.add(clazz);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            classes.clear();
        }
        return classes;
    }

    /**
     * 通过给出的根路径来查找路径下所有的全类名
     * @param basePath 物理路径
     * @param basePack 基础包名
     * @return 全类名字符串集合
     */
    public static List<String> getClassNamesByBasePath(String basePath, String basePack){
        List<String> classNames = new ArrayList<>();
        try{
            File base = new File(basePath);
            File[] files = base.listFiles();
            for(File file:files){
                //文件
                if(file.isFile()){
                    String[] strings = file.getName().split(SYMBOL_SPLIT_SPOT);
                    //判断是不是字节码文件
                    if(BYTE_CODE_SUFFIX.equals(strings[strings.length-1])){
                        String cName = file.getName().replaceAll(SYMBOL_SPOT + BYTE_CODE_SUFFIX, BLOCK_STRING);
                        String className = basePack + SYMBOL_SPOT + cName;
                        classNames.add(className);
                    }
                }
                //文件夹
                else if(file.isDirectory()){
                    //递归获取全类名
                    String dirName = file.getName();
                    List<String> childNames = getClassNamesByBasePath(basePath + SYMBOL_SEPARATOR + dirName, basePack + SYMBOL_SPOT + dirName);
                    classNames.addAll(childNames);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            classNames.clear();
        }
        return classNames;
    }
}

