# GIRA后台开发指导文档

## 1. 技术栈与版本

### 1.1 核心依赖
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.2.0</spring-boot.version>
    <postgresql.version>42.7.1</postgresql.version>
    <h2database.version>2.2.224</h2database.version>
    <jjwt.version>0.11.5</jjwt.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <lombok.version>1.18.30</lombok.version>
    <springdoc.version>2.3.0</springdoc.version>
    <jacoco.version>0.8.11</jacoco.version>
    <surefire.version>3.2.2</surefire.version>
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

    <!-- OpenAPI Documentation -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>${springdoc.version}</version>
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

    <!-- 测试 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- Spring Boot Maven Plugin -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <version>${project.parent.version}</version>
        </plugin>

        <!-- Maven Compiler Plugin -->
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

        <!-- Maven Surefire Plugin -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>${surefire.version}</version>
            <configuration>
                <includes>
                    <include>**/*Test.java</include>
                </includes>
                <reportFormat>plain</reportFormat>
                <reportsDirectory>${project.build.directory}/test-results</reportsDirectory>
            </configuration>
        </plugin>

        <!-- JaCoCo Plugin -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>${jacoco.version}</version>
            <executions>
                <execution>
                    <id>prepare-agent</id>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>${project.build.directory}/coverage-reports</outputDirectory>
                    </configuration>
                </execution>
            </executions>
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
│   │               │   └── WebConfig.java
│   │               ├── controller/       # 控制器
│   │               │   ├── AuthController.java
│   │               │   ├── BoardController.java
│   │               │   ├── SprintController.java
│   │               │   ├── TaskController.java
│   │               │   └── UserController.java
│   │               ├── dto/             # 数据传输对象
│   │               │   ├── request/     # 请求对象
│   │               │   └── response/    # 响应对象
│   │               ├── entity/          # 实体类
│   │               ├── exception/       # 异常处理
│   │               ├── mapper/          # 对象映射
│   │               ├── repository/      # 数据访问层
│   │               ├── security/        # 安全相关
│   │               │   ├── JwtTokenProvider.java
│   │               │   └── SecurityConfig.java
│   │               └── service/         # 业务逻辑层
│   │                   ├── impl/        # 服务实现
│   │                   └── interfaces/  # 服务接口
│   └── resources/
│       ├── application.yml             # 主配置文件
│       ├── application-dev.yml         # 开发环境配置
│       └── application-prod.yml        # 生产环境配置
└── test/
    └── java/
        └── com/
            └── rayfay/
                └── gira/
                    ├── api/            # API测试
                    └── service/        # 服务层测试
```

## 3. 环境配置

### 3.1 主配置文件 (application.yml)
```yaml
spring:
  profiles:
    active: dev
  application:
    name: gira-backend
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
    serialization:
      write-dates-as-timestamps: false

server:
  port: 8080

springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    path: /swagger-ui.html 

security:
  jwt:
    secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
    expiration: ${JWT_EXPIRATION:86400000} # 24 hours
    refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000} # 7 days
```

### 3.2 开发环境 (application-dev.yml)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:gira;DB_CLOSE_DELAY=-1;MODE=MySQL
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console
      settings:
        web-allow-others: false
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  sql:
    init:
      mode: always
      schema-locations: classpath:db/schema-h2.sql
      data-locations: classpath:db/data-h2.sql
      platform: h2

security:
  jwt:
    secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
    expiration: 86400000  # 24小时

logging:
  level:
    root: INFO
    com.rayfay.gira: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

### 3.3 生产环境 (application-prod.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:gira}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
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
   - 安装Lombok插件
   - 启用注解处理器

### 4.2 数据库设置
1. 开发环境
   - 使用内嵌H2数据库
   - 访问H2控制台：http://localhost:8080/h2-console
   - JDBC URL: jdbc:h2:mem:gira
   - 用户名：sa
   - 密码：空

2. 生产环境
   - 安装PostgreSQL 14
   - 创建数据库和用户
   - 配置环境变量：
     - DB_HOST
     - DB_PORT
     - DB_NAME
     - DB_USERNAME
     - DB_PASSWORD

### 4.3 安全配置
1. JWT配置
   - 开发环境使用默认密钥
   - 生产环境必须设置环境变量：
     - JWT_SECRET：JWT签名密钥
     - JWT_EXPIRATION：访问令牌过期时间（毫秒）
     - JWT_REFRESH_EXPIRATION：刷新令牌过期时间（毫秒）

2. CORS配置
   - 开发环境允许所有源
   - 生产环境需要配置允许的源

### 4.4 测试配置
1. 单元测试
   - 使用JUnit 5
   - 使用Spring Boot Test
   - 使用H2内存数据库

2. 代码覆盖率
   - 使用JaCoCo生成覆盖率报告
   - 报告位置：target/coverage-reports

3. 测试报告
   - 使用Maven Surefire生成测试报告
   - 报告位置：target/test-results
``` 