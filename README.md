# Pippin

Various Scala utility code.
Named after Peregrin Took, to keep a LOTR theme going.
Supports Scala 2.13+ and [Dotty / Scala 3](https://dotty.epfl.ch/).

### Organization

- _core_ includes general utilities and a Logback duplicate message filter. It's a dependency for the other subprojects.
- _logconfig_ contains a [Logback](http://logback.qos.ch/) configuration that filters duplicate messages.
- _misc_ contains miscellaneous code, especially code with bulky dependencies.
- _grammars_ contains formally specified grammars and their parsers.

### To build and use

For now, build with `sbt publish-local`.
To import the published _core_ artifact, add this to your SBT dependencies:

```
com.github.dmyersturnbull" %% "pippin" % "0.6.0-SNAPSHOT"
```

By [Douglas Myers-Turnbull](https://github.com/dmyersturnbull). Apache-2.0.
