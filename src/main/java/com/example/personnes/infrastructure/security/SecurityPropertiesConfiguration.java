package com.example.personnes.infrastructure.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClientAccessProperties.class)
class SecurityPropertiesConfiguration {
}
