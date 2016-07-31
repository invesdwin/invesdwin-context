# invesdwin-context

This is a module system for the invesdwin software product line platform. It allows to configure an application on a per module basis. On application startup the bootstrap process collects the configuration snippets and creates a full application context. It also handles the lifecycle of the application (providing hooks for modules) for running and testing it. Tests also benefit from the flexibility of replacing bean instances with stubs, choosing different spring-xmls to be loaded, enabling embedded servers (e.g. webserver=jetty, database=h2, registry=juddi) on a per test basis via easy annotations. These embedded servers are themselves modules which are available in separate projects and can be even packaged into distributions for use in production environments where appropriate.

In comparison to [spring-boot](http://projects.spring.io/spring-boot/), this platform provides the following advantages:
- per module configuration instead of per application
- explicitly separate technology modules from domain modules and allow combining those per target environment into distributions
- customized Spring ApplicationContext per testcase without having to write one spring-xml per case, instead reusing and just selecting which spring-xml-snippets to use
- automatically merge individual logback.xmls, ehcache.xmls, properties files, spring-xmls, jaxb.xsds, web-fragment.xmls, aop.xmls or create jpa persistence.xmls during bootstrap from isolated snippets and classpath information

For more information on the concept and ideas behind this platform you can have a look at [this presentation](https://github.com/subes/invesdwin-context/blob/master/invesdwin-context-parent/invesdwin-context/doc/concept/invesdwin-concept.pdf) that was made for an earlier version of this platform where Ant+Ivy+Groovy was used for configuration management. Today these concepts got adapted into a new and improved maven implementation that provides many benefits over the older design while still keeping the same features as before. It was just the case that maven was not where it is today when this platform first came to life, but a switch was finally made when maven became more robust.

## Maven

Releases and snapshots are deployed to this maven repository:
```
http://invesdwin.de/artifactory/invesdwin-oss
```

Dependency declaration:
```xml
<dependency>
	<groupId>de.invesdwin</groupId>
	<artifactId>invesdwin-context-integration</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Base Classes

- AMain: this class can be used to implement your own main function that deals with running the application bootstrap and custom console arguments (using [args4j](http://args4j.kohsuke.org/)). It also processes "-Dparams=value" and sets them as system parameters to override existing defaults.
- ATest: this class should be extended by your unit tests. It provides test lifecycle methods to override in your tests like you were used to in JUnit 3.x, even though JUnit 4 is used now. It also handles the application bootstrap and decides whether it needs to be reinitialized depending on the current ApplicationContext configuration for the test. The context can be customized via setUpContextLocations(...), hooks like IStub, IContextLocation or by just adding annotations like @WebServer to your test (which is an annotation available in invesdwin-context-webserver that runs an embedded jetty server during tests by providing a stub implementation that checks for this annotation automatically for each test). Other such test annotations are available in other invesdwin projects and their respective modules.

## Hooks

The following lifecycle hooks are available, which can either just be added to a spring bean to automatically be picked up during bootstrap or alternatively registered manually in their respective XyzHookManager class.

- IBasePackageDefinition: this allows you to extend the classpath scanning packages to include your own ones. Per default only "de.invesdwin" is scanned, so your IBasePackageDefinition bean has to reside in this package as well to tell what other packages should be scanned (e.g. "com.mycompany") so that the bootstrap discovers beans/hooks/services in those packages too.
- IInstrumentationHook: allows to run additional instrumentations before any classes get loaded by the classloader. E.g. to run the [datanucleus enhancer](http://www.datanucleus.org/products/datanucleus/jpa/enhancer.html) automatically without having to add it as a java agent manually when you want to use that framework as your ORM provider.
- IPreStartupHook: allows to do application setup before the application bootstrap begins. E.g. to initialize some internal libraries once.
- IContextLocation: allows you to provide additional spring.xmls that have to be loaded for the current module. You can even let these spring.xmls be loaded due to some logic to e.g. load a service directly or access it via some remote communication depending on if the service implementation module is present in the distribution (by checking the presence of a class). Sometimes the order in which spring.xmls are loaded matters, in that case you can also define a position for the spring.xmls at hand.
- IStartupHook: allows to do some additional setup after the application bootstrap finished. E.g. to start some jobs or to create additional database indexes for specific tables after they were generated by hibernate.
- IShutdownHook: allows to do some cleanup tasks when the application is stopped (note that this won't work on [SIGKILL](https://en.wikipedia.org/wiki/Unix_signal#SIGKILL))
- IErrHook: provides means to be notified when an error gets logged in the Err class or by the uncaught exception handler. E.g. to show the error in a swing message box for a desktop application.
- IStub: to hook into the test lifecycle to implement stubs/mocks which can customize the application context, do file system cleanup after tests, setup additional test infrastructure and so on. Please note that IStub beans are removed from the application context during bootstrap when running in production mode, so they can safely be put into your modules src/main/java without having to worry about if they are only executed inside of tests.

## Tools

- PreMergedContext: with this ApplicationContext you can collect spring beans before the actual application is bootstrapped. This is a preliminary context with which the MergedContext is built. When integrating the platform into another platform, you have to make sure the static initializers inside this class are called very early during application startup or else the instrumentation will be too late since too many classes have already been loaded by the classloader. The following things are setup here:
	- load [invesdwin-instrument](https://github.com/subes/invesdwin-instrument) to ensure classes are instrumented by AspectJ and module specific instrumentations
	- discover base packages for further classpath scanning (IBasePackageDefinition)
	- determine if we are running inside a test envinronment (if src/test/java directory exists)
	- initialize our context directories which can be optionally used by our modules:
		- a process specific temp directory for classpath extension with generated classes that gets deleted when the application is exits (e.g. to load a dynamic instrumentation agent as in invesdwin-instrument or to create additional configuration files that get generated from classpath scanning like a persistence.xml) 
		- a cache directory inside our working directly for our application specific cache files that should be remembered between restarts (e.g. to store downloaded files that only have to be updated daily or monthly and should otherwise be remembered between application restarts)
		- a user specific home directory where files can be stored that can be accessed by different processes and applications (e.g. to store financial data used by multiple instances of parallel running strategy backtest processes)
	- make sure we use the correct [xalan](https://xalan.apache.org/) xslt transformer from our JVM (which might be discovered wrong depending on which libraries are in the classpath which will cause runtime errors)
	- initialize [Logback](http://logback.qos.ch/) as our [slf4j](http://www.slf4j.org/) provider by merging all config snippets we find in our classpath matching the path pattern "/META-INF/logback/*logback.xml" or "/META-INF/logback/*logback-test.xml". Note that the logback-test.xml can be put inside your "src/test/java" to only be loaded for your JUnit tests to increase the log level for specific libraries.
- InvesdwinInitializationProperties: If you want to prevent the static initializers from running entirely and prevent the bootstrap from running, you can disable it here. For instance when running inside a restricted environment like a [JForex](https://www.dukascopy.com/swiss/deutsch/forex/jforex/) bundled strategy, the initializers will fail because file system, classloader and other access is restricted. In that case you can still use most of the utilities available, but have to manage without spring-beans. Though this is not the normal deployment case and should thus only worry you when you go some uncommon path regarding application integration.
- MergedContext: inside this ApplicationContext the actual application runs after it was bootstrapped.
