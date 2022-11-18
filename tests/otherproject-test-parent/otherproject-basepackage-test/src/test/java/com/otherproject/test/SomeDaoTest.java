package com.otherproject.test;

import javax.annotation.concurrent.ThreadSafe;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.persistence.jpa.test.APersistenceTest;

@ThreadSafe
public class SomeDaoTest extends APersistenceTest {

    @Inject
    private SomeDao someDao;

    @Test
    public void test() {
        SomeEntity e = new SomeEntity();
        e.setSomeColumn("asdf");
        e = someDao.save(e);
        someDao.delete(e);
    }

}
