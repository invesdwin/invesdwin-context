# invesdwin-context

This is the core of the lightweight module system for the invesdwin software product line platform. It allows to configure an application on a per module basis. On application startup the bootstrap process collects the configuration snippets and creates a full application context. It also handles the lifecycle of the application (providing hooks for modules) for running and testing it. Tests also benefit from the flexibility of replacing bean instances with stubs, choosing different spring-xmls to be loaded depending on the circumstances and enabling embedded servers (e.g. webserver=jetty, database=h2, registry=juddi) on a per test basis via annotations. These embedded servers are themselves modules which are available in separate projects and can be even packaged into distributions for use in production environments where appropriate.

This platform shares some of the goals of [spring-boot](http://projects.spring.io/spring-boot/). In comparison this platform provides the following advantages:
- per module configuration instead of per application
- explicitly separate technology modules from domain modules and allow combining those per target environment into distributions
- customized spring `ApplicationContext` per testcase without having to write one spring-xml per case, instead reusing and just selecting which spring-xml-snippets to use (essentially solving Spring-XML-Hell in complex testing scenarios)
- automatically merge individual logback.xmls, ehcache.xmls, properties files, spring-xmls, jaxb.xsds, web-fragment.xmls, aop.xmls or create jpa persistence.xmls during bootstrap from isolated snippets and classpath information

For more information on the concept and ideas behind this platform you can have a look at [this presentation](https://github.com/subes/invesdwin-context/blob/master/invesdwin-context-parent/invesdwin-context/doc/concept/invesdwin-concept.pdf) that was made for an earlier version of this platform where Ant+Ivy+Groovy was used for configuration management. Today these concepts got adapted into a new and improved Maven implementation that provides many benefits over the older design while still keeping the same features as before. It was just the case that Maven was not where it is today when this platform first came to life, but a switch was finally made when Maven became more robust and faster.

## Maven

Releases and snapshots are deployed to this maven repository:
```
http://invesdwin.de/artifactory/invesdwin-oss-remote
```

Dependency declaration:
```xml
<dependency>
	<groupId>de.invesdwin</groupId>
	<artifactId>invesdwin-context</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Parent Pom

When setting up modules or distributions it is recommended to make `invesdwin-context-parent` the parent pom of your specific module/distribution pom.xml. This provides the following benefits respectively. This enables:
- common configurations for quality assurancy plugins in maven and eclipse
- handles the eclipse project settings and git ignores automatically using the [invesdwin-maven-plugin](https://github.com/subes/invesdwin-maven-plugin) (which is required to make the JAXB-Generator work properly or else the invesdwin.xjb is missing)
- automatically fixes umlauts in properties files
- allows to put resource files into src/main/java instead of src/main/resources (which helps with Wicket html/page files to not requiring you to manage two equal package trees which can get hairy with refactorings)
- provides a robust [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/) config that allows you to create fat-jars inside of your distributions

If you setup a product (being a collection of modules and distributions) it is recommended to create an intermediate parent pom so you can define your own additional dependency management and plugin overrides (see the `granatasoft-remotelist-parent` example web project [here](https://github.com/subes/invesdwin-nowicket/tree/master/invesdwin-nowicket-parent/invesdwin-nowicket-examples)).
Though you are free to roll your own parent pom where you just cherry-pick the parts that you like from the parent pom here, or do everything on your own. If you are just interested in the dependency management you can import this pom as a [bill of materials](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html).  See the [included test projects](https://github.com/subes/invesdwin-context/tree/master/tests) for some examples of the different ways of referencing invesdwin-context modules with maven.

In order to fully benefit from the module system, you should follow the best practice of modules always having the package type `jar`. Only distributions should repackage modules into other package types like `war`, `ear`, `zip` and so on. This allows the highest flexibility in module reuse between different products and various distributions for different customers and target environments.

## Base Classes

- **AMain**: this class can be used to implement your own main function that deals with running the application bootstrap and handling custom console arguments (using [args4j](http://args4j.kohsuke.org/)). It also processes `-Dparams=value` and sets them as system parameters to override existing defaults.
- **ATest**: this class should be extended by your unit tests. It provides test lifecycle methods to override in your tests like you were used to in JUnit 3.x, even though JUnit 4 is used now. It also handles the application bootstrap and decides whether it needs to be reinitialized depending on the current `ApplicationContext` configuration for the test. The context can be customized via `setUpContextLocations(...)`, hooks like IStub, IContextLocation or by just adding annotations like `@WebServerTest` to your test (which is an annotation available in `invesdwin-context-webserver` that runs an embedded jetty server during tests by providing a stub implementation that checks for this annotation automatically for each test). Other such test annotations are available in other invesdwin projects and their respective modules. You can also activate/deactivate/replace spring beans via `setUpContext(TestContext)` to fit your testing requirements.

## Hooks

The following lifecycle hooks are available, which can either just be added to a spring bean to be automatically picked up during bootstrap or alternatively registered manually in their respective `XyzHookManager` class.

- **IBasePackageDefinition**: this allows you to extend the classpath scanning packages to include your own ones. Per default only `de.invesdwin` is scanned, so your `IBasePackageDefinition` bean has to reside in this package to tell what other packages should be scanned (e.g. `com.mycompany`) so that the bootstrap discovers beans/hooks/services in your packages too.
- **IInstrumentationHook**: allows to run additional instrumentations before any classes get loaded by the classloader. E.g. to run the [datanucleus enhancer](http://www.datanucleus.org/products/datanucleus/jpa/enhancer.html) automatically without having to add it as a java agent manually when you want to use that framework as your ORM provider. Please note that these hooks need to be defined as a service provider inside `/META-INF/services/de.invesdwin.context.beans.hook.IInstrumentationHook`. Just put one or more fully qualified class names there for your hook implementations so they can get picked up during bootstrap. Discovering them via classpath scanning (like the other hooks get discovered) would defeat the purpose, since scanning would load too many classes which won't get instrumented then by your hooks.
- **IPreStartupHook**: allows to do application setup before the application bootstrap begins. E.g. to initialize some internal libraries once.
- **IContextLocation**: allows you to provide additional spring.xmls that have to be loaded for the current module. You can even let these spring.xmls be loaded based on some logic (e.g. to load a service directly or access it via some remote protocol depending on if the service implementation module is present in the distribution, by checking the presence of a class). Sometimes the order in which spring.xmls are loaded matters, in that case you can also define a position for the spring.xmls (begin, middle, end).
- **IStartupHook**: allows to do some additional setup after the application bootstrap finished. E.g. to start some jobs or to create additional database indexes for specific tables after they were generated by Hibernate. These hooks are start in parallel when the bootstrap finishes and can run endlessly since they are non-blocking for the application boostrap. If you want to delay the boostrap process you can use `IBlockingStartupHook` instead, though you should not run endless tasks with that.
- **IShutdownHook**: allows to do some cleanup tasks when the application is stopped (note that this won't work on [SIGKILL](https://en.wikipedia.org/wiki/Unix_signal#SIGKILL)). E.g. deleting some temp/cache files.
- **IErrHook**: provides means to be notified when an error gets logged in the Err class or by the uncaught exception handler. E.g. to show the error in a swing message box of a desktop application.
- **IStub**: to hook into the test lifecycle to implement stubs/mocks which can customize the application context, do file system cleanup after tests, setup additional test infrastructure and so on. Please note that IStub beans are removed from the `ApplicationContext` during bootstrap when running in production mode, so they can safely be put into your modules `src/main/java` without having to worry about if they are only executed inside of tests. This convention of making modules test-aware also does not require you to add test-jar maven dependencies, which themselves won't get transitive dependencies resolved in Maven and might introduce some unwanted stubs that you only need for testing the actual module itself.

## Tools

- **PreMergedContext**: with this `ApplicationContext` you can collect spring beans before the actual application is bootstrapped. This is a preliminary context with which the MergedContext is built. When integrating the platform into another platform, you have to make sure the static initializers inside this class are called very early during application startup or else the instrumentation will be too late since too many classes have already been loaded by the classloader. The following things are setup here:
	- load [invesdwin-instrument](https://github.com/subes/invesdwin-instrument) to ensure classes are instrumented by AspectJ and module specific instrumentations
	- discover base packages for further classpath scanning (`IBasePackageDefinition`)
	- determine if we are running inside a test envinronment (if `src/test/java` directory exists, see `ContextProperties.IS_TEST_ENVIRONMENT`)
	- initialize our context directories which can be optionally used by our modules (see `ContextProperties` class):
		- a process specific temp directory for classpath extension with generated classes that gets deleted when the application is exits (e.g. to load a dynamic instrumentation agent as in `invesdwin-instrument` or to create additional configuration files that get generated from classpath scanning like a persistence.xml) 
		- a cache directory inside our working directory for our application specific cache files that should be remembered between restarts (e.g. to store downloaded files that only have to be updated daily or monthly and should otherwise be remembered between application restarts)
		- a process specific log folder inside our working directory
		- a user specific home directory (`$HOME/.invesdwin`) where files can be stored that can be accessed by different processes and applications (e.g. to store financial data used by multiple instances of parallel running strategy backtest processes)
	- make sure we use the correct [xalan](https://xalan.apache.org/) xslt transformer from our JVM (which might be discovered wrong depending on which libraries are in the classpath, which will cause runtime errors if not fixed)
	- initialize [Logback](http://logback.qos.ch/) as our [SLF4J](http://www.slf4j.org/) provider by merging all config snippets we find in our classpath matching the classpath pattern `/META-INF/logback/*logback.xml`, or `/META-INF/logback/*logback-test.xml` for overrides. Note that the logback-test.xml can be put inside your `src/test/java` to only be loaded for your JUnit tests to increase the log level for specific libraries.
	- load properties files that match the classpath pattern `/META-INF/*.properties` and make them available globally as system properties. You can put machine specific properties in the file `${user.home}/.invesdwin/system.properties`. You can set up developer specific properties that get them loaded during testing in any module by placing a `/META-INF/env/${USERNAME}.properties` and defining your specific property overrides there. Distributions of your applications can package a `/META-INF/env/distribution.properties` to override the properties for your target customer/environment. Please note that security sensitive information should be configurable in a more sensitive fashion. System properties can be read by any other process. So either tighten up the computers the processes run on or tighten up the application themselves (e.g. when delivering fat-clients to end-customers). Anyway when security is not so much a concern, it can be quite handy for an admin to differentiate process configurations by looking at their system properties via JVisualVM on a server.
	- set a default network timeout to prevent connections from stalling threads because they never get a response. It is generally better to retry since otherwise connections can become stalled and never respond, while another try would get an immediate response depending on what endpoint is tried to be reached (JVM default is an unlimited timeout which is bad)
	- preregister serializable classes for [FST](http://ruedigermoeller.github.io/fast-serialization/) to make deep cloning of value objects faster
	- register an uncaught exception handler so that all exceptions get at least logged once (especially valuable in multi-threaded applications)
	- register a URI extension to support the `classpath:` protocol which might be needed to easily setup third party frameworks that only support URI paths
	- set the JVM default timezone to UTC to get commonality in timestamps for distributed applications. This makes comparing logs and IPC (Inter-Process-Communication) between servers in different countries easier. Note that you can override this via JVM arguments as noted in the application bootstrap hint on the console to e.g. keep the default timezone for a desktop application. In that case you have to care for timezones on application boundaries.
	- ensure the JVM default file encoding is set to UTF-8, else we might get funny problems with special characters on console or when reading/writing files which are really hard to troubleshoot. The file encoding can sometimes be wrong on misconfigured servers and workstations.
- **PlatformInitializerProperties**: If you want to prevent the static initializers from running entirely and prevent the bootstrap from happening, you can disable it here. For instance when running inside a restricted environment like a [JForex](https://www.dukascopy.com/swiss/deutsch/forex/jforex/) bundled strategy, the initializers will fail because the file system, classloaders and other resources are restricted. In that case you can still use most of the functionality available, but have to manage without spring-beans. In another use case you might want to integrate an invesdwin module into a different platform, but you do not want the static initializers to change any JVM defaults since they might interfere with your main platform. In that case disabling some static initializers is the only solution. You can also gain more fine granular control about which initializations should happen by overriding methods in `DefaultPlatformInitializer`, just set your customized instance via `PlatformInitializerProperties.setInitializer(...)` before any initializers have run. Though this is not the normal deployment case and you should thus only worry about it when you go some uncommon path regarding application integration. Or on the other hand if you just want to change the default directories you can also do that here.
- **MergedContext**: inside this `ApplicationContext` the actual application runs after it was bootstrapped. The bootstrap is invoked by the first call of `MergedContext.autowire(...)` which is automatically invoked by `AMain` or `ATest` application entry points. Note that you can also call this method manually in your objects (which are not `@Configurable` or spring beans) to get dependency injection. Also you can provide `ApplicationContexts` to set them as children of the `MergedContext` (can be wrapped in `DirectChildContext` and `ParentContext` to change the handling) to create a `ApplicationContext` hierarchy for special framework integration needs. The bootstrap itself accomplishes the following things:
	- collecting all spring.xml `IContextLocations` that are supposed to build the final `ApplicationContext`
	- collect all ehcache config files matching the classpath pattern `/META-INF/ehcache/*ehcache.xml` and setup the caches (e.g. used by hibernate or datanucleus, but also is the provider for the JCache API that you might want to use)
	- setup spring subsystems like `@Configurable`, `@Transactional`, `@Scheduled`, `@Cacheable`, `@Async` so that your beans can utilize them easily
	- collect XSDs matching the classpath pattern `/META-INF/xsd/*.xsd` to setup the JAXB validation context properly for use in the Marshaller util inside `invesdwin-context-integration` (when that module is in the classpath)
	- do classpath scanning for JPA entities to automatically generate a persistence.xml for the appropriate persistence units and ORMs that are configured (see [invesdwin-context-persistence-jpa](https://github.com/subes/invesdwin-context-persistence) project for further information about this polyglot persistence feature)
	- do other module specific initialization that is integrated using the various hooks (e.g. launching a webserver, juddi registry, ApacheDS LDAP server, some UI, or whatelsenot)
	- even though this might seem to be a lot to take care of, the application bootstrap is trimmed to run very fast to ensure quick development roundturns (a typical web application with an embedded database and webserver starts in about 8 seconds during development/testing on our workstations)
- **Err**: this utility class can be called to log errors. It ensures errors are only logged once or when the stacktrace of an already logged exceptions gets extended by more exceptions (it will tell you in the log about an already logged reference exception id by concatening the cause ids). It will log the full stacktrace inside `log/error.log` and a shortened stacktrace on the console and inside `log/common.log`.
- **Log**: this is a wrapper around SLF4J to provide some convenience constructors and to support `%s` style text placeholders like one is used from `String.format(...)` instead of the SLF4J `{}`. Sometimes it is hard to know where one should use which notations, so for invesdwin we settled on `%s` and make sure all our utilities recognize that notation. Nothing is worse than a broken log statement when you want to troubleshoot some hairy production problem, so we try everything to minimize these common coding errors.
- **AProperties**: this is a wrapper for [commons-configuration](https://commons.apache.org/proper/commons-configuration/) that handles thread safety, adds useful error messages for incorrect or missing properties and provides additional convenience type transformations to make handling properties of all sorts easier (providing implementations such as URLProperties, SystemProperties, FileProperties)
- **NativeLibrary**: to allow packaging of native libraries for various target architectures inside your modules (e.g. used by a portscanner module to integrate [jpcapng](https://sourceforge.net/projects/jpcapng/))
- **BeanValidator**: to conveniently verify the consistency of your value object where ever you might need to do so using the [BeanValidation Annotations](http://beanvalidation.org/)
- **SystemPropertiesDefinition**: when defining this bean inside of your spring-xml, you are able to reference the system properties via the `${PROPERTYNAME}` notation. This bean also allows you to set additional context dependant system properties from inside your spring-xml (which can be loaded depending on some logic via `IContextLocation`).

## Webserver Module

This module packages an embedded [jetty server](http://www.eclipse.org/jetty/) to provide a servlet container for web services and web frameworks either during testing by annotating your test with `@WebServerTest` or to deploy your application with a distribution that contains an embedded webserver. You can also call `WebserverContextLocation.activate()`/`.deactivate()` before the application is bootstrapped to enable/disable the embedded webserver programmatically. 
- to provide a convenience entry point for your web application distribution, just define the main class as `de.invesdwin.context.webserver.Main` or roll your own to add more configuration options.
- the property `de.invesdwin.context.integration.IntegrationProperties.WEBSERVER_BIND_URI` with its default value `http://localhost:9001` is referenced from the `invesdwin-context-integration` module to setup the server. 
	- change the port in your developer/distribution properties override to suit your needs. When the server starts, it logs where it is listening to. 
	- you can even change the protocol to `https://` to enable SSL support. Though you should make sure to change the `de.invesdwin.context.webserver.WebserverProperties.KEYSTORE_*` properties to switch to a real certificate instead of the auto generated one inside the module itself. Or use the more common approach of settings up a reverse proxy on your apache webserver that adds ssl for your website (see [chapter 4.6 from the invesdwin-NoWicket documentation](http://invesdwin.de/nowicket/installation)).
	- on server startup the classpath pattern `/META-INF/web/web-fragment.xml` is used to find all module specific web app config snippets. They are then merged to build the actual web.xml configuraton for the webserver. These are not placed in `/META-INF/web-fragment.xml` in order to not get automatically picked up when deploying invesdwin modules into a `war` archive. In that case the distribution should take care of relocating and merging the files into a web.xml as similarly handled by the fat-jar feature inside the `maven-shade-plugin` definition of the `invesdwin-context-parent` pom. Otherwise one could not decide to override the automatically generated web.xml with a custom created one in his distribution or to manually disable/rename/reconfigure some services that are shipped with the modules default config.

Also note that with this module generally resources are served from the classpath (as this is the only place where modules can store their resources since they are packaged as a `jar`). The classpath underlies a resource blacklist that prevents class and java files from being served by accident to ensure security (see `BlacklistedWebAppContext`). Also file browsing is disabled on the server for security reasons.

## Report Module

This module bundles tools and utilities for creating reports of various types:

- **PDF**: create PDF files using [DynamicReports](http://www.dynamicreports.org/) which is a pure Java API frontend for [JasperReports](http://community.jaspersoft.com/project/jasperreports-library) so you don't have to fiddle around with JRXML files.
- **Excel**: sometimes tabular data is favorable since it allows easy editing of the raw data, this is handled by [Apache POI]( https://poi.apache.org/).
- **Chart**: you might want to include charts in your reports, this can be handled by [JFreeChart](http://www.jfree.org/jfreechart/). Notable utilties provided for this are:
	- `Aggragating(Ohlc)PointsCollection`: generating charts from large datasets is very slow and memory intensive without having any visual benefit, thus it makes sense to prefilter the data by aggregating points into to-be-pixels. This is handled by this special collection for XY-Points and OHLC-Bars spectively. You normally don't need more than around 10,000 datapoints for a XY-Chart or more than 1,000 datapoints for a OHLC-Chart to look good.
	- `AJFreeChartVisitor`: allows to visit and modify chart elements to apply some common modifications on charts by post-processing. The included `JFreeChartLocaleChanger` and `JFreeChartWeekendGapRemover` are two examples for such cases. The first one adjusts date and number formats in the chart according to the given locale (JFreeChart otherwise uses the JVM default locale) and the second one removes weekends from `DateAxis` instances by setting an appropriate `SegmentedTimeline`. 
	- `JFreeChartExporter`: allows you to easily export charts lazily, memory sensitive and in parallel as desired into different file formats and dimensions. It also allows you to scale the fonts via a multiplier so they are not too small on higher chart resolutions.

## More Modules

There are a few more modules available in their respective github projects including their individual documentation:

- **Integration Modules**: IO, IPC, Messaging, Services, Batch, Hadoop
	- https://github.com/subes/invesdwin-context-integration 
- **Persistence Modules**: Databases, SQL, NoSQL
	- https://github.com/subes/invesdwin-context-persistence
- **Security Modules**: Authentication, Authorization, SSO
	- https://github.com/subes/invesdwin-context-security 
- **Client Modules**: Desktop and Web Frontends
	- https://github.com/subes/invesdwin-context-client
- **R Modules**: Scripting with R
	- https://github.com/subes/invesdwin-context-r 
- **Python Modules**: Scripting with Python
	- https://github.com/subes/invesdwin-context-python
- **Webproxy Modules**: Download Manager for Web Scraping that supports Proxy Servers
	- https://github.com/subes/invesdwin-webproxy
