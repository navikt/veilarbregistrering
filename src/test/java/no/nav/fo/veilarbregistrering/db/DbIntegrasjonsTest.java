package no.nav.fo.veilarbregistrering.db;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public abstract class DbIntegrasjonsTest {

    private static AnnotationConfigApplicationContext annotationConfigApplicationContext;
    private static PlatformTransactionManager platformTransactionManager;
    private TransactionStatus transactionStatus;

    @BeforeAll
    public static void setupContext() {
        setupContext(
                DatabaseConfig.class
        );
    }

    @SneakyThrows
    private static void setupContext(Class<?>... classes) {
        annotationConfigApplicationContext = new AnnotationConfigApplicationContext(classes);
        annotationConfigApplicationContext.start();
        platformTransactionManager = getBean(PlatformTransactionManager.class);

        MigrationUtils.createTables(getBean(JdbcTemplate.class));
    }

    @BeforeEach
    public void injectAvhengigheter() {
        annotationConfigApplicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @BeforeEach
    public void startTransaksjon() {
        transactionStatus = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @AfterEach
    public void rollbackTransaksjon() {
        if (platformTransactionManager != null && transactionStatus != null) {
            platformTransactionManager.rollback(transactionStatus);
        }
    }

    protected static <T> T getBean(Class<T> requiredType) {
        return annotationConfigApplicationContext.getBean(requiredType);
    }

    @AfterAll
    public static void close() {
        if (annotationConfigApplicationContext != null) {
            annotationConfigApplicationContext.stop();
            annotationConfigApplicationContext.close();
            annotationConfigApplicationContext = null;
        }
    }
}