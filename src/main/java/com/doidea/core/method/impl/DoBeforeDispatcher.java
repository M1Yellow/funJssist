package com.doidea.core.method.impl;

import com.doidea.core.MyClassFileTransformer;
import com.doidea.core.method.DoBefore;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

import java.util.Arrays;
import java.util.stream.Stream;

public enum DoBeforeDispatcher implements DoBefore {

    INSTANCE;

    @Override
    public byte[] doBeforeJDialogSetTitle(String targetClassName, String targetMethodName, String[] paramClassNames) {
        try {
            /*
            TODO ClassPool.getDefault() 需要依赖 javassist jar 包
            MANIFEST.MF 中指定 lib/javassist-3.30.2-GA.jar 无效
            使用 maven-assembly-plugin 打包
            或者 JVM 启动参数添加：
            -Xbootclasspath/a:E:\DevRes\doidea\javassist-3.30.2-GA.jar
            */
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(targetClassName); // xxx.xxxx.xxx$xxx 格式
            //CtMethod declaredMethod = cc.getDeclaredMethod(targetMethodName); // 可能会有多个重载方法
            // 指定方法参数类型，区分重载方法
            // CtClass type 常量只有基础数据类型
            // 引用类型，String.class.getName()、int[].class.getName()、byte[].class.getName()
            CtClass[] paramTypes = MyClassFileTransformer.classNamesToCtClassArr(classPool,
                    new String[]{String.class.getName()});
            String[] paramTypeNames = Stream.of(paramTypes).map(CtClass::getName).toArray(String[]::new);
            System.out.println(">>>> " + targetMethodName + " 参数类型：" + Arrays.toString(paramTypeNames));
            CtMethod declaredMethod = ctClass.getDeclaredMethod(targetMethodName, paramTypes);
            // 设置访问权限
            declaredMethod.setModifiers(Modifier.PUBLIC);

            // 目前没使用修改 body 的方式
            /*
            String mBody = "{DialogWrapper dw = this.myDialogWrapper.get();\n"
                    + "System.out.println(\">>>> DialogWrapper title: \" + dw.getTitle());\n"
                    + "if (dw.getTitle().equalsIgnoreCase(\"Licenses\") || dw.getTitle().equalsIgnoreCase(\"许可证\")) {return null};\n"
                    + "return dw;\n}";
            System.out.println(">>>> Target Class mBody: " + mBody);
            declaredMethod.setBody(mBody);
            */

            // TODO 巧妙利用方法内抛异常的方式，终止窗口创建，达到去掉弹窗的效果。异常信息建议不写，防止异常信息上报（有可能会上报）
            //  因为是根据窗口标题判断，目前只对英文版和简体中文版做了判断，其他语言版本，自行添加判断即可
            declaredMethod.insertBefore("{String title = $1;\nSystem.out.println(title);\nif (title.trim().equalsIgnoreCase(\"Licenses\") || title.trim().equalsIgnoreCase(\"许可证\")) {throw new RuntimeException();}}");

            // 移除已加载的目标类对象，下次使用时重新加载新的类文件字节码，使修改生效
            ctClass.detach();
            // 返回修改后的字节码文件
            return ctClass.toBytecode();
        } catch (Throwable e) { // 捕获 ClassPool.getDefault() 异常
            System.err.println(">>>> doBeforeJDialogSetTitle error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
