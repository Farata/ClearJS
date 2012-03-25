package com.farata.test.service;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.hibernate.Session;

import clear.cdb.utils.SessionFactoryUtils;

import com.farata.test.entity.Company;
import com.farata.test.service.generated.*;

public class CompanyService extends _CompanyService {
	public java.lang.Object getSomething(java.lang.Integer id) {
		UserTransaction tx = null;
		try {
			tx = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");                            
			tx.begin();
			Session session = SessionFactoryUtils.getCurrentSession();
			Company entity = (Company)session.get(Company.class, id);
			com.farata.test.entity.Company dto = new com.farata.test.entity.Company();
			dto.setCompany(entity.getCompany());
			dto.setId(entity.getId());
			tx.commit();
			return dto;
		} catch (Throwable e) {
			try {
				tx.rollback();
			} catch (Throwable th) {
				throw new RuntimeException(th);
			}
			throw new RuntimeException(e);
		}
	}		
}