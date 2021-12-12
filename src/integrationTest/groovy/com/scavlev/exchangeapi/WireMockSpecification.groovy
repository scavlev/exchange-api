package com.scavlev.exchangeapi

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.SocketUtils

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

@ContextConfiguration(initializers = PropertyOverrideContextInitializer)
class WireMockSpecification extends IntegrationSpecification {

    static final int WIREMOCK_PORT = SocketUtils.findAvailableTcpPort()

    static WireMockServer wireMock = new WireMockServer(options()
            .port(WIREMOCK_PORT)
            .extensions(new ResponseTemplateTransformer(true))
            .usingFilesUnderClasspath("src/integrationTest/resources"))

    def setupSpec() {
        wireMock.start()
    }

    def cleanupSpec() {
        wireMock.stop()
    }

    def cleanup() {
        wireMock.resetAll()
    }

    static class PropertyOverrideContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        void initialize(ConfigurableApplicationContext applicationContext) {
            System.setProperty("wiremock.port", WIREMOCK_PORT as String);
        }
    }
}
