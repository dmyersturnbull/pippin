# Pippin

Various Scala utility code.

Mostly experimental, may not always build, and may have bugs.
Rather than importing as a dependency, the better choice might be to copy-and-paste
into your project. Apache-licensed. Supports[Dotty / Scala 3](https://dotty.epfl.ch/).

## Organization

- _core_ includes general utilities and a Logback duplicate message filter. It's a dependency for the other subprojects.
- _logconfig_ contains a [Logback](http://logback.qos.ch/) configuration that filters duplicate messages.
- _misc_ contains miscellaneous code, especially code with bulky dependencies.
- _grammars_ contains formally specified grammars and their parsers.
- _video_ contains code to read video files as streams of frames, and calculate things.
