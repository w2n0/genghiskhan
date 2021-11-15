# Genghiskhan
## Introduction
The Java backend service integration development package supports the starter mechanism and has the following functions:
1. The dubbo extension
Dubbo generalization extension, unified exception handling, global message ID, tenant event
2. Message alarm
Supports sending of dingtalk groups and email messages
3. The data source
Supports dynamic data sources based on tenant and data source type, automatically setting data sources based on tenant in dubbo calls
4. The cache
Redis is packaged with tools
5. The serial number
Supports snowflake algorithm to generate global unique ID
6. Tools
Provides encryption and decryption, Excel processing, large file reading, basic type encapsulation and other functions
7. The entity
Provides generic entity objects such as paging, base exceptions, and so on

## Table of Contents
****
- [Usage](#usage)
	- [genghiskhan-datasource](#genghiskhan-datasource)
	- [genghiskhan-dubbo](#genghiskhan-dubbo)
	- [genghiskhan-alarm](#genghiskhan-alarm)
	- [genghiskhan-sequence](#genghiskhan-sequence)
- [License](#license)

## Usage

### genghiskhan-datasource

**Maven**
```xml
<dependency>
    <groupId>cn.w2n0.genghiskhan</groupId>
    <artifactId>genghiskhan-datasource</artifactId>
    <version>1.0.1</version>
</dependency>
```
**Yml**
```yml
genghiskhan:
  datasource:
    enable: true
    items:
      - tag: 001
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://192.168.1.40:3306/test01?characterEncoding=utf8&useSSL=true&serverTimezone=UTC&zeroDateTimeBehavior=CONVERT_TO_NULL
        username: root
        password: root
        initialSize: 10
        maxActive: 20
        minIdle: 15
        maxWait: 3000
      - tag: 002
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://192.168.1.40:3306/test02?characterEncoding=utf8&useSSL=true&serverTimezone=UTC&zeroDateTimeBehavior=CONVERT_TO_NULL
        username: root
        password: root
        initialSize: 10
        maxActive: 20
        minIdle: 15
        maxWait: 3000
      - tag: 003
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://192.168.1.40:3306/test03?characterEncoding=utf8&useSSL=true&serverTimezone=UTC&zeroDateTimeBehavior=CONVERT_TO_NULL
        username: root
        password: root
        initialSize: 10
        maxActive: 20
        minIdle: 15
        maxWait: 3000
```
**Java**
```java
DynamicDataSourceContextHolder.setDataSource("001");
```
### genghiskhan-dubbo

**Maven**
```xml
<dependency>
    <groupId>cn.w2n0.genghiskhan</groupId>
    <artifactId>genghiskhan-dubbo</artifactId>
    <version>1.0.1</version>
</dependency>
```

###Producer Services

**Yml**

```yml
dubbo:
  protocol:
    name: dubbo
    port: -1
  scan:
    base-packages: cn.w2n0.upms.app.service #扫描包名
  provider:
    filter: sentinel.dubbo.provider.filter,dubbo.application.context.name.filter,echo,classloader,traceId,rewriteGeneric,validation,context,trace,timeout,monitor,globalException,-default
    group: genghiskhan
    retries: 0
    timeout: 60000
    validation: true
    version: 1.0.0
  registry:
    address: nacos://192.168.1.40:8848/?username=nacos&password=password01 #注册中心
    check: false
    group: genghiskhan
    parameters:
      namespace: 7c2827ed-24e7-4c8a-852b-3b8720d20a3f
    protocol: nacos
```

###Consumer Services

**Yml**
```yml
dubbo:
  consumer:
    group: genghiskhan
    filter: traceId
    validation: false
    version: 1.0.0
    router:
      enable: false
      rule: condition
      param: tenant_id #路由参数
      values: 00001,00002,00003  #指定参数值
      routers:  #匹配后跳转的IP
        - 192.168.30.231
        - 192.168.1.41
  protocol:
    name: dubbo
  registry:
    address: nacos://192.168.1.40:8848 #注册中心
    check: false
    group: genghiskhan
    parameters:
      namespace: 7c2827ed-24e7-4c8a-852b-3b8720d20a3f
    protocol: nacos
    version: 1.0
```
**Java**
```java
@Configuration
public class ReferenceDubboConfig {
    @Bean
    public GenericService dubbo() {
        return new DubboService();
    }
}
```

```java
public enum ResultError {
    REQUESTPATH_ERROR(1010000, "错误的请求路径"),
    AUTHRPC_ERROR(1010001, "权限系统调用错误"),
    METHODNULL_ERROR(1010010, "method没有填写"),
    TIMEOUT_ERROR(1010015,"请求服务超时,调用超时"),
    FLOW_ERROR(1010016,"请求服务过于频繁"),
    NETWORK_ERROR(1010020,"RPC调用错误,网络异常"),
    SERIALIZATION_ERROR(1010025,"RPC调用错误,序列号异常"),
    FORBIDDEN_ERROR(1010030,"RPC调用错误,无提供者服务"),
    UNKNOWN_ERROR(1010035,"RPC调用错误,FORBIDDEN_ERROR"),
    PARAMS_VALIDATION_ERROR(1010050,"请求参数校验错误"),
    BIZ_ERROR(999999, "业务系统调用错误");
    private int code;
    private String message;
    ResultError(int code, String message) {
        this.code = code;
        this.message = message;
    }
    /**
     * 错误代码
     */
    public int getCode() {
        return code;
    }
    /**
     * 错误信息
     */
    public String getMessage() {
        return message;
    }
}
```

```java
    @Autowired
    private GenericService genericService;
    
    public Result rpcInovke(String service, String version, String method, Map<String, Object> paramsMap) {
            Result result;
            Object object;
            try {
                object = genericService.exec(service, version, method, paramsMap);
                if (object == null) {
                    result = new Result();
                    result.setResult(null);
                } else {
                    if (object instanceof HashMap) {
                        HashMap<String, Object> maps = (HashMap<String, Object>) object;
                        if (maps.size() == 5 && maps.get("class")
                                .equals("cn.w2n0.genghiskhan.entity.Result")) {
                            if (maps.get("success").equals(true)) {
                                result = new Result();
                                String json = JSONObject.toJSONString(maps.get("result"),propertyFilter);
                                result.setSuccess(true);
                                result.setResult(json);
                            } else {
                                result = errorResult(ResultError.BIZ_ERROR);
                                int subcode = (Integer) maps.get("code");
                                String msg = (String) maps.get("msg");
                                result.setCode(subcode);
                                result.setMsg(msg);
                            }
                        } else {
    
                            result = new Result();
                            String json = JSONObject.toJSONString(object, propertyFilter);
                            result.setResult(json);
                        }
                    } else {
                        result = new Result();
                        String json = JSONObject.toJSONString(object, propertyFilter);
                        result.setResult(json);
                    }
                }
            } catch (RpcException ex) {
                switch (ex.getCode()) {
                    case org.apache.dubbo.rpc.RpcException.NETWORK_EXCEPTION:
                        result = errorResult(ResultError.NETWORK_ERROR);
                        break;
                    case org.apache.dubbo.rpc.RpcException.TIMEOUT_EXCEPTION:
                        result = errorResult(ResultError.TIMEOUT_ERROR);
                        break;
                    case org.apache.dubbo.rpc.RpcException.SERIALIZATION_EXCEPTION:
                        result = errorResult(ResultError.SERIALIZATION_ERROR);
                        break;
                    case org.apache.dubbo.rpc.RpcException.FORBIDDEN_EXCEPTION:
                        result = errorResult(ResultError.FORBIDDEN_ERROR);
                        break;
                    case org.apache.dubbo.rpc.RpcException.BIZ_EXCEPTION:
                        result = errorResult(ResultError.BIZ_ERROR);
                        break;
                    default:
                        result = errorResult(ResultError.UNKNOWN_ERROR);
                        break;
                }
            } catch (Exception x) {
                if (x.getMessage().indexOf("ConstraintViolationImpl") > 0) {
                    String msg = getCutoutStr("interpolatedMessage='", "',", x.getMessage());
                    result = Result.failed(ResultError.PARAMS_VALIDATION_ERROR.getCode());
                    result.setMsg(msg);
                } else {
                    result = errorResult(ResultError.BIZ_ERROR);
                }
            }
            return result;
        }
    
        /**
         * error
         *
         * @param e
         * @return
         */
        private Result errorResult(ResultError e) {
            Result result = new Result();
            result.setCode(e.getCode());
            result.setMsg(e.getMessage());
            return result;
        }
```
### genghiskhan-alarm

**Maven**
```xml
<dependency>
    <groupId>cn.w2n0.genghiskhan</groupId>
    <artifactId>genghiskhan-alarm</artifactId>
    <version>1.0.1</version>
</dependency>
```
**Yml**
```yml
spring:
  mail:
    default-encoding: utf-8
    from: ****@raycloud.com
    host: smtp.exmail.qq.com
    password: ****
    port: 465
    properties:
      mail:
        smtp:
          auth: true
          socketFactory:
            class: javax.net.ssl.SSLSocketFactory
          starttls:
            enable: true
            required: true
    username: ****@raycloud.com
```

**Java**
```java
    @Autowired
    private AlarmSendingTemplate alarmSendingTemplate;
   
    //钉钉机器人webhook
    private String webHook="https://oapi.dingtalk.com/robot/send?access_token=###";

    void sendCart() {
            String title="星网，流水线[ERP订单写入SAP错误]";
            String content="任务[SAP订单写入]:  \n <font color=\"red\">模型循环计划，得到的模型不可执行 </font>";
            ActionCardEntity actionCardEntity=new ActionCardEntity(title, content,  "查看详情", "http://www.xxx.com");
            alarmSendingTemplate.asyncSendDingTalk(actionCardEntity, webHook);
    }
    
    void sendEmail() {
            String[] tos={"xxx@163.com"};
            String title="星网，流水线[ERP订单写入SAP错误]";
            String content="任务[SAP订单写入]:  \n <font color=\"red\">模型循环计划，得到的模型不可执行 </font>";
            ActionCardEntity actionCardEntity=new ActionCardEntity(title, content,  "查看详情", "http://www.xxx.com");
            alarmSendingTemplate.asyncSendMail(tos,title,content);
    }
```
## genghiskhan-sequence

**Maven**
```xml
<dependency>
    <groupId>cn.w2n0.genghiskhan</groupId>
    <artifactId>genghiskhan-sequence</artifactId>
    <version>1.0.1</version>
</dependency>
```
**Java**

```java
    @Autowired
    private SnGenerate snGenerate;

    public void gen(){
        snGenerate.gen();
    }
```
## License

[MIT](LICENSE) 