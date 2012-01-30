package com.farata.cleardatabuilder.util;

import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.InformixDialect;
import org.hibernate.dialect.Ingres10Dialect;
import org.hibernate.dialect.Ingres9Dialect;
import org.hibernate.dialect.IngresDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.dialect.SybaseAnywhereDialect;

public class HibernateDialectResolver {
	public static Dialect resolveDialect(String databaseName,
			int databaseMajorVersion, int databaseMinorVersion) {
		if ("HSQL Database Engine".equals(databaseName))
			return new HSQLDialect();
		if ("H2".equals(databaseName))
			return new H2Dialect();
		if ("MySQL".equals(databaseName))
			return new MySQLDialect();
		if ("PostgreSQL".equals(databaseName))
			return new PostgreSQLDialect();
		if ("Apache Derby".equals(databaseName))
			return new DerbyDialect();
		if ("ingres".equalsIgnoreCase(databaseName)) {
			switch (databaseMajorVersion) {
			case 9:
				if (databaseMinorVersion > 2)
					return new Ingres9Dialect();
				else
					return new IngresDialect();

			case 10:
				return new Ingres10Dialect();
			}
			return new IngresDialect();
		}
		if (databaseName.startsWith("Microsoft SQL Server"))
			return new SQLServerDialect();
		if ("Sybase SQL Server".equals(databaseName)
				|| "Adaptive Server Enterprise".equals(databaseName))
			return new SybaseASE15Dialect();
		if (databaseName.startsWith("Adaptive Server Anywhere"))
			return new SybaseAnywhereDialect();
		if ("Informix Dynamic Server".equals(databaseName))
			return new InformixDialect();
		if (databaseName.startsWith("DB2/"))
			return new DB2Dialect();
		if ("Oracle".equals(databaseName))
			switch (databaseMajorVersion) {
			case 11:
				return new Oracle10gDialect();

			case 10:
				return new Oracle10gDialect();

			case 9:
				return new Oracle9iDialect();

			case 8:
				return new Oracle8iDialect();

			default:
				break;
			}
		return null;
	}

	public static Dialect resolveDialect(String dbType, String dbVersion) {
		int databaseMajorVersion = getDatabaseMajorVersion(dbVersion);
		int databaseMinorVersion = getDatabaseMinorVersion(dbVersion);
		return resolveDialect(dbType, databaseMajorVersion,
				databaseMinorVersion);
	}

	private static int getDatabaseMinorVersion(String dbVersion) {
		if (dbVersion != null) {
			String[] arr = dbVersion.split(".");
			try {
				return Integer.valueOf(arr[arr.length - 1]);
			} catch (Throwable e) {
			}
		}
		return 0;
	}

	private static int getDatabaseMajorVersion(String dbVersion) {
		if (dbVersion != null) {
			String[] arr = dbVersion.split(".");
			try {
				return Integer.valueOf(arr[0]);
			} catch (Throwable e) {
			}
		}
		return 0;
	}
}