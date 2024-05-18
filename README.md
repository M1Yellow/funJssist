## 说明

使用 `Javaagent` + `Javassist` 实现动态修改类方法。

可应用在 IntelliJ IDEA 30 天试用到期后，自动去掉 Licenses（许可证）弹窗，达到【无限试用】的效果。

目前仅短期测试了 `IntelliJ IDEA 2023.3.6` 版本正常使用，其他版本请自行测试。

本项目**仅供技术参考学习**！有条件的**请支持正版**或申请优惠渠道！

<br/>

## 使用

1. 下载部署 Oracle JDK 17 / Open JDK 17 环境
2. 下载配置 Maven 3.6.3 （或以上版本）
3. `git clone url` 克隆项目到本地
4. IDEA 打开项目，Maven package 打包
5. target 目录下的 `doidea-1.0.0-jar-with-dependencies.jar` 即为可用 jar 包，复制到一个目录，比如：`E:\DevRes\doidea`
6. `idea64.exe.vmoptions` 添加 `-javaagent:E:\DevRes\doidea\doidea-1.0.0-jar-with-dependencies.jar` 即可生效

<br/>

## 参考

- [Jetbrains系列破解思路的详细实现步骤探索](https://www.52pojie.cn/thread-1921814-1-1.html)
- [JetBrains 全家桶系列 2024 破解思路](https://www.52pojie.cn/thread-1919098-1-1.html)

<br/>
