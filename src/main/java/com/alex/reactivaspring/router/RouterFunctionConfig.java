package com.alex.reactivaspring.router;

import com.alex.reactivaspring.handler.SampleHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(SampleHandlerFunction sampleHandlerFunction) {
        return RouterFunctions
                .route(
                        GET("/functional/flux").and(accept(MediaType.APPLICATION_JSON)),
                        sampleHandlerFunction::flux
                );
    }

}
