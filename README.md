# Pidesco
Pidesco stands for **P**edagogical **IDE** for **S**oftware **Co**mponents and consists of a toy IDE for didactic purposes, useful to exercise component-based software engineering. Pidesco is built on top of Equinox/OSGi, offering a very simple infrastructure for plugging in views and tools. This repository also contains a file system browser component (`pt.iscte.pidesco.projectbrowser`), as well as a basic Java code editor (`pt.iscte.pidesco.javaeditor`). Using these as a starting point, apprentices may develop other IDE components that integrate with them.

## Code Generator
Code Generator is a Pidesco component that provides a generic interface that can be implemented by other extensions.

**Extension Points:**
* actions

``` java
package pt.iscte.pidesco.codegenerator.extensibility;

import org.eclipse.swt.widgets.Composite;

public interface CodeGeneratorAction {
	//Create view elements
	void run(Composite viewArea);

	//Method that return the generated code
	String generateFileContent(String content);
}
```

## POJO Generator : Code Generator Extension
**P**lain **O**ld **J**ava **O**bjects allows to serialize and deserialize JSON objects to Java objects and vice versa.

<p align="center">
  <img src="https://i.imgur.com/WZYBNrL.png" title="POJO Generator" width="80%" height="80%"/>
</p>
