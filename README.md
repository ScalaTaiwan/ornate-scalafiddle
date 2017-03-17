# ScalaFiddle extension for [Ornate](https://github.com/szeiger/ornate)

Ornate is a tool for building multiple html pages from markdown files.
orndate-scalafiddle is an extension for Ornate that makes including ScalaFiddle code snippets in the html much easier.

This project is both an Ornate extension and a sample Ornate project.  All of the *.md files goes in the src/site folder.
To use the ScalaFiddle extension, put the following at the top of *.md files:
```
---
extensions = [
  scalaFiddle
  includeCode
  highlightjs
]
---
```
All of the configs follow the [HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md) convention.

And in the code section, specify it's a ScalaFiddle code snippet by putting *scalaFiddle* after the backticks.
*scalaFiddle* takes optional attributes *name*, *description*, and *libraries* in this form (see src/site/test.md for detail):
```
<3 backticks>scalaFiddle name="simple" description="a simple test" libraries="Java8 Time-0.1.0"
import java.time._
println(LocalTime.now)
<3 backticks>
```
Execute *ornate* in sbt console to generate the html files.  They are in target/site by default.

## Default page config
Default attributes(*name*, *description*, *libraries*) per page can be defined on top of the md file as well.
```
---
...
extension.scalaFiddle {
  name = "default name"
  description = "default description"
  libraries = [
    Cats-0.7.2
  ]
}
---
```
Both *name* and *description* attributes specify the default values when they are missing in the ScalaFiddle code snippets.
*libraries* attribute, however, is concatenated with the *libraries* attribute in each ScalaFiddle code snippet.