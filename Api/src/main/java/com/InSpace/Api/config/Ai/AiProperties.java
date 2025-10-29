package com.InSpace.Api.config.Ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private Map<String, String> schemas;

    public Map<String, String> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, String> schemas) {
        this.schemas = schemas;
    }
}
