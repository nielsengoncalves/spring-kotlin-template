package com.example.template.repository

import com.example.template.db.Public
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(initializers = [RepositoryIntegrationTest.Initializer::class])
@ActiveProfiles("test")
abstract class RepositoryIntegrationTest {

    @Autowired
    protected lateinit var dslContext: DSLContext

    @AfterEach
    fun afterEach() {
        log.info { "Cleaning up database for next execution..." }
        val tablesToTruncate = Public().tables
        tablesToTruncate.forEach { table ->
            dslContext.truncate(table).execute().also { log.info { "Truncated `${table.name}` table" } }
        }
    }

    companion object {
        val postgresql = PostgreSQLContainer<Nothing>("postgres").apply {
            withUsername("testuser")
            withPassword("testpw123")
            withDatabaseName("spring_kotlin_template_test")
        }
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            postgresql.start()
            configurableApplicationContext.environment.apply {
                systemProperties["spring.datasource.url"] = postgresql.jdbcUrl
                systemProperties["spring.datasource.username"] = postgresql.username
                systemProperties["spring.datasource.password"] = postgresql.password
            }
        }
    }

    private val log = LoggerFactory.getLogger(this::class.java)
}
