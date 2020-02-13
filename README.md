# [springboot和springsecurity整合OAuth2](https://www.cnblogs.com/ifme/p/12188982.html)
https://www.cnblogs.com/ifme/p/12188982.html

## 1\. OAuth2.0介绍


`OAuth`（开放授权）是一个开放标准，允许用户授权第三方应用访问他们存储在另外的服务提供者上的信息，而不需要将用户名和密码提供给第三方应用或分享他们数据的所有内容。`OAuth2.0`是`OAuth`协议的延续版本，但不向后兼容`OAuth 1.0`即完全废止了`OAuth1.0`。很多大公司如`Google`，`Yahoo`，`Microsoft`等都提供了`OAUTH`认证服务，这些都足以说明`OAUTH`标准逐渐成为开放资源授权的标准。
`Oauth`协议目前发展到2.0版本，1.0版本过于复杂，2.0版本已得到广泛应用。

下边分析一个 `Oauth2`认证的例子，通过例子去理解`OAuth2.0`协议的认证流程，本例子是黑马程序员网站使用微信认证的过程，这个过程的简要描述如下：

用户借助微信认证登录黑马程序员网站，用户就不用单独在黑马程序员注册用户，怎么样算认证成功吗？黑马程序
员网站需要成功从微信获取用户的身份信息则认为用户认证成功，那如何从微信获取用户的身份信息？用户信息的
拥有者是用户本人，微信需要经过用户的同意方可为黑马程序员网站生成令牌，黑马程序员网站拿此令牌方可从微
信获取用户的信息。

*   1、 客户端请求第三方授权
    用户进入黑马程序的登录页面，点击微信的图标以微信账号登录系统，用户是自己在微信里信息的资源拥有者。
    点击“微信”出现一个二维码，此时用户扫描二维码，开始给黑马程序员授权。
*   2、 资源拥有者同意给客户端授权
    资源拥有者扫描二维码表示资源拥有者同意给客户端授权，微信会对资源拥有者的身份进行验证， 验证通过后，微信会询问用户是否给授权黑马程序员访问自己的微信数据，用户点击“确认登录”表示同意授权，微信认证服务器会颁发一个授权码，并重定向到黑马程序员的网站。
*   3、 客户端获取到授权码，请求认证服务器申请令牌
    此过程用户看不到，客户端应用程序请求认证服务器，请求携带授权码。
*   4、认证服务器向客户端响应令牌
    微信认证服务器验证了客户端请求的授权码，如果合法则给客户端颁发令牌，令牌是客户端访问资源的通行证。
    此交互过程用户看不到，当客户端拿到令牌后，用户在黑马程序员看到已经登录成功。
*   5、客户端请求资源服务器的资源
    客户端携带令牌访问资源服务器的资源。
    黑马程序员网站携带令牌请求访问微信服务器获取用户的基本信息。
*   6、资源服务器返回受保护资源
    资源服务器校验令牌的合法性，如果合法则向用户响应资源信息内容。

**以上认证授权详细的执行流程如下：**

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192917675-1745106987.png)

**OAuth2.0认证流程：**

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192908176-15754733.png)

**OAauth2.0包括以下角色：**
**1、客户端**
本身不存储资源，需要通过资源拥有者的授权去请求资源服务器的资源，比如： `Android`客户端、`Web`客户端（浏览器端）、微信客户端等。
**2、资源拥有者**
通常为用户，也可以是应用程序，即该资源的拥有者。
**3、授权服务器（也称认证服务器）**

用于服务提供商对资源拥有的身份进行认证、对访问资源进行授权，认证成功后会给客户端发放令牌
（ `access_token`），作为客户端访问资源服务器的凭据。本例为微信的认证服务器。
**4、资源服务器**
存储资源的服务器，本例子为微信存储的用户信息。
现在还有一个问题，服务提供商能允许随便一个客户端就接入到它的授权服务器吗？答案是否定的，服务提供商会
给准入的接入方一个身份，用于接入时的凭据:
`client_id`：客户端标识

`client_secret`：客户端秘钥
因此，准确来说，授权服务器对两种 `OAuth2.0`中的两个角色进行认证授权，分别是资源拥有者、客户端。

---

## 2\. OAuth2.0 中四种授权方式

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192855718-1043650555.png)

### 1\. 授权码模式（ authorization code）

#### 流程

> 说明：【A服务客户端】需要用到【B服务资源服务】中的资源

**第一步**：【A服务客户端】将用户自动导航到【B服务认证服务】，这一步用户需要提供一个回调地址，以备【B服务认证服务】返回授权码使用。
**第二步**：用户点击授权按钮表示让【A服务客户端】使用【B服务资源服务】，这一步需要用户登录B服务，也就是说用户要事先具有B服务的使用权限。
**第三步**：【B服务认证服务】生成授权码，授权码将通过第一步提供的回调地址，返回给【A服务客户端】。

> 注意这个授权码并非通行【B服务资源服务】的通行凭证。

**第四步**：【A服务认证服务】携带上一步得到的授权码向【B服务认证服务】发送请求，获取通行凭证`token`。
**第五步**：【B服务认证服务】给【A服务认证服务】返回令牌`token`和更新令牌`refresh token`。

#### 使用场景

授权码模式是`OAuth2`中最安全最完善的一种模式，应用场景最广泛，可以实现服务之间的调用，常见的微信，QQ等第三方登录也可采用这种方式实现。

---

### 2\. 简化模式（implicit）

#### 流程

> 说明：简化模式中没有【A服务认证服务】这一部分，全部有【A服务客户端】与B服务交互，整个过程不再有授权码，token直接暴露在浏览器。

**第一步**：【A服务客户端】将用户自动导航到【B服务认证服务】，这一步用户需要提供一个回调地址，以备【B服务认证服务】返回`token`使用，还会携带一个【A服务客户端】的状态标识`state`。
**第二步**：用户点击授权按钮表示让【A服务客户端】使用【B服务资源服务】，这一步需要用户登录B服务，也就是说用户要事先具有B服务的使用权限。

**第三步**：【 B服务认证服务】生成通行令牌`token`，`token`将通过第一步提供的回调地址，返回给【A服务客户端】。

#### 使用场景

适用于A服务没有服务器的情况。比如：纯手机小程序，`JavaScript`语言实现的网页插件等。

---

### 3\. 密码模式（resource owner password credentials）

#### 流程

**第一步**：直接告诉【A服务客户端】自己的【B服务认证服务】的用户名和密码
**第二步**：【A服务客户端】携带【B服务认证服务】的用户名和密码向【B服务认证服务】发起请求获取
`token`。
**第三步**：【B服务认证服务】给【A服务客户端】颁发`token`。

#### 使用场景

此种模式虽然简单，但是用户将B服务的用户名和密码暴露给了A服务，需要两个服务信任度非常高才能使
用。

---

### 4\. 客户端模式（client credentials）

#### 流程

> 说明：这种模式其实已经不太属于`OAuth2`的范畴了。A服务完全脱离用户，以自己的身份去向B服务索取`token`。换言之，用户无需具备B服务的使用权也可以。完全是A服务与B服务内部的交互，与用户无关了。

**第一步**：A服务向B服务索取`token`。
**第二步**：B服务返回`token`给A服务。

#### 使用场景

A服务本身需要B服务资源，与用户无关。

---

## 3\. OAuth2.0 sql语句

**说明**
既可以写死在代码中，也可以写入到数据库中，通常写入到数据库
**建表语句**
[官方SQL地址：](https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/test/resources/schema.sql)

复制代码

```
create table oauth_client_details (
  client_id VARCHAR(256) PRIMARY KEY,
  resource_ids VARCHAR(256),
  client_secret VARCHAR(256),
  scope VARCHAR(256),
  authorized_grant_types VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities VARCHAR(256),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(256)
);

create table oauth_client_token (
  token_id VARCHAR(256),
  token LONGVARBINARY,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name VARCHAR(256),
  client_id VARCHAR(256)
);

create table oauth_access_token (
  token_id VARCHAR(256),
  token LONGVARBINARY,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name VARCHAR(256),
  client_id VARCHAR(256),
  authentication LONGVARBINARY,
  refresh_token VARCHAR(256)
);

create table oauth_refresh_token (
  token_id VARCHAR(256),
  token LONGVARBINARY,
  authentication LONGVARBINARY
);

create table oauth_code (
  code VARCHAR(256), authentication LONGVARBINARY
);

create table oauth_approvals (
    userId VARCHAR(256),
    clientId VARCHAR(256),
    scope VARCHAR(256),
    status VARCHAR(10),
    expiresAt TIMESTAMP,
    lastModifiedAt TIMESTAMP
);

-- customized oauth_client_details table
create table ClientDetails (
  appId VARCHAR(256) PRIMARY KEY,
  resourceIds VARCHAR(256),
  appSecret VARCHAR(256),
  scope VARCHAR(256),
  grantTypes VARCHAR(256),
  redirectUrl VARCHAR(256),
  authorities VARCHAR(256),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additionalInformation VARCHAR(4096),
  autoApproveScopes VARCHAR(256)
);
```

## 4\. demo案例

### 1\. 创建父工程

`pom.xml`文件如下

复制代码

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <packaging>pom</packaging>
    <modules>
        <module>oauth_resource</module>
        <module>oauth_server</module>
    </modules>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>springboot_security_oauth</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>springboot_security_oauth</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>Hoxton.RELEASE</spring-cloud.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-dependencies -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <repositories>
        <repository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>https://repo.spring.io/snapshot</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
</project>

```

### 2\. 创建资源提供方模块

`pom.xml`文件如下

复制代码

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>springboot_security_oauth</artifactId>
        <groupId>com.example</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>oauth_resource</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-oauth2</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.48</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.0</version>
        </dependency>
    </dependencies>

</project>
```

#### 1\. 配置`application.yml`

复制代码

```
server:
  port: 9002
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql:///test
    username: root
    password: root
  main:
    allow-bean-definition-overriding: true
mybatis:
  type-aliases-package: com.example.domain
  configuration:
    map-underscore-to-camel-case: true
logging:
  level:
    com.example: debug
```

#### 2\. 配置启动类

复制代码

```
@SpringBootApplication
@MapperScan("com.example.mapper")
public class OAuthResourceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OAuthResourceApplication.class, args);
    }
}
```

#### 3\. 编写一个资源路由

复制代码

```
@RestController
@RequestMapping("/product")
public class ProductController {
    @GetMapping
    public String findAll() {
        return "查询产品列表成功！";
    }
}
```

#### 4\. 创建用户pojo和角色pojo

**用户pojo**

复制代码

```
public class SysUser implements UserDetails {
    private Integer id;
    private String username;
    private String password;
    private Integer status;
    private List<SysRole> roles = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

---

**角色pojo**

复制代码

```
public class SysRole implements GrantedAuthority {
    private Integer id;
    private String roleName;
    private String roleDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    //标记此属性不做json处理
    @JsonIgnore
    @Override
    public String getAuthority() {
        return roleName;
    }
}
```

#### 5\. 编写资源管理配置类

复制代码

```
@Configuration
@EnableResourceServer
public class OauthResourceConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private TokenStore tokenStore;

    /**
     * 指定token的持久化策略
     * 其下有   RedisTokenStore保存到redis中，
     * JdbcTokenStore保存到数据库中，
     * InMemoryTokenStore保存到内存中等实现类，
     * 这里我们选择保存在数据库中
     *
     * @return
     */
    @Bean
    public TokenStore jdbcTokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("product_api")//指定当前资源的id，非常重要！必须写！
                .tokenStore(tokenStore);//指定保存token的方式
    }

    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //指定不同请求方式访问资源所需要的权限，一般查询是read，其余是write。
                .antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
                .antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')")
                .and()
                .headers().addHeaderWriter((request, response) -> {
            response.addHeader("Access-Control-Allow-Origin", "*");//允许跨域
            if (request.getMethod().equals("OPTIONS")) {//如果是跨域的预检请求，则原封不动向下传达请求头信息
                response.setHeader("Access-Control-Allow-Methods", request.getHeader("Access-Control-Request-Method"));
                response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
            }
        });
    }
}
```

### 3\. 创建授权模块

**`pom.xml`文件如下**

复制代码

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>springboot_security_oauth</artifactId>
        <groupId>com.example</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>oauth_server</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-oauth2</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.48</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.0</version>
        </dependency>
    </dependencies>
</project>
```

#### 1\. 配置`application.yml`

复制代码

```
server:
  port: 9001
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql:///test
    username: root
    password: root
  main:
    allow-bean-definition-overriding: true # 这个表示允许我们覆盖OAuth2放在容器中的bean对象，一定要配置
mybatis:
  type-aliases-package: com.example.domain
  configuration:
    map-underscore-to-camel-case: true
logging:
  level:
    com.example: debug
```

---

#### 2\. 配置启动类

复制代码

```
@SpringBootApplication
@MapperScan("com.example.mapper")
public class OauthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OauthServerApplication.class, args);
    }
}
```

---

#### 3\. 创建用户pojo和角色pojo

**用户pojo**

复制代码

```
public class SysUser implements UserDetails {
    private Integer id;
    private String username;
    private String password;
    private Integer status;
    private List<SysRole> roles = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

**角色pojo**

复制代码

```
public class SysRole implements GrantedAuthority {
    private Integer id;
    private String roleName;
    private String roleDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    //标记此属性不做json处理
    @JsonIgnore
    @Override
    public String getAuthority() {
        return roleName;
    }
}

```

#### 4\. 编写`UserMapper`和`RoleMapper`

**`RoleMapper`**

复制代码

```
public interface RoleMapper {
    @Select("select r.id,r.role_name roleName ,r.role_desc roleDesc " +
            "FROM sys_role r,sys_user_role ur " +
            "WHERE r.id=ur.rid AND ur.uid=#{uid}")
    public List<SysRole> findByUid(Integer uid);
}
```

**`UserMapper`**

复制代码

```
public interface UserMapper {
    @Select("select * from sys_user where username=#{username}")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "roles", column = "id", javaType = List.class,
                    many = @Many(select = "com.example.mapper.RoleMapper.findByUid"))
    })
    public SysUser findByUsername(String username);

}
```

#### 5\. 编写`UserDetailService`的实现类

**`UserService`**

复制代码

```
public interface UserService extends UserDetailsService {

}
```

**`UserServiceImpl`**

复制代码

```
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userMapper.findByUsername(username);
    }
}
```

#### 6\. 编写 `SpringSecurity`配置类

复制代码

```
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder myPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //所有资源必须授权后访问
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .permitAll()//指定认证页面可以匿名访问
                //关闭跨站请求防护
                .and()
                .csrf().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        //UserDetailsService类
        auth.userDetailsService(userService)
                //加密策略
                .passwordEncoder(passwordEncoder);
    }

    //AuthenticationManager对象在OAuth2认证服务中要使用，提取放入IOC容器中
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
```

#### 7\. 编写`OAuth2`授权配置类

复制代码

```
@Configuration
@EnableAuthorizationServer
public class OauthServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //从数据库中查询出客户端信息
    @Bean
    public JdbcClientDetailsService clientDetailsService() {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
        return jdbcClientDetailsService;
    }

    //token保存策略
    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    //授权信息保存策略
    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    //授权码模式专用对象
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    //指定客户端登录信息来源
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //从数据库取数据
        clients.withClientDetails(clientDetailsService());

        // 从内存中取数据
//        clients.inMemory()
//                .withClient("baidu")
//                .secret(passwordEncoder.encode("12345"))
//                .resourceIds("product_api")
//                .authorizedGrantTypes(
//                        "authorization_code",
//                        "password",
//                        "client_credentials",
//                        "implicit",
//                        "refresh_token"
//                )// 该client允许的授权类型 authorization_code,password,refresh_token,implicit,client_credentials
//                .scopes("read", "write")// 允许的授权范围
//                .autoApprove(false)
//                //加上验证回调地址
//                .redirectUris("http://www.baidu.com");
    }

    //检测token的策略
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.allowFormAuthenticationForClients()    //允许form表单客户端认证,允许客户端使用client_id和client_secret获取token
                .checkTokenAccess("isAuthenticated()")     //通过验证返回token信息
                .tokenKeyAccess("permitAll()")            // 获取token请求不进行拦截
                .passwordEncoder(passwordEncoder);
    }

    //OAuth2的主配置信息
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .approvalStore(approvalStore())
                .authenticationManager(authenticationManager)
                .authorizationCodeServices(authorizationCodeServices())
                .tokenStore(tokenStore())
                .userDetailsService(userDetailsService);
    }
}
```

---

### 4\. 测试

#### 4.1 授权码模式测试

在地址栏访问地址
http://localhost:9001/oauth/authorize?response\_type=code&client\_id=baidu
跳转到 `SpringSecurity`默认认证页面，提示用户登录个人账户【这里是`sys_user`表中的数据】

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192818134-1269856900.png)

登录成功后询问用户是否给予操作资源的权限，具体给什么权限。 `Approve`是授权，`Deny`是拒绝。
这里我们选择 `read`和`write`都给予`Approve`。

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192808390-1025000780.png)

点击`Authorize`后跳转到回调地址并获取授权码

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192758165-825194343.png)

使用授权码到服务器申请通行令牌`token`

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192748443-1941489913.png)

测试携带通行令牌再次去访问资源服务器资源路由

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192730381-1033180394.png)

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192737222-1840737999.png)

#### 4.2 简化模式测试

在地址栏访问地址
http://localhost:9001/oauth/authorize?response\_type=token&client\_id=baidu
由于上面用户已经登录过了，所以无需再次登录，其实和上面是有登录步骤的，这时，浏览器直接返回了token

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192719166-297934614.png)

使用刚才生成的`access_token`访问资源服务器

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192709316-291333844.png)

#### 4.3 密码模式测试

申请`token`

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192657008-2073405513.png)

使用刚才生成的`access_token`访问资源服务器

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192646613-1025036038.png)

#### 4.4 客户端模式测试

申请token

![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192635779-1202831594.png)

使用刚才生成的`access_token`访问资源服务器
![](https://img2018.cnblogs.com/blog/1580998/202001/1580998-20200113192622246-1427527908.png)

### 5\. 代码地址

[码云](https://gitee.com/an1993/springboot-springsecurity-oauth2)