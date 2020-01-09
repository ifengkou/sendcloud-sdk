## sendcloud-sdk: 发短信、邮件、语音短信

由于sendcloud 提供的是 源码zip包[「源码路径」](https://www.sendcloud.net/doc/sdk/downloads/sendcloud-sdk.zip)，在工程集成时特别不方便

现将其发布成到Github上的私有maven仓库

### 使用

1. 在pom.xml 文件的 repositories 节点下添加 Github 私有maven仓库地址
```xml
    <repositories>
        <repository>
            <id>mvn-repo</id>
            <url>https://raw.github.com/ifengkou/mvn-repo/master</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>
```
2. 在pom.xml 文件的 repositories 节点下添加项目的引用

```xml
    <dependencies>
        <dependency>
            <groupId>com.sendcloud</groupId>
            <artifactId>sendcloud-sdk</artifactId>
            <version>1.2</version>
        </dependency>
    </dependencies>
```

3. sdk 使用文档：

```html
https://www.sendcloud.net/doc/sdk/
```