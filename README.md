# kl-common-scala
Public Scala utilities to be shared across projects in the [Kokel Lab](https://kokellab.com).

[![CircleCI](https://circleci.com/gh/kokellab/kl-common-scala.svg?style=shield)](https://circleci.com/gh/kokellab/kl-common-scala)

## organization

- _core_ includes general utilities and a Logback duplicate message filter, and it's a dependency for the other subprojects.
- _logconfig_ contains a [Logback](http://logback.qos.ch/) configuration that filters duplicate messages.
- _chem_ contains code for working with [CDK](https://github.com/cdk/cdk).
- _webservices_ contains code that interact with webservices.
- _math_ contains code for math, statistics, and machine learning. It's mainly built on [Breeze](https://github.com/scalanlp/breeze).
- _grammars_ contains formally specified grammars and their parsers.

## building

This will soon be published on [Maven Central](https://maven.org). In the meantime, to build this for dependencies to use, run:

```
sbt publish-local
```

This will publish all of these subprojects to your local [Ivy](https://ant.apache.org/ivy/) repository (usually at `~/.ivy2/cache`).

To import the published _core_ artifact, add this to your SBT dependencies:

```
com.github.kokellab" %% "kl-common-scala" % "0.1.1-SNAPSHOT"
```

## license

The authors and the Kokel Lab release this code and supporting files under the terms of the [Apache License, version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
The project was developed to support research at the Kokel Lab, fulfill degree requirements for [UCSF QBC](http://qbc.ucsf.edu/) PhD programs, and be useful to the public.
Due to the complexity of academic copyright and the [UC copyright policy](http://copyright.universityofcalifornia.edu/ownership/works-created-at-uc.html), the list of copyright owners is unknown.

#### authors
- Douglas Myers-Turnbull

