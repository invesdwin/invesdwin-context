package com.otherproject.scripting;

import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.context.integration.script.IScriptTaskResults;
import de.invesdwin.context.matlab.runtime.contract.AScriptTaskMatlab;
import de.invesdwin.context.python.runtime.contract.AScriptTaskPython;
import de.invesdwin.context.r.runtime.contract.AScriptTaskR;
import de.invesdwin.util.assertions.Assertions;

public class ScriptingWithoutBootstrapMain {

	public static void main(String[] args) {
		// disable invesdwin bootstrap to make this more lightweight
		PlatformInitializerProperties.setAllowed(false);

		/*
		 * alternatively only disable e.g. logback configuration so that you can use
		 * your existing logger configuration
		 * 
		 * if you want to use log4j2 instead of logback, just exclude the maven
		 * artifact, include the slf4j redirect for logback and add the dependency for
		 * slf4j-log4j2
		 */
//		PlatformInitializerProperties.setInitializer(new DelegatePlatformInitializer(PlatformInitializerProperties.getInitializer()) {
//			@Override
//			public void initLogbackConfigurationLoader() {
//				//noop to disable this bootstrap step
//			}
//		});

		callScriptPython();
		callScriptR();
		callScriptMatlab();

		// need to explicitly call system.exit so that threads are stopped properly
		System.exit(0);
	}

	private static void callScriptPython() {
		final AScriptTaskPython<String> script = new AScriptTaskPython<String>() {

			@Override
			public void populateInputs(final IScriptTaskInputs inputs) {
				inputs.putString("hello", "World");
			}

			@Override
			public void executeScript(final IScriptTaskEngine engine) {
				// execute this script inline:
				engine.eval("world = \"Hello \" + hello + \"!\"");
				// or run it from a file:
				// engine.eval(new ClassPathResource("HelloWorldScript.py", getClass()));
			}

			@Override
			public String extractResults(final IScriptTaskResults results) {
				return results.getString("world");
			}
		};
		final String result = script.run(); // optionally pass a specific runner as an argument here
		Assertions.assertThat(result).isEqualTo("Hello World!");
		System.out.println("Python: " + result);
	}

	private static void callScriptMatlab() {
		final AScriptTaskMatlab<String> script = new AScriptTaskMatlab<String>() {

			@Override
			public void populateInputs(final IScriptTaskInputs inputs) {
				inputs.putString("hello", "World");
			}

			@Override
			public void executeScript(final IScriptTaskEngine engine) {
				// execute this script inline:
				engine.eval("world = strcat({'Hello '}, hello, '!')");
				// or run it from a file:
				// engine.eval(new ClassPathResource("HelloWorldScript.m", getClass()));
			}

			@Override
			public String extractResults(final IScriptTaskResults results) {
				return results.getString("world");
			}
		};
		final String result = script.run(); // optionally pass a specific runner as an argument here
		Assertions.assertThat(result).isEqualTo("Hello World!");
		System.out.println("Matlab: " + result);
	}

	private static void callScriptR() {
		final AScriptTaskR<String> script = new AScriptTaskR<String>() {

			@Override
			public void populateInputs(final IScriptTaskInputs inputs) {
				inputs.putString("hello", "World");
			}

			@Override
			public void executeScript(final IScriptTaskEngine engine) {
				// execute this script inline:
				engine.eval("world <- paste(\"Hello \", hello, \"!\", sep=\"\")");
				// or run it from a file:
				// engine.eval(new ClassPathResource("HelloWorldScript.R", getClass()));
			}

			@Override
			public String extractResults(final IScriptTaskResults results) {
				return results.getString("world");
			}
		};
		final String result = script.run(); // optionally pass a specific runner as an argument here
		Assertions.assertThat(result).isEqualTo("Hello World!");
		System.out.println("R: " + result);
	}

}
