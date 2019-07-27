package io.webApp.springbootstarter.notes;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * Random Unique Identifier ID generator class, implements IdentifierGenerator
 * class
 * 
 * @author satishkumaranbalagan
 *
 */
public class MyGenerator implements IdentifierGenerator {

	public static final String generatorName = "myGenerator";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.id.IdentifierGenerator#generate(org.hibernate.engine.spi.
	 * SharedSessionContractImplementor, java.lang.Object)
	 */
	@Override
	public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object object)
			throws HibernateException {
		return UUID.randomUUID().toString();
		// or any other logic you'd like for generating unique IDs
	}
}
