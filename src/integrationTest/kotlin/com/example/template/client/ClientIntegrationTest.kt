package com.example.template.client

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.mockserver.integration.ClientAndServer
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = [
        FeignAutoConfiguration::class,
        HttpMessageConvertersAutoConfiguration::class
    ]
)
@ActiveProfiles("test")
@ComponentScan("com.example.template.client")
@EnableFeignClients
abstract class ClientIntegrationTest {

    companion object {
        val mockServer: ClientAndServer = ClientAndServer.startClientAndServer(8090)

        @AfterAll
        fun afterAll() {
            mockServer.close()
        }
    }

    @BeforeEach
    fun beforeEach() {
        mockServer.reset()
    }
}
