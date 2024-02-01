package ru.aasmc.productservice

import jakarta.transaction.Transactional
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("integtest")
@DataJpaTest
@Testcontainers
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
    scripts = ["classpath:clear-db.sql"],
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
open class BaseJpaTest {
}