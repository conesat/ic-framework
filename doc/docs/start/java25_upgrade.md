# Java 25 环境升级与清理指南

本文档记录了将IC Framework开发环境从Java 21升级到Java 25以及Maven从3.9.11升级到Maven 4.0.0 RC-4的过程，并清理了系统中其他Java版本，确保仅保留Java 25作为唯一Java环境。

## 升级与清理背景

为了使用Java的最新特性和性能改进，我们将开发环境升级到Java 25。同时，为了更好地支持Java 25，我们也升级到了Maven 4.0.0 RC-4。此外，为了确保环境的一致性和避免版本冲突，我们清理了系统中其他Java版本，仅保留Java 25作为唯一Java环境。

## 升级与清理步骤

### 1. 安装Java 25

```bash
# 通过Homebrew安装Java 25
brew install openjdk@25

# 验证安装
java -version
```

### 2. 清理其他Java版本

为了确保环境的一致性，我们建议清理系统中其他Java版本：

```bash
# 检查当前安装的所有Java版本
/usr/libexec/java_home -V

# 卸载通过Homebrew安装的其他Java版本（如openjdk@21）
brew uninstall openjdk@21

# 删除/Library/Java/JavaVirtualMachines/目录下其他版本
sudo rm -rf /Library/Java/JavaVirtualMachines/*

# 删除用户目录下的其他Java版本
rm -rf ~/Library/Java/JavaVirtualMachines/*

# 清理jenv中不需要的Java版本链接
rm -rf ~/.jenv/versions/*

# 添加Java 25到jenv并设置为全局默认版本
jenv add /opt/homebrew/Cellar/openjdk/25/libexec/openjdk.jdk/Contents/Home
jenv global 25
```

### 3. 安装Maven 4.0.0 RC-4

如果需要将 Maven 4 迁移到指定目录（如 [/Volumes/data/maven](file:///Volumes/data/maven)），可以使用以下步骤：

```bash
# 将 Maven 4 迁移到指定目录
sudo mv /opt/apache-maven-4.0.0-rc-4 /Volumes/data/maven/apache-maven-4.0.0-rc-4

# 更新环境变量
sed -i '' 's|/opt/apache-maven-4.0.0-rc-4|/Volumes/data/maven/apache-maven-4.0.0-rc-4|g' ~/.zshrc

# 重新加载环境变量
source ~/.zshrc

# 验证安装
mvn -version
```

由于Maven 4尚未正式发布，我们需要手动下载并安装预览版本：

```bash
# 下载Maven 4.0.0 RC-4
cd /tmp
curl -O https://downloads.apache.org/maven/maven-4/4.0.0-rc-4/binaries/apache-maven-4.0.0-rc-4-bin.tar.gz

# 解压文件
tar -xzf apache-maven-4.0.0-rc-4-bin.tar.gz

# 移动到合适位置
sudo mv apache-maven-4.0.0-rc-4 /Volumes/data/maven/apache-maven-4.0.0-rc-4

# 添加到PATH环境变量
echo 'export PATH="/Volumes/data/maven/apache-maven-4.0.0-rc-4/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 验证安装
mvn -version
```

### 3. 更新项目配置

我们需要更新项目中的Java版本配置：

1. 更新根目录pom.xml中的Java版本：
   ```xml
   <java.version>25</java.version>
   <maven.compiler.source>25</maven.compiler.source>
   <maven.compiler.target>25</maven.compiler.target>
   ```

2. 更新ic-framework-dependencies模块中的Java版本配置

3. 更新ic-framework-version-plugin模块中的依赖版本

### 4. 配置Maven Toolchain

为了确保Maven能正确使用Java 25，我们需要配置toolchain：

在`~/.m2/toolchains.xml`中添加以下内容：

```xml
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>25</version>
      <vendor>openjdk</vendor>
    </provides>
    <configuration>
      <jdkHome>/opt/homebrew/Cellar/openjdk/25/libexec/openjdk.jdk/Contents/Home</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

## 构建项目

使用Java 25和Maven 4构建项目：

```bash
# 设置环境变量
export JAVA_HOME=/opt/homebrew/Cellar/openjdk/25/libexec/openjdk.jdk/Contents/Home
export PATH="/Volumes/data/maven/apache-maven-4.0.0-rc-4/bin:$JAVA_HOME/bin:$PATH"

# 构建项目（跳过测试）
mvn clean install -DskipTests

# 或者跳过version插件模块构建（如果仍有兼容性问题）
mvn clean install -DskipTests -pl '!ic-framework-version-plugin'
```

## 已知问题

### Maven插件兼容性问题

目前Maven 4.0.0 RC-4与ic-framework-version-plugin模块仍存在兼容性问题，表现为：
```
Unsupported class file major version 69
```

这是由于Maven插件工具尚未完全支持Java 25的class文件格式。作为临时解决方案，可以在构建时跳过该模块：
```bash
mvn clean install -DskipTests -pl '!ic-framework-version-plugin'
```

## 验证升级与清理结果

升级与清理完成后，可以通过以下方式验证环境配置：

1. 检查Java版本：
   ```bash
   java -version
   ```

2. 检查系统中安装的Java版本：
   ```bash
   /usr/libexec/java_home -V
   ```

3. 检查Maven版本：
   ```bash
   mvn -version
   ```

4. 构建项目并运行测试

## 注意事项

1. Maven 4.0.0 RC-4是预览版本，不建议在生产环境中使用
2. 系统中仅保留Java 25版本，如需其他Java版本需重新安装
3. 建议定期检查Maven官方发布，及时更新到稳定版本

## 参考资料

- [Maven下载页面](https://maven.apache.org/download.cgi)
- [OpenJDK 25文档](https://openjdk.java.net/projects/jdk/25/)