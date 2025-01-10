# GIRA后台开发指导文档

## 1. 技术栈与版本

### 1.1 核心依赖
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.2.0</spring-boot.version>
    <postgresql.version>42.7.1</postgresql.version>
    <h2database.version>2.2.224</h2database.version>
    <jjwt.version>0.12.3</jjwt.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <lombok.version>1.18.30</lombok.version>
</properties>

<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- 数据库 -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>${jjwt.version}</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>${jjwt.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>${jjwt.version}</version>
        <scope>runtime</scope>
    </dependency>

    <!-- 工具 -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <version>${spring-boot.version}</version>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>${java.version}</source>
                <target>${java.version}</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 2. 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── rayfay/
│   │           └── gira/
│   │               ├── GiraApplication.java
│   │               ├── config/           # 配置类
│   │               │   ├── SecurityConfig.java
│   │               │   ├── JpaConfig.java
│   │               │   └── WebConfig.java
│   │               ├── controller/       # 控制器
│   │               │   ├── AuthController.java
│   │               │   ├── BoardController.java
│   │               │   ├── SprintController.java
│   │               │   └── TaskController.java
│   │               ├── dto/             # 数据传输对象
│   │               │   ├── request/
│   │               │   └── response/
│   │               ├── entity/          # 实体类
│   │               ├── mapper/          # 对象映射
│   │               ├── repository/      # 数据访问层
│   │               ├── security/        # 安全相关
│   │               │   ├── JwtTokenProvider.java
│   │               │   └── UserDetailsServiceImpl.java
│   │               └── service/         # 业务逻辑层
│   │                   ├── impl/
│   │                   └── interfaces/
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── application-prod.yml
└── test/
    └── java/
        └── com/
            └── rayfay/
                └── gira/
                    └── api/             # API测试
```

## 3. 环境配置

### 3.1 开发环境 (application-dev.yml)
```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/gira-dev
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console

security:
  jwt:
    secret: your-dev-secret-key
    expiration: 86400000  # 24小时

logging:
  level:
    com.rayfay.gira: DEBUG
    org.springframework: INFO
```

### 3.2 生产环境 (application-prod.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:gira}
    driver-class-name: org.postgresql.Driver
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: none
    show-sql: false

security:
  jwt:
    secret: ${JWT_SECRET}
    expiration: ${JWT_EXPIRATION:86400000}

logging:
  level:
    com.rayfay.gira: INFO
    org.springframework: WARN
```

### 3.3 主配置文件 (application.yml)
```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  application:
    name: gira-backend

server:
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: /api

springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
```

## 4. 开发环境设置

### 4.1 本地开发环境搭建
1. 安装OpenJDK 21
   ```bash
   # Windows使用scoop安装
   scoop install openjdk21

   # 或使用官方安装包
   # 下载地址：https://jdk.java.net/21/
   ```

2. 配置Maven
   - 下载最新版Maven (3.9.6)
   - 配置JAVA_HOME指向OpenJDK 21安装目录
   - 配置MAVEN_HOME和PATH

3. IDE配置
   - 使用IntelliJ IDEA 2023.3或更高版本
   - 确保IDE使用JDK 21
   - 启用Lombok插件
   - 配置MapStruct支持

### 4.2 本地启动步骤
1. 克隆项目
```bash
git clone https://github.com/rayfay/gira.git
cd gira/app/gira-backend
```

2. 编译项目
```bash
mvn clean install
```

3. 运行项目
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### 4.3 开发模式特性
1. H2数据库
   - 内存数据库，无需额外安装
   - 访问H2控制台: http://localhost:8080/api/h2-console
   - JDBC URL: jdbc:h2:file:./data/gira-dev

2. 自动重载
   - 使用spring-boot-devtools
   - 修改代码后自动重启应用

3. API文档
   - Swagger UI: http://localhost:8080/api/swagger-ui.html
   - API文档: http://localhost:8080/api/v3/api-docs

### 4.4 调试功能
1. 远程调试
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

2. 日志配置
```yaml
logging:
  file:
    name: logs/gira-dev.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 5. 开发规范

### 5.1 代码规范
1. 使用Lombok简化代码
2. 使用MapStruct进行对象映射
3. 统一的异常处理
4. 统一的返回格式

### 5.2 提交规范
1. 提交信息格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

2. Type类型
   - feat: 新功能
   - fix: 修复
   - docs: 文档
   - style: 格式
   - refactor: 重构
   - test: 测试
   - chore: 构建过程或辅助工具的变动

### 5.3 测试规范
1. 单元测试
   - 使用JUnit 5
   - 测试覆盖率要求>80%

2. API测试
   - 使用@SpringBootTest
   - 测试所有API接口
``` 