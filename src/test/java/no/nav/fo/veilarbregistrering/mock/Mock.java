package no.nav.fo.veilarbregistrering.mock;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static no.nav.fo.veilarbregistrering.config.ApplicationTestConfig.RUN_WITH_MOCKS;

public class Mock implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return RUN_WITH_MOCKS;
    }

}
