package clear.cdb.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * This class guarantees that only one single SessionFactory is instantiated and
 * that the configuration is done thread safe as singleton. Actually it only
 * wraps the Hibernate SessionFactory. You are free to use any kind of JTA or
 * Thread transactionFactories.
 */
public class SessionFactoryUtils {

	/** The single instance of hibernate SessionFactory */
	private static org.hibernate.SessionFactory sessionFactory;

	public static SessionFactoryUtils setSessionFactory(SessionFactory sessionFactory) {
		SessionFactoryUtils.sessionFactory = sessionFactory;
		return new SessionFactoryUtils();
	}

	/**
	 * disable constructor to guaranty a single instance
	 */
	private SessionFactoryUtils() {
	}

	public static SessionFactory getInstance() {
		if (sessionFactory == null) {
			sessionFactory = new AnnotationConfiguration().configure()
					.buildSessionFactory();
		}
		return sessionFactory;
	}
	
	/**
	 * Create SessionFactory based on specified configuration file
	 * @param config - the configuration file
	 * @return instance of SessionFactory
	 */
	public static SessionFactory getInstance(String config) {
		if (sessionFactory == null) {
			sessionFactory = new AnnotationConfiguration().configure(config)
					.buildSessionFactory();
		}
		return sessionFactory;
	}

	/**
	 * Opens a session and will not bind it to a session context
	 * 
	 * @return the session
	 */
	public static Session openSession() {
		return getInstance().openSession();
	}

	/**
	 * Returns a session from the session context. If there is no session in the
	 * context it opens a session, stores it in the context and returns it. This
	 * factory is intended to be used with a hibernate.cfg.xml including the
	 * following property <property
	 * name="current_session_context_class">thread</property> This would return
	 * the current open session or if this does not exist, will create a new
	 * session
	 * 
	 * @return the session
	 */
	public static Session getCurrentSession() {
		return getInstance().getCurrentSession();
	}

	/**
	 * closes the session factory
	 */
	public static void close() {
		if (sessionFactory != null)
			sessionFactory.close();
		sessionFactory = null;
	}
}