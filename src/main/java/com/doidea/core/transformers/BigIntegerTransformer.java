package com.doidea.core.transformers;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * BigInteger oddModPow 方法修改
 */
public class BigIntegerTransformer implements IMyTransformer {

    @Override
    public String getTargetClassName() {
        return "java.math.BigInteger";
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
            String targetMethodName = "oddModPow";
            System.out.println(">>>> Target method name: " + targetMethodName);
            //CtClass[] paramTypes = {classPool.get(BigInteger.class.getName()), classPool.get(BigInteger.class.getName())};
            CtClass[] paramTypes = {classPool.get("java.math.BigInteger"), classPool.get("java.math.BigInteger")};
            String[] paramTypeNames = Stream.of(paramTypes).map(CtClass::getName).toArray(String[]::new);
            System.out.println(">>>> Target method param type: " + Arrays.toString(paramTypeNames));
            CtMethod declaredMethod = ctClass.getDeclaredMethod(targetMethodName, paramTypes);

            String testURL = "{java.math.BigInteger biObj = com.doidea.core.filters.BigIntegerFilter.testFilter($0, $1, $2);\n" +
                    "if (null != biObj) return biObj;}";

            declaredMethod.insertBefore(testURL);

            // 移除已加载的目标类对象，下次使用时重新加载新的类文件字节码，使修改生效
            ctClass.detach();
            // 返回修改后的字节码文件
            return ctClass.toBytecode();
        } catch (Throwable e) { // 捕获 ClassPool.getDefault() 异常
            System.err.println(">>>> BigIntegerTransformer getNewBytes error: " + e.getMessage());
            e.printStackTrace();
        }

        return classBytes;
    }
}
