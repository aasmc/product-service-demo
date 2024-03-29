package ru.aasmc.productservice

import jakarta.transaction.Transactional
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("integtest")
@SpringBootTest
@Testcontainers
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
    scripts = ["classpath:clear-db.sql"],
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
)
open class BaseJpaTest {
}