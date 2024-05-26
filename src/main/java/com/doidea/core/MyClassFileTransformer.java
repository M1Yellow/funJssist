package com.doidea.core;

import com.doidea.core.bo.TargetMethod;
import com.doidea.core.method.impl.DoBeforeDispatcher;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.stream.Stream;

/**
 * 类文件转换处理
 */
public class MyClassFileTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        //System.out.println(">>>> loading Class: " + className); // className 为 xxx/xxxx/xxx$xxx 格式
        String targetClassName = className.replace("/", "."); // targetClass 为 xxx.xxxx.xxx$xxx 格式
        List<TargetMethod> targetMethods = Launcher.targetClassMethodMap.get(targetClassName);
        if (targetMethods == null || targetMethods.isEmpty()) return classfileBuffer;

        // 命中目标类和方法
        System.out.println(">>>> Target Class: " + targetClassName);
        try {
            return getNewBytes(loader, targetClassName, classfileBuffer);
        } catch (Throwable e) {
            System.err.println(">>>> getNewBytes error: " + e.getMessage());
            e.printStackTrace();
        }

        return classfileBuffer;
    }

    /**
     * 获取修改后的字节码
     */
    private byte[] getNewBytes(ClassLoader loader, String targetClassName, byte[] classfileBuffer) {
        List<TargetMethod> targetMethods = Launcher.targetClassMethodMap.get(targetClassName);
        byte[] newBytes = null;
        for (TargetMethod targetMethod : targetMethods) {
            System.out.println(">>>> targetMethod: " + targetMethod.getTargetMethodName());
            newBytes = DoBeforeDispatcher.INSTANCE.doBeforeJDialogSetTitle(targetMethod);
        }

        if (newBytes != null) return newBytes;
        return classfileBuffer;
    }


    /**
     * 类名数组转 getDeclaredMethod 所需要的参数类型数组
     *
     * @param classPool  ClassPool 对象
     * @param classNames String[] 类名数组
     * @return CtClass[] getDeclaredMethod 所需要的参数类型数组
     */
    public static CtClass[] classNamesToCtClassArr(ClassPool classPool, String[] classNames) {
        if (classNames == null || classNames.length < 1) return new CtClass[]{};
        if (classPool == null) classPool = ClassPool.getDefault();
        String[] newClassNames =
                Stream.of(classNames).filter(cname -> cname != null && !cname.isBlank()).toArray(String[]::new);
        if (newClassNames.length < 1) return new CtClass[]{};
        CtClass[] paramTypes = new CtClass[newClassNames.length];
        for (int i = 0; i < newClassNames.length; i++) {
            try {
                paramTypes[i] = classPool.get(classNames[i]);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return paramTypes;
    }
}
