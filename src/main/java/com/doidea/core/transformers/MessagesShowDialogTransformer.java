package com.doidea.core.transformers;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 消息弹窗方法修改
 */
public class MessagesShowDialogTransformer implements IMyTransformer {


    @Override
    public String getTargetClassName() {
        return "com." + "intel" + "lij" + ".openapi.ui.Messages";
    }

    @Override
    public byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return getNewBytes(className, classBytes);
    }

    private byte[] getNewBytes(String className, byte[] classBytes) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(className); // xxx.xxxx.xxx$xxx 格式
            String targetMethodName = "showDialog";
            System.out.println(">>>> Target method name: " + targetMethodName);
            // .method public static showDialog(Ljava/awt/Component;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;ILjavax/swing/Icon;)I
            // parent, message, title, options, defaultOptionIndex, icon // $3 -> title 第三个参数
            CtClass[] paramTypes = {classPool.get(java.awt.Component.class.getName()), classPool.get(String.class.getName()),
                    classPool.get(String.class.getName()), classPool.get(String[].class.getName()),
                    classPool.get(int.class.getName()), classPool.get(javax.swing.Icon.class.getName())};
            String[] paramTypeNames = Stream.of(paramTypes).map(CtClass::getName).toArray(String[]::new);
            System.out.println(">>>> Target method param type: " + Arrays.toString(paramTypeNames));
            CtMethod declaredMethod = ctClass.getDeclaredMethod(targetMethodName, paramTypes);

            // 打印调用堆栈 new RuntimeException().printStackTrace(); 可以写多个 insertBefore，后定义的先执行
            //declaredMethod.insertBefore("{new RuntimeException().printStackTrace();}");

            // 去掉试用已到期提示弹窗
            declaredMethod.insertBefore("{String title = $3;\n" +
                    "if (title.trim().contains(\"trial has expired\") || title.trim().contains(\"试用已到期\")) " +
                    "{System.out.println(title);\nreturn 0;}}");

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
