package no.nav.fo.veilarbregistrering.db;

import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.config.ApplicationConfig;
import no.nav.testconfig.ApiAppTest;
import no.nav.veilarbregistrering.TestContext;
import no.nav.veilarbregistrering.db.DatabaseTestContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public abstract class IntegrasjonsTest {

    private static AnnotationConfigApplicationContext annotationConfigApplicationContext;
    private static PlatformTransactionManager platformTransactionManager;
    private TransactionStatus transactionStatus;

    @BeforeAll
    @BeforeClass
    public static void setupContext() {
        ApiAppTest.setupTestContext();
        TestContext.setup();
        setupContext(
                ApplicationConfig.class
        );
    }

    @SneakyThrows
    private static void setupContext(Class<?>... classes) {
        DatabaseTestContext.setupContext(System.getProperty("database"));

        annotationConfigApplicationContext = new AnnotationConfigApplicationContext(classes);
        annotationConfigApplicationContext.start();
        platformTransactionManager = getBean(PlatformTransactionManager.class);

        MigrationUtils.createTables(getBean(JdbcTemplate.class));
    }

    @BeforeEach
    @Before
    public void injectAvhengigheter() {
        annotationConfigApplicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @BeforeEach
    @Before
    public void startTransaksjon() {
        transactionStatus = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @AfterEach
    @After
    public void rollbackTransaksjon() {
        if (platformTransactionManager != null && transactionStatus != null) {
            platformTransactionManager.rollback(transactionStatus);
        }
    }

    protected static <T> T getBean(Class<T> requiredType) {
        return annotationConfigApplicationContext.getBean(requiredType);
    }

    @AfterAll
    @AfterClass
    public static void close() {
        if (annotationConfigApplicationContext != null) {
            annotationConfigApplicationContext.stop();
            annotationConfigApplicationContext.close();
            annotationConfigApplicationContext.destroy();
            annotationConfigApplicationContext = null;
        }
    }

}
