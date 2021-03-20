package com.ms.oracle;

import com.ms.oracle.domain.Hello;
import com.ms.oracle.repository.HelloRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.OracleContainer;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(
        initializers = {HelloIT.TestContainerInitializer.class})
class HelloIT {

    private static OracleContainer oracleContainer = new OracleContainer("oracleinanutshell/oracle-xe-11g");

    @Autowired
    private HelloRepository helloRepository;

    @BeforeAll
    static void startup() {
        oracleContainer.withInitScript("init.sql");
        oracleContainer.start();
    }

    @AfterAll
    static void shutdown() {
        oracleContainer.stop();
    }

    @Test
    void test1() {
        helloRepository.save(Hello.builder().greeting("AAA").build());
        final int size = helloRepository.findAll().size();
        Assertions.assertEquals(4, size);
    }

    @Test
    void test2() {
        helloRepository.save(Hello.builder().greeting("AAA").build());
        helloRepository.save(Hello.builder().greeting("BBB").build());
        helloRepository.save(Hello.builder().greeting("CCC").build());
        final int size = helloRepository.findAll().size();
        Assertions.assertEquals(6, size);
    }

    static class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues
                    .of(
                            "spring.datasource.url=" + oracleContainer.getJdbcUrl(),
                            "spring.datasource.username=" + oracleContainer.getUsername(),
                            "spring.datasource.password=" + oracleContainer.getPassword(),
                            "spring.datasource.driver-class-name=" + oracleContainer.getDriverClassName()
                    )
                    .applyTo(applicationContext.getEnvironment());
        }
    }
}
