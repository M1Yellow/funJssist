package com.doidea.core.transformers;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * DNS 域名解析方法修改
 */
public class InetAddressTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "java.net.InetAddress";
    }

    @Override
    public byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return getNewBytes(className, classBytes);
    }

    private byte[] getNewBytes(String className, byte[] classBytes) {
        try {
            // javassist 默认类池
            ClassPool classPool = ClassPool.getDefault();
            // 获取目标类
            CtClass ctClass = classPool.get(className); // xxx.xxxx.xxx$xxx 格式
            // 获取目标方法
            String targetMethodName = "getAllByName";
            System.out.println(">>>> Target method name: " + targetMethodName);
            //CtClass[] paramTypes = {classPool.get(String.class.getName()), classPool.get(InetAddress.class.getName())};
            CtClass[] paramTypes = {classPool.get("java.lang.String"), classPool.get("java.net.InetAddress")};
            String[] paramTypeNames = Stream.of(paramTypes).map(CtClass::getName).toArray(String[]::new);
            System.out.println(">>>> Target method param type: " + Arrays.toString(paramTypeNames));
            CtMethod declaredMethod = ctClass.getDeclaredMethod(targetMethodName, paramTypes);

            // 修改代码，用类全路径名，注意别遗漏方法名！
            String testURL = "{com.doidea.core.filters.DNSFilter.testQuery($1);}";
            // 在方法前插入修改代码
            declaredMethod.insertBefore(testURL);

            // 继续下一个方法
            targetMethodName = "isReachable";
            System.out.println(">>>> Target method name: " + targetMethodName);
            CtClass[] paramTypes2 = {classPool.get(NetworkInterface.class.getName()), classPool.get(int.class.getName()), classPool.get(int.class.getName())};
            paramTypeNames = Stream.of(paramTypes2).map(CtClass::getName).toArray(String[]::new);
            System.out.println(">>>> Target method param type: " + Arrays.toString(paramTypeNames));
            declaredMethod = ctClass.getDeclaredMethod(targetMethodName, paramTypes2);

            testURL = "{com.doidea.core.filters.DNSFilter.testReachable($0);}";
            declaredMethod.insertBefore(testURL);

            // 移除已加载的目标类对象，下次使用时重新加载新的类文件字节码，使修改生效
            ctClass.detach();
            // 返回修改后的字节码文件
            return ctClass.toBytecode();
        } catch (Throwable e) { // 捕获 ClassPool.getDefault() 异常
            System.err.println(">>>> InetAddressTransformer getNewBytes error: " + e.getMessage());
            e.printStackTrace();
        }

        return classBytes;
    }
}
