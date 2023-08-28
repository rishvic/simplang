# Lab Assignment 5

## How to build

### Prerequisites

- Gradle
- Java 17 or later

### Running the Command

```bash
./gradlew run --args='../samples/sample3.simp'
```

Output:
```
> Task :app:generateGrammarSource UP-TO-DATE
> Task :app:compileJava UP-TO-DATE
> Task :app:processResources NO-SOURCE
> Task :app:classes UP-TO-DATE

> Task :app:run
Initial rules:
A -> a B
   | a B a
   | a B a a
   ;

B -> B a
   | ϵ
   ;

Rules after removing left recursion:
A -> a B
   | a B a
   | a B a a
   ;

B -> B_r
   ;

B_r
  -> a B_r
   | ϵ
   ;

Rules after removing left factoring:
A-a.B
  -> ϵ
   | a A-a.B.a
   ;

A -> a B A-a.B
   ;

B -> B_r
   ;

B_r
  -> ϵ
   | a B_r
   ;

A-a.B.a
  -> ϵ
   | a
   ;


BUILD SUCCESSFUL in 715ms
3 actionable tasks: 1 executed, 2 up-to-date
```
