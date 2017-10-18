# Add developer support for Javascript

Use a transpiler.  Use [JSweet](http://www.jsweet.org/) to transpile to Typescript or Javascript, or simply use Typescript
to transpile to Javascript.

# Use task configure method to avoid duplication between tasks

```
tasks.withType(JavaExec) {
    // …
}
```

# Dynamically create tasks based on build configuration

Allow the user to specify input folders and other configuration parameters.  Then create the tasks.
https://docs.gradle.org/4.1/userguide/tutorial_using_tasks.html#sec:dynamic_tasks.

It's unclear if this will interfere with determining task dependencies, or what other restrictions there may be.

# Speed up build by streamlining file copy tasks

Converting markdown to HTML and creating HTML files is fast.  Simply copying images files is extremely slow.  Try to make
use of parallelization, and replace the current file copying method with something more efficient.

# Use Gradle extension properties when applying the plug-in
 From the plug-in's apply() method, create all of the tasks that rely on the extension within a
`project.afterEvaluate {}` closure.
