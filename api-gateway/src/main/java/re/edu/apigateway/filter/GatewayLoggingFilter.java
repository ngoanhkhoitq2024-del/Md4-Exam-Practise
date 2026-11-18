package re.edu.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.LocalDateTime;

@Component
public class GatewayLoggingFilter {
    private static final Logger log =
            LoggerFactory.getLogger(GatewayLoggingFilter.class);

    public HandlerFilterFunction<ServerResponse, ServerResponse> loggingFilter() {

        return (request, next) -> {

            long startTime = System.currentTimeMillis();

            String time = LocalDateTime.now().toString();
            String method = request.method().name();
            String uri = request.uri().toString();

            log.info("========== GATEWAY REQUEST ==========");
            log.info("Time: {}", time);
            log.info("Method: {}", method);
            log.info("URI: {}", uri);

            ServerResponse response = next.handle(request);

            long duration = System.currentTimeMillis() - startTime;

            log.info("Status: {}", response.statusCode());
            log.info("Duration: {} ms", duration);
            log.info("=====================================");

            return response;
        };
    }
}

