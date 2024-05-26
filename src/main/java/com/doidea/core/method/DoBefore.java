package com.doidea.core.method;

/**
 * 在目标方法执行之前处理
 */
public interface DoBefore {
    /**
     * JDialog SetTitle 之前执行
     *
     * @param methodObj 目标方法对象
     * @return byte[] 类修改后的字节码
     */
    byte[] doBeforeJDialogSetTitle(Object methodObj);
}
