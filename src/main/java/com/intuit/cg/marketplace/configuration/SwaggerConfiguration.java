package com.intuit.cg.marketplace.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@org.springframework.context.annotation.Configuration
@EnableSwagger2
@ComponentScan(basePackages={"com.intuit.cg.projects.users", "com.intuit.cg.projects.projects"})
@SuppressWarnings("unused")
public class SwaggerConfiguration {
	@Bean
	public Docket customDocket(){
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build()
				.apiInfo(getApiInfo());
	}

	private ApiInfo getApiInfo() {
		return new ApiInfoBuilder()
				.title("Marketplace Project")
				.description("Marketplace for buying and selling projects")
				.contact(new Contact("Billy Ji", "", "Billy_Ji@intuit.com"))
				.build();
	}
}
