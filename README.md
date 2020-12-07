# Functional Patterns in Java

This repository contains source code underlying
[Functional Patterns in Java](https://sebfisch.github.io/java-fun/).
The minimum required Java Version is 11.
The code has also been tested with Java 14.

You can install 
[Docker](https://docs.docker.com/get-docker/)
and 
[VS Code](https://code.visualstudio.com/download)
with the 
[Remote Development Extension Pack](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.vscode-remote-extensionpack)
to use a predefined development environment without creating (or adjusting) your own.
To use the predefined environment in VS Code 
download and unpack
[this repository](https://github.com/sebfisch/java-fun-code/archive/main.zip)
(or use git to clone it),
open the repository folder in VS Code,
click on the Remote Containers icon in the bottom-left corner, 
and select *Reopen Folder in Container*.

The first time you open the folder in a container will take a long time.
To check if the container was created successfully,
you can open a terminal in VS Code,
and run `mvn test` to run the test suite.
You can also run tests in VS Code
by opening a Java file containing tests
and clicking the *Run Test* link shown with declarations.

[© 2020](https://creativecommons.org/licenses/by-sa/2.0/)
by Sebastian Fischer