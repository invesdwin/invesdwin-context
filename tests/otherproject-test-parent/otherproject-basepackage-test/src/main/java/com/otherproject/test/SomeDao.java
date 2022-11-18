package com.otherproject.test;

import javax.annotation.concurrent.ThreadSafe;
import jakarta.inject.Named;

import de.invesdwin.context.persistence.jpa.api.dao.ADao;

@ThreadSafe
@Named
public class SomeDao extends ADao<SomeEntity> {

}
