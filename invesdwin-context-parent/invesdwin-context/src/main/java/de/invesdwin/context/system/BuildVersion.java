package de.invesdwin.context.system;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.internal.ABuildVersion;
import de.invesdwin.norva.apt.buildversion.BuildVersionDefinition;

/**
 * This class provides a static build version to check against a dynamic one that is copied at compile time to the place
 * where the check is needed. This uses the compiler optimization that replaces static final references with literals in
 * code. Thus it is possible to make code depend on a specific compiled version to create assertions that verify all
 * libs are of the same version.
 * 
 * http://stackoverflow.com/questions/5173372/java-static-final-values-replaced-in-code-when-compiling
 * 
 */
@Immutable
@BuildVersionDefinition(name = "de.invesdwin.context.system.internal.ABuildVersion")
public class BuildVersion extends ABuildVersion {

}
