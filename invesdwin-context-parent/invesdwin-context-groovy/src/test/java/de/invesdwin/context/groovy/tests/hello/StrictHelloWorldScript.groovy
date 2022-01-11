//inputs/outputs are also strict when using it like this
var hello = binding.getVariable("hello")
var world = "Hello " + hello + "!"
binding.setVariable("world", world)