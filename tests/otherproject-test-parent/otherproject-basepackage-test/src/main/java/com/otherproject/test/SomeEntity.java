package com.otherproject.test;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.persistence.jpa.api.dao.entity.AEntity;
import jakarta.persistence.Entity;

@SuppressWarnings("serial")
@NotThreadSafe
@Entity
public class SomeEntity extends AEntity {

    private String someColumn;

    public String getSomeColumn() {
        return someColumn;
    }

    public void setSomeColumn(final String someColumn) {
        this.someColumn = someColumn;
    }

}
