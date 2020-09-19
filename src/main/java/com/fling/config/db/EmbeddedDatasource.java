package com.fling.config.db;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class EmbeddedDatasource {

    @Value("${db.port}")
    private int port;

    @Bean("embedDatasource")
    public DataSource memoryPg() throws IOException {
        return EmbeddedPostgres.builder()
                .setServerConfig("timezone", "Asia/Seoul")
                .setPort(port)
                .setServerConfig("max_connections", "10000")
                .start().getPostgresDatabase();
    }

}
