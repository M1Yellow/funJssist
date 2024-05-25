package com.doidea.core.method;

/**
 * 在目标方法执行之前处理
 */
public interface DoBefore {
    /**
     * JDialog SetTitle 之前执行
     *
     * @param targetClassName 目标类名
     * @param targetMethodName 目标方法名
     * @param paramClassNames 方法参数类型名称数组
     * @return byte[] 类修改后的字节码
     */
    byte[] doBeforeJDialogSetTitle(String targetClassName, String targetMethodName, String[] paramClassNames);
}
