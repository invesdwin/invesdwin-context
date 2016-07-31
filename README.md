# invesdwin-context

This is a module system for the invesdwin software product line platform. It allows to configure an application on a per module basis. On application startup the bootstrap process collects the configuration snippets and creates a full application context. It also handles the lifecycle of the application (providing hooks for modules) for running and testing it. Tests also benefit from the flexibility of replacing bean instances with stubs, choosing different spring-xmls to be loaded, enabling embedded servers (e.g. webserver=jetty, database=h2, registry=juddi) on a per test basis via easy annotations. These embedded servers are themselves modules which are available in separate projects and can be even packaged into distributions for use in production environments where appropriate.

In comparison to [spring-boot](http://projects.spring.io/spring-boot/), this platform provides the following advantages:
- per module configuration instead of per application
- explicitly separate technology modules from domain modules and allow combining those per target environment into distributions
- customized Spring ApplicationContext per testcase without having to write one spring-xml per case, instead reusing and just selecting which spring-xml-snippets to use
- automatically merge individual logback.xmls, ehcache.xmls, properties files, spring-xmls, jaxb.xsds, web-fragment.xmls, aop.xmls or create jpa persistence.xmls during bootstrap from isolated snippets and classpath information

For more information on the concept and ideas behind this platform you can have a look at [this presentation](https://github.com/subes/invesdwin-context/blob/master/invesdwin-context-parent/invesdwin-context/doc/concept/invesdwin-concept.pdf) that was made for an earlier version of this platform where Ant+Ivy+Groovy was used for configuration management. Today most of these concept got adapted into a new and improved maven implementation that provides many benefits over the older design while still keeping the same features as before. It was just the case that maven was not where it is today when this platform first came to life.

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
