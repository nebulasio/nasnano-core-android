# neb_wallet_core使用说明

### 依赖说明

neb_wallet_core由于是以本地aar文件提供依赖，所以涉及到的第三方依赖需要单独处理。
涉及到的第三方依赖如下：

`'com.google.protobuf:protobuf-java:3.5.1'`
`'com.google.code.gson:gson:2.8.2'`

需要在module下的build.gradle中的dependencies添加如下依赖：

```groovy
    implementation 'com.google.code.gson:gson:2.8.2'
    implementation 'com.google.protobuf:protobuf-java:3.5.1'
```