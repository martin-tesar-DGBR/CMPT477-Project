## Build Instructions
In the terminal, run `$ mvn package` to build the project.

On Linux, run `$ java -jar target/verif-comp-1.0.jar <input>` to run the executable jar.

On Windows, run `$ java -jar target\verif-comp-1.0.jar <input>` to run the executable jar.

`<input>` is the (relative) path to the program to be interpreted.

Note that if exporting the project, only `verif-comp-1.0.jar` file and the `lib/`
directory are needed; the .jar file expects the library folder to have the same structure
as it has in the target directory.

Update the build instructions if any external libraries are added.
