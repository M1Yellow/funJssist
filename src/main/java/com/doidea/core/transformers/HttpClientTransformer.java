package com.doidea.core.transformers;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * HttpClient 方法修改
 */
public class HttpClientTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "sun.net.www.http.HttpClient";
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
            String targetMethodName = "openServer";
            System.out.println(">>>> Target method name: " + targetMethodName);
            CtClass[] paramTypes = new CtClass[]{};
            String[] paramTypeNames = Stream.of(paramTypes).map(CtClass::getName).toArray(String[]::new);
            System.out.println(">>>> Target method param type: " + Arrays.toString(paramTypeNames));
            CtMethod declaredMethod = ctClass.getDeclaredMethod(targetMethodName, paramTypes);

            // TODO 修改代码，用类全路径名，很容易遗漏方法名，或者写错类名！！
            String testURL = "{com.doidea.core.filters.URLFilter.testURL($0.url);}";
            // 在方法前插入修改代码
            declaredMethod.insertBefore(testURL);

            // 移除已加载的目标类对象，下次使用时重新加载新的类文件字节码，使修改生效
            ctClass.detach();
            // 返回修改后的字节码文件
            return ctClass.toBytecode();
        } catch (Throwable e) { // 捕获 ClassPool.getDefault() 异常
            System.err.println(">>>> HttpClientTransformer getNewBytes error: " + e.getMessage());
            e.printStackTrace();
        }

        return classBytes;
    }
}
