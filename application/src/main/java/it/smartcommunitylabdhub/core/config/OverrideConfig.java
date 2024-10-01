package it.smartcommunitylabdhub.core.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

@Configuration
@Order(99)
public class OverrideConfig implements InitializingBean {

    @Autowired
    private WebSocketMessageBrokerStats webSocketMessageBrokerStats;

    @Override
    public void afterPropertiesSet() throws Exception {
        //override configs

        //disable websocket stats collection
        webSocketMessageBrokerStats.setLoggingPeriod(0);
    }
}
