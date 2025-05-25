package com.kdk.cofnig.app;

import java.util.Collections;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 6. 8. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
@Configuration
@Aspect
public class TransactionAspect {

	private static final String AOP_TRANSACTION_EXPRESSION = "execution(* com.kdk.app.**..impl.*Impl.*(..))";

	private final TransactionManager transactionManager;

	public TransactionAspect(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	private static final String[] DEFAULT_READ_ONLY_METHOD_RULE_TRANSACTION_ATTRIBUTES = {
		"select*",
		"get*",
		"find*",
		"search*",
		"fetch*"
	};

	private static final String[] DEFAULT_REQUIRED_METHOD_RULE_TRANSACTION_ATTRIBUTES = {
		"*"
	};

	private RuleBasedTransactionAttribute readOnlyTransactionRule () {
		RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
		readOnly.setReadOnly(true);
		readOnly.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
		return readOnly;
	}

	private RuleBasedTransactionAttribute requiredTransactionRule () {
		RuleBasedTransactionAttribute required = new RuleBasedTransactionAttribute();
		required.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
		required.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		required.setTimeout(TransactionDefinition.TIMEOUT_DEFAULT);
		return required;
	}

    @Bean
    TransactionInterceptor transactionAdvice() {
		NameMatchTransactionAttributeSource transactionAttributeSource = new NameMatchTransactionAttributeSource();

		RuleBasedTransactionAttribute readOnly = this.readOnlyTransactionRule();
		RuleBasedTransactionAttribute required = this.requiredTransactionRule();

		for (String methodName : DEFAULT_READ_ONLY_METHOD_RULE_TRANSACTION_ATTRIBUTES) {
			transactionAttributeSource.addTransactionalMethod(methodName , readOnly);
		}

		for (String methodName : DEFAULT_REQUIRED_METHOD_RULE_TRANSACTION_ATTRIBUTES) {
			transactionAttributeSource.addTransactionalMethod(methodName , required);
		}

		return new TransactionInterceptor(transactionManager, transactionAttributeSource);
	}

    @Bean
    Advisor transactionAdviceAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(AOP_TRANSACTION_EXPRESSION);

		return new DefaultPointcutAdvisor(pointcut, this.transactionAdvice());
	}

}
