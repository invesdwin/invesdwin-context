System.out.println("getPercent");
if(getPercent != void)
	throw new Exception("getPercent already defined!");
getPercent = callback("getPercent");
getPercentType = getPercent.getClass();
System.out.println(getPercentType);
System.out.println(getPercent);
if(getPercentType != Double.class)
	throw new Exception("getPercent not Double!");
callback("setPercent",getPercent);

System.out.println("getPercentVector");
if(getPercentVector != void)
	throw new Exception("getPercentVector already defined!");
getPercentVector = callback("getPercentVector");
getPercentVectorType = getPercentVector.getClass().getComponentType();
System.out.println(getPercentVectorType);
System.out.println(getPercentVector);
if(getPercentVectorType != double.class)
	throw new Exception("getPercentVector not Double!");
callback("setPercentVector",getPercentVector);

System.out.println("getPercentVectorAsList");
if(getPercentVectorAsList != void)
	throw new Exception("getPercentVectorAsList already defined!");
getPercentVectorAsList = callback("getPercentVectorAsList");
getPercentVectorAsListType = getPercentVectorAsList.getClass().getComponentType();
System.out.println(getPercentVectorAsListType);
System.out.println(getPercentVectorAsList);
if(getPercentVectorAsListType != double.class)
	throw new Exception("getPercentVectorAsList not Double!");
callback("setPercentVectorAsList",getPercentVectorAsList);

System.out.println("getPercentMatrix");
if(getPercentMatrix != void)
	throw new Exception("getPercentMatrix already defined!");
getPercentMatrix = callback("getPercentMatrix");
getPercentMatrixType = getPercentMatrix[0].getClass().getComponentType();
System.out.println(getPercentMatrixType);
System.out.println(getPercentMatrix);
if(getPercentMatrixType != double.class)
	throw new Exception("getPercentMatrix not Double!");
callback("setPercentMatrix",new Object[]{getPercentMatrix});

System.out.println("getPercentMatrixAsList");
if(getPercentMatrixAsList != void)
	throw new Exception("getPercentMatrixAsList already defined!");
getPercentMatrixAsList = callback("getPercentMatrixAsList");
getPercentMatrixAsListType = getPercentMatrixAsList[0].getClass().getComponentType();
System.out.println(getPercentMatrixAsListType);
System.out.println(getPercentMatrixAsList);
if(getPercentMatrixAsListType != double.class)
	throw new Exception("getPercentMatrixAsList not Double!");
callback("setPercentMatrixAsList",new Object[]{getPercentMatrixAsList});
