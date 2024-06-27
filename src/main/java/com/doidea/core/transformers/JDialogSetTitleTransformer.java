package com.doidea.core.transformers;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Licenses（许可证）弹窗设置标题方法修改
 */
public class JDialogSetTitleTransformer implements IMyTransformer {


    @Override
    public String getTargetClassName() {
        return "com." + "intel" + "lij" + ".openapi.ui.DialogWrapper";
    }

    @Override
    public byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return getNewBytes(className, classBytes);
    }

    private byte[] getNewBytes(String className, byte[] classBytes) {
        try {
            /*
            TODO ClassPool.getDefault() 需要依赖 javassist jar 包
            MANIFEST.MF 中指定 lib/javassist-3.30.2-GA.jar 无效
            使用 maven-assembly-plugin 打包
            或者 JVM 启动参数添加：
            -Xbootclasspath/a:E:\DevRes\doidea\javassist-3.30.2-GA.jar
            */
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(className); // xxx.xxxx.xxx$xxx 格式
            String targetMethodName = "setTitle";
            System.out.println(">>>> Target method name: " + targetMethodName);
            //CtMethod declaredMethod = cc.getDeclaredMethod(targetMethod.getTargetMethodName()); // 可能会有多个重载方法
            // 指定方法参数类型，区分重载方法
            // CtClass type 常量只有基础数据类型
            // 引用类型，String.class.getName()、int[].class.getName()、byte[].class.getName()
            // .method public setTitle(Ljava/lang/String;)V
            CtClass[] paramTypes = {classPool.get(String.class.getName())};
            String[] paramTypeNames = Stream.of(paramTypes).map(CtClass::getName).toArray(String[]::new);
            System.out.println(">>>> Target method param type: " + Arrays.toString(paramTypeNames));
            CtMethod declaredMethod = ctClass.getDeclaredMethod(targetMethodName, paramTypes);
            // 设置访问权限，本身就是 public，不用设置
            //declaredMethod.setModifiers(Modifier.PUBLIC);

            // 目前没使用修改 body 的方式
            /*
            String mBody = "{DialogWrapper dw = this.myDialogWrapper.get();\n"
                    + "System.out.println(\">>>> DialogWrapper title: \" + dw.getTitle());\n"
                    + "if (dw.getTitle().equalsIgnoreCase(\"Licenses\") || dw.getTitle().equalsIgnoreCase(\"许可证\")) {return null};\n"
                    + "return dw;\n}";
            System.out.println(">>>> Target Class mBody: " + mBody);
            declaredMethod.setBody(mBody);
            */

            // 打印调用堆栈 new RuntimeException().printStackTrace(); 可以写多个 insertBefore，后定义的先执行
            //declaredMethod.insertBefore("{new RuntimeException().printStackTrace();}");

            // TODO 巧妙利用方法内抛异常的方式，终止窗口创建，达到去掉 Licenses（许可证）弹窗的效果。异常信息建议不写，防止异常信息上报（有可能会上报）
            //  因为是根据窗口标题判断，目前只对英文版和简体中文版做了判断，其他语言版本，自行添加判断即可
            declaredMethod.insertBefore("{String title = $1;\n" +
                    "if (title.trim().equalsIgnoreCase(\"Licenses\") || title.trim().equalsIgnoreCase(\"许可证\")) " +
                    "{System.out.println(title);\nthrow new RuntimeException();}}");

            /*
            // 去掉【试用已到期】弹窗，这个弹窗直接抛异常会导致程序异常报错，不能正常启动，不能直接抛异常
            declaredMethod.insertBefore("{String title = $1;\nSystem.out.println(title);\n" +
                    "if (title.trim().contains(\"trial has expired\") || title.trim().contains(\"试用已到期\")) " +
                    "{throw new RuntimeException();}}");
            */

            // 移除已加载的目标类对象，下次使用时重新加载新的类文件字节码，使修改生效
            ctClass.detach();
            // 返回修改后的字节码文件
            return ctClass.toBytecode();
        } catch (Throwable e) { // 捕获 ClassPool.getDefault() 异常
            System.err.println(">>>> JDialogSetTitleTransformer getNewBytes error: " + e.getMessage());
            e.printStackTrace();
        }

        return classBytes;
    }
}
