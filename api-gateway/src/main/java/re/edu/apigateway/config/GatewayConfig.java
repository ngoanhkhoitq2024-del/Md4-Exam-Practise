package re.edu.apigateway.config;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import re.edu.apigateway.filter.GatewayLoggingFilter;

@Configuration
public class GatewayConfig {
    private final GatewayLoggingFilter loggingFilter;

    public GatewayConfig(GatewayLoggingFilter loggingFilter) {
        this.loggingFilter = loggingFilter;
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return GatewayRouterFunctions.route("order-service")
                .route(
                        RequestPredicates.path("/api/orders/**"),
                        HandlerFunctions.http()
                )
                .filter(loggingFilter.loggingFilter())
                .filter(LoadBalancerFilterFunctions.lb("order-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return GatewayRouterFunctions.route("inventory-service")
                .route(
                        RequestPredicates.path("/api/inventory/**"),
                        HandlerFunctions.http()
                )
                .filter(loggingFilter.loggingFilter())
                .filter(LoadBalancerFilterFunctions.lb("inventory-service"))
                .build();
    }
}