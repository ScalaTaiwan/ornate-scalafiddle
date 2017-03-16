# ScalaFiddle extension for [Ornate](https://github.com/szeiger/ornate)

Ornate is a tool for building multiple html pages from markdown files.
orndate-scalafiddle is an extension for Ornate that makes including ScalaFiddle code snippets in the html much easier.

This project is both an Ornate extension and a sample Ornate project.  All of the *.md files goes in the src/site folder.
To use the ScalaFiddle extension, put the following at the top of *.md files
```
---
extensions = [
  scalaFiddle
  includeCode
  highlightjs
]
---
```
And in the code section, specify it's a ScalaFiddle code snippet by putting "scalaFiddle" after the backticks.
"scalaFiddle" takes optional attributes "name", "description", and "libraries" in this form (see src/site/test.md for detail):
```
<3 backticks>scalaFiddle name="simple" description="a simple loop" libraries="Java8 Time-0.1.0"
```
Execute "orndate" in sbt console to generate the html files.  They are in target/site by default.