package com.ms.oracle.controller;

import com.ms.oracle.MvcHelper;
import com.ms.oracle.domain.Hello;
import com.ms.oracle.repository.HelloRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = {HelloControllerTest.TestContainerInitializer.class})
@EnableAutoConfiguration
@AutoConfigureMockMvc
class HelloControllerTest {

    /*
        Only one container is created and is used by all the tests in this class.  SQL scripts are
        executed to set up the database for each test.

        Sometimes, for some reason, this class sometimes throws an exception:
            java.lang.NoSuchMethodError: com.sun.jna.Native.load(Ljava/lang/String;Ljava/lang/Class;Ljava/util/Map;)Lcom/sun/jna/Library;

        When this happens, the tests are ignored.  This happens sporadically.  Not sure why.
     */


    private static final Logger LOGGER = LoggerFactory.getLogger(HelloControllerTest.class);

    @Container
    public static OracleContainer container = new OracleContainer(DockerImageName.parse("oracleinanutshell/oracle-xe-11g"))
            .withLogConsumer(new Slf4jLogConsumer(LOGGER))
            .withInitScript("controller-init.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HelloRepository helloRepository;

    @Test
    @Sql("classpath:controller-populate.sql")
    @Sql(scripts = "classpath:controller-clean-up.sql", executionPhase = AFTER_TEST_METHOD)
    void useSqlToPopulateDatabase() {
        final List<Hello> hellos = helloRepository.findAll();
        assertThat(hellos.size()).isEqualTo(3);
    }

    @Test
    @Sql(scripts = "classpath:controller-clean-up.sql", executionPhase = AFTER_TEST_METHOD)
    void useRepositoryToPopulateDatabase() {
        helloRepository.save(Hello.builder().greeting("1").build());
        helloRepository.save(Hello.builder().greeting("2").build());
        helloRepository.save(Hello.builder().greeting("3").build());
        final List<Hello> hellos = helloRepository.findAll();
        assertThat(hellos.size()).isEqualTo(3);
    }

    @Test
    @Sql("classpath:controller-populate.sql")
    @Sql(scripts = "classpath:controller-clean-up.sql", executionPhase = AFTER_TEST_METHOD)
    void all() throws Exception {
        final MvcResult result = MvcHelper.doGet(mockMvc, "/all");
        final List<Hello> actual = MvcHelper.deserializeIntoList(result.getResponse().getContentAsString(), Hello.class);
        assertThat(actual.size()).isEqualTo(3);
    }

    @Test
    @Sql("classpath:controller-populate.sql")
    @Sql(scripts = "classpath:controller-clean-up.sql", executionPhase = AFTER_TEST_METHOD)
    void add() throws Exception {
        MvcHelper.doPost(mockMvc, "/", Hello.builder().greeting("howdy").build());
        final List<Hello> actual = helloRepository.findAll();
        assertThat(actual.size()).isEqualTo(4);
    }

    @Test
    @Sql("classpath:controller-populate.sql")
    @Sql(scripts = "classpath:controller-clean-up.sql", executionPhase = AFTER_TEST_METHOD)
    void add_verifyReturn() throws Exception {
        final MvcResult result = MvcHelper.doPost(mockMvc, "/", Hello.builder().greeting("howdy").build());
        final Hello actual = MvcHelper.deserializeObject(result, Hello.class);
        assertThat(actual.getGreeting()).isEqualTo("howdy");
    }

    static class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues
                    .of(
                            "spring.datasource.url=" + container.getJdbcUrl(),
                            "spring.datasource.username=" + container.getUsername(),
                            "spring.datasource.password=" + container.getPassword(),
                            "spring.datasource.driver-class-name=" + container.getDriverClassName()
                    )
                    .applyTo(applicationContext.getEnvironment());
        }
    }

}

