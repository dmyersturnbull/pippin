# kokel-utils
Public Scala utilities to be shared across projects in the Kokel Lab.

## Organization

- _core_ includes general utilities and a Logback configuration, and it's a dependency for the other subprojects.
- _chem_ contains code for working with [CDK](https://github.com/cdk/cdk).
- _webservices_ contains code that interact with webservices.
- _math_ contains code for math, statistics, and machine learning. It's mainly built on [Breeze](https://github.com/scalanlp/breeze).

## Building

This will eventually be published on [Bintray](https://bintray.com/). In the meantime, to build this for dependencies to use, run:

```
sbt publish-local
```

This will publish all of these subprojects to your local [Ivy](https://ant.apache.org/ivy/) repository (usually at `~/.ivy2/cache`).

To import the published _chem_ artifact, add this to your SBT dependencies:

```
com.github.dmyersturnbull" %% "kokel-utils-core" % "0.0.1
```
