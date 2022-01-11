//https://stackoverflow.com/a/38636152
@groovy.transform.CompileStatic
@groovy.transform.TypeChecked
String strictHelloWorld(String hello) {
	var world = "Hello " + hello + "!"
	return world
}

world = strictHelloWorld(hello)

