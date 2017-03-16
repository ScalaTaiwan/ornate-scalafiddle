---
extensions = [
  scalaFiddle
  includeCode
  highlightjs
]
---
1. hello
2. world

```scalaFiddle
println("hello")
```
another one
```scalaFiddle name="simple" description="simple test" libraries="Cats-0.7.2,Shapeless-2.2.5"
import cats._
import cats.implicits._
import shapeless._
println(1 |+| 2)
println(1 :: "hello" :: false :: HNil)

```