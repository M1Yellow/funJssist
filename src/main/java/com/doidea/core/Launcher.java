package com.doidea.core;

import com.doidea.core.constant.GlobalConstant;
import com.doidea.core.util.SimpleUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Launcher {

    private static volatile boolean loaded = false;

    public static void main(String[] args) {
        // 程序自身的日志不会在IDEA日志中打印
        System.out.println(">>>> Launcher main >>>>");
    }

    /**
     * 在 JVM 启动前加载
     *
     * @param args
     * @param instrumentation
     */
    public static void premain(String args, Instrumentation instrumentation) {

        if (loaded) {
            System.err.println(">>>> multiple javaagent jar.");
            return;
        }

        try {
            MyClassFileTransformer transformer = new MyClassFileTransformer();
            instrumentation.addTransformer(transformer);
            //instrumentation.addTransformer(transformer, true);
            loaded = true;
        } catch (Exception e) {
            System.err.println(">>>> instrumentation addTransformer failed.");
            e.printStackTrace();
        }
    }

    public static class MyClassFileTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

            //System.out.println(">>>> Class 已加载：" + className);
            // TODO 包名关键字简单加密混淆一下，稍微减缓 DMCA 的速度
            String modifyClassName = "com." + SimpleUtil.xor(GlobalConstant.IJ_L_XOR, null) + ".openapi.ui.DialogWrapper";
            String modifyClassMethod = "setTitle";
            //String modifyClassName = "com." + SimpleUtil.xor(GlobalConstant.IJ_L_XOR, null) + ".openapi.ui.impl.DialogWrapperPeerImpl$MyDialog";
            //String modifyClassMethod = "getDialogWrapper"; // final class 中的方法也能改
            String loadClassName = modifyClassName.replace(".", "/");
            // 表示找到了这个类
            //if (className.contains("DialogWrapperPeerImpl")) {
            if (className.equals(loadClassName)) {
                System.out.println(">>>> Target Class: " + "0000");
                System.out.println(">>>> Target Class: " + className);
                // 开始使用当前的javassist修改字节码文件
                try {
                    System.out.println(">>>> Target Class: " + "0001");
                    /*
                    TODO ClassPool.getDefault() 需要依赖 javassist jar 包，否则这个语句不执行，也不报错！！
                    MANIFEST.MF 中指定 lib/javassist-3.30.2-GA.jar 无效
                    使用 maven-assembly-plugin 打包
                    或者 JVM 启动参数添加：
                    -Xbootclasspath/a:E:\DevRes\doidea\javassist-3.30.2-GA.jar
                     */
                    ClassPool cp = ClassPool.getDefault();
                    System.out.println(">>>> Target Class: " + "0001.1");
                    CtClass cc = cp.get(modifyClassName); // xxx.xxxx.xxx xxx.xxxx.xxx$xxx 格式
                    System.out.println(">>>> Target Class: " + "0001.2");
                    // 判断类是否被冻结
                    if (cc.isFrozen()) {
                        System.out.println(">>>> Target Class isFrozen.");
                        cc.defrost();
                    }
                    System.out.println(">>>> Target Class: " + "0002");
                    //CtMethod declaredMethod = cc.getDeclaredMethod(modifyClassMethod, new CtClass[]{CtClass.charType});
                    CtMethod declaredMethod = cc.getDeclaredMethod(modifyClassMethod);
                    // 设置访问权限
                    declaredMethod.setModifiers(Modifier.PUBLIC);
                    //cc.removeMethod(declaredMethod);

                    System.out.println(">>>> Target Class: " + "0003");
                    // 目前没使用修改 body 的方式
                    /*
                    String mBody = "{DialogWrapper dw = this.myDialogWrapper.get();\n"
                            + "System.out.println(\">>>> DialogWrapper title: \" + dw.getTitle());\n"
                            + "if (dw.getTitle().equalsIgnoreCase(\"Licenses\") || dw.getTitle().equalsIgnoreCase(\"许可证\")) {return null};\n"
                            + "return dw;\n}";
                    System.out.println(">>>> Target Class mBody: " + mBody);
                    declaredMethod.setBody(mBody);
                    */

                    // TODO 巧妙利用方法内抛异常的方式，终止窗口创建，达到去掉弹窗的效果
                    declaredMethod.insertBefore("{String title = $1;\nSystem.out.println(title);\nif (title.equalsIgnoreCase(\"Licenses\") || title.equalsIgnoreCase(\"许可证\")) {throw new RuntimeException(\"Licenses dialog abort.\");}}");

                    // detach 将内存中曾经被 javassist 加载过的 CtClass 类对象移除，下次调用重新走 javassist 加载
                    cc.detach();
                    System.out.println(">>>> Target Class: " + "0004");
                    return cc.toBytecode();
                } catch (Exception e) {
                    System.err.println(">>>> do transform Exception: " + e.getMessage());
                    e.printStackTrace();
                } catch (Throwable e) { // 捕获 ClassPool.getDefault() 异常
                    System.err.println(">>>> do transform Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            //return ClassFileTransformer.super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            //return classfileBuffer;
            // 返回null则字节码不会被修改
            return null;
        }
    }
}
