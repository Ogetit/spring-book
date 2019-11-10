package com.github.app.util.swagger;

import static springfox.documentation.spring.web.paths.Paths.removeAdjacentForwardSlashes;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.AbstractPathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 文件描述 Swagger 配置
 *
 * @author ouyangjie
 * @Title: SwaggerApiConfig
 */
@Configuration
@EnableSwagger2
@EnableWebMvc
@EnableSwaggerBootstrapUI
public class SwaggerApiConfig {
    @Value("#{config['system.url']}")
    private String systemUrl;

    /**
     * 根据配置读取是否开启swagger文档，针对测试与生产环境采用不同的配置
     */
    @Value("#{config['swagger.enable']}")
    private boolean isSwaggerEnable;

    @Bean
    public Docket api() {
        if (isSwaggerEnable) {
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .produces(getProduces())
                    .consumes(getConsumes())
                    .useDefaultResponseMessages(false)
                    .pathProvider(new CustRelativePathProvider())
                    .select()
                    // 如果接口太多，导致内存溢出？错，存在大量的循环依赖参数，导致Swagger解析参数时候内存溢出
                    // .apis(RequestHandlerSelectors.basePackage("com.github.app.web"))
                    .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                    .paths(PathSelectors.any())
                    .build();
        } else {
            return apiOnline();
        }
    }

    /**
     * 获取线上Docket
     *
     * @return
     */
    private Docket apiOnline() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(false)
                .apiInfo(apiInfoOnline())
                .select()
                // 如果是线上环境，添加路径过滤，设置为全部都不符合
                .paths(PathSelectors.none())
                .build();
    }

    /**
     * 自定义生成的 RestFul 接口链接带的后缀，如 .do、.api、.json 等
     */
    public class CustRelativePathProvider extends AbstractPathProvider {
        public static final String ROOT = "/";

        @Override
        public String getOperationPath(String operationPath) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
            String uri = removeAdjacentForwardSlashes(uriComponentsBuilder.path(operationPath).build().toString());
            return uri + ".json";
        }

        @Override
        protected String applicationPath() {
            return ROOT;
        }

        @Override
        protected String getDocumentationPath() {
            return ROOT;
        }
    }

    /**
     * 开发测试环境ApiInfo
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("系统接口平台-仅开发测试环境")
                .description("开发、测试环境专属。")
                .contact(new Contact("欧阳洁", systemUrl, "secret"))
                .version("1.0.0")
                .build();
    }

    /**
     * 生产环境ApiInfo
     *
     * @return
     */
    private ApiInfo apiInfoOnline() {
        return new ApiInfoBuilder()
                .title("生产环境屏蔽接口文档")
                .description("生产环境屏蔽接口文档")
                .version("0.0.0")
                .contact(new Contact("欧阳洁", systemUrl, "secret"))
                .build();
    }

    /**
     * 获取消费（返回）文本类型
     *
     * @return
     */
    private Set<String> getConsumes() {
        Set<String> defaultConsumes = new HashSet<String>();
        defaultConsumes.add("application/json");
        defaultConsumes.add("*/*");
        return defaultConsumes;
    }

    /**
     * 获取生产（请求）文本类型
     *
     * @return
     */
    private Set<String> getProduces() {
        Set<String> defaultProduces = new HashSet<String>();
        defaultProduces.add("application/json");
        defaultProduces.add("text/plain");
        defaultProduces.add("application/x-www-form-urlencoded");
        defaultProduces.add("*/*");
        return defaultProduces;
    }
}
