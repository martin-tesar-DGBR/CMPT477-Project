## Build Instructions
In the terminal, run `$ mvn package` to build the project.

On Linux, run `$ java -jar target/verif-comp-1.0.jar <input>` to run the executable jar.

On Windows, run `$ java -jar target\verif-comp-1.0.jar <input>` to run the executable jar.

`<input>` is the (relative) path to the program to be interpreted.

Note that if exporting the project, only `verif-comp-1.0.jar` file and the `lib/`
directory are needed; the .jar file expects the library folder to have the same structure
as it has in the target directory.

Update the build instructions if any external libraries are added.

## About
The purpose of this project is to create a toy language that includes deductive verification capabilities. A sample code snippet of the `verif-comp` language is as below:

```
{
  my_var := 6
  x := 3 - 2 * my_var + 5
  if x > 0 {
    y := x
  }
  else {
    y := -x
  }
  check(!(y < 0))
  print(y)
}
```

The `check()`, or `static_assert` in other languages, is verified at compile time.

See the `v1.0-havoc` tag for a variant of the language that omits multiplication in favour of user input, which better demonstrates the capabilities of the project.

See `report.pdf` for the report submitted as part of the final project for CMPT 477, Introduction to Formal Verification.
