package de.invesdwin.context.beans.init.duplicate.one;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import de.invesdwin.context.beans.init.duplicate.IBeanWithSameClassName;

@Named("aname.for.BeanWithSameClassName")
@Immutable
public class BeanWithSameClassName implements IBeanWithSameClassName {

}
