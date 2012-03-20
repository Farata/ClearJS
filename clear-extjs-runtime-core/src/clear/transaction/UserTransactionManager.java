package clear.transaction;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

public class UserTransactionManager {

	public static final String USER_TX_JNDI_NAME = "java:comp/UserTransaction";
	static Logger logger;
	static {
		logger = Logger.getLogger(UserTransactionManager.class);
	}

	private static ThreadLocal<UserTransaction> HOLDER = new ThreadLocal<UserTransaction>();
	private static ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>();

	private static UserTransaction createUserTransaction()
			throws NamingException, NotSupportedException, SystemException {
		Context ctx = new InitialContext();
		final UserTransaction userTransaction = (UserTransaction) ctx
				.lookup(USER_TX_JNDI_NAME);
		userTransaction.begin();
		if (logger.isDebugEnabled())
			logger.debug("BEGIN userTransaction");
		return userTransaction;
	}

	public static void joinUserTransaction() throws NamingException,
			NotSupportedException, SystemException {
		Integer count = COUNTER.get();
		if (count == null) {
			UserTransaction ut = HOLDER.get();
			if (ut == null) {
				ut = createUserTransaction();
				HOLDER.set(ut);
			}
			COUNTER.set(1);
		} else {
			count = count.intValue() + 1;
			COUNTER.set(count);
		}
	}

	public static void commitUserTransaction() throws SecurityException,
			IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException {
		UserTransaction userTransaction = HOLDER.get();
		Integer count = COUNTER.get();
		if (count == null) {
			count = 0;
		} else if (count.intValue() > 0) {
			count = count.intValue() - 1;
			COUNTER.set(count);
		}
		if (count.intValue() == 0) {
			HOLDER.remove();
			COUNTER.remove();
		}
		if (userTransaction != null && count.intValue() == 0) {
			userTransaction.commit();
			if (logger.isDebugEnabled())
				logger.debug("COMMIT userTransaction");
		}
	}

	public static void rollbackUserTransaction() throws IllegalStateException,
			SecurityException, SystemException {
		UserTransaction userTransaction = HOLDER.get();
		HOLDER.remove();
		COUNTER.remove();
		if (userTransaction != null) {
			userTransaction.rollback();
			if (logger.isDebugEnabled())
				logger.debug("ROLLBACK userTransaction");
		}
	}
}