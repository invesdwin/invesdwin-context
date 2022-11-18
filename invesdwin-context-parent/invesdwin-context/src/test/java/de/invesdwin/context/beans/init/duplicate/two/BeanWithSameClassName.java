package de.invesdwin.context.beans.init.duplicate.two;

import javax.annotation.concurrent.Immutable;
import jakarta.inject.Named;

import de.invesdwin.context.beans.init.duplicate.IBeanWithSameClassName;

@Named
@Immutable
public class BeanWithSameClassName implements IBeanWithSameClassName {

}
