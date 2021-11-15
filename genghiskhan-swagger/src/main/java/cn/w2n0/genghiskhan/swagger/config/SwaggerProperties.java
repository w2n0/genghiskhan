package cn.w2n0.genghiskhan.swagger.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties("swagger")
public class SwaggerProperties {

    /**
     * 是否开启Swagger。
     */
    private Boolean enabled;

    /**
     * Swagger解析的基础包路径。
     **/
    private String basePackage = "";

    /**
     * ApiInfo中的标题。
     **/
    private String title = "";

    /**
     * ApiInfo中的描述信息。
     **/
    private String description = "";

    /**
     * ApiInfo中的版本信息。
     **/
    private String version = "";

    /**
     * 许可证
     **/
    private String license = "";

    /**
     * 许可证URL
     **/
    private String licenseUrl = "";

    /**
     * 服务条款URL
     **/
    private String termsOfServiceUrl = "";

    /**
     * host信息
     **/
    private String host = "";

    /**
     * 联系人信息
     */
    private Contact contact = new Contact();

    /**
     * 联系人
     */
    @Data
    public class Contact {
        /**
         * 联系人
         **/
        private String name = "";
        /**
         * 联系人url
         **/
        private String url = "";
        /**
         * 联系人email
         **/
        private String email = "";
    }
}
