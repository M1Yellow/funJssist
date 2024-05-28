## 说明

使用 `Javaagent` + `Javassist` 实现无侵入动态修改类方法。ASM 版本已实现 👉 **[doidea-asm](https://github.com/M1Yellow/doidea-asm)**

可应用在 IDEA 启动时，未激活/未试用/试用到期状态下，自动去掉 Licenses（许可证）弹窗，达到【无限试用】的效果。

目前仅短期测试了以下版本正常使用，其他开发工具/版本请自行修改测试。
- IDEA 2023.3.6
- IDEA 2024.1.1
- IDEA 2024.1.2



本项目**仅供技术参考学习**！有条件的**请支持正版**或申请优惠渠道！



<br/>

## 使用

1. 官网下载对应版本的 IDEA，建议下载压缩包版本
2. 下载部署 Oracle JDK 17 / Open JDK 17 环境
3. 下载配置 Maven 3.6.3 （或更高版本）
4. `git clone url` 克隆项目到本地
5. IDEA 打开项目（打开 doidea 文件夹即可），Maven package 打包
6. target 目录下的 `doidea-1.0.0-jar-with-dependencies.jar` 即为可用 jar 包，复制到一个目录，比如：`E:\DevRes\doidea`
7. `idea64.exe.vmoptions` 添加 `-javaagent:E:\DevRes\doidea\doidea-1.0.0-jar-with-dependencies.jar` 即可生效



<br/>

## 参考

- [Javassist 官网](http://www.javassist.org)
- [通过实战走近Java Agent探针技术](https://juejin.cn/post/7025410644463583239)
- [Java Agent学习](https://www.yijinglab.com/specialized/20211214150751)
- [Java 类字节码编辑 - Javassist](https://javasec.org/javase/JavaByteCode/Javassist.html)
- [字节码增强技术探索](https://tech.meituan.com/2019/09/05/java-bytecode-enhancement.html)
- [Jexbraxxs系列破解思路的详细实现步骤探索](https://www.52pojie.cn/thread-1921814-1-1.html)
- [JexBraxxs 全家桶系列 2024 破解思路](https://www.52pojie.cn/thread-1919098-1-1.html)



<br/>
