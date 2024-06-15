package com.doidea.core.transformers;

/**
 * 扩展处理 Javassist transform 没有转换的类
 */
public class ExtendTransformer {

    /**
     * 额外处理 transform
     */
    public void doExtendTransform() throws Exception {
        redoInetAddressTransform();
        redoBigIntegerTransform();
    }

    /**
     * Javassist transform 没有加载 java.net.InetAddress <br>
     * 需要手动重新 transform
     */
    public void redoInetAddressTransform() throws Exception {
        new InetAddressTransformer().transform("java.net.InetAddress", null, 0);
    }

    /**
     * Javassist transform 没有加载 java.math.BigInteger <br>
     * 需要手动重新 transform
     */
    public void redoBigIntegerTransform() throws Exception {
        new BigIntegerTransformer().transform("java.math.BigInteger", null, 0);
    }
}
