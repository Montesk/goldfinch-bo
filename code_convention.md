### Imports

#### Priority

1. Java system wide
2. Scala system wide
3. Framework Dependencies
4. Company library dependencies
5. Project dependencies

#### Generic naming convention

1. Nested case classes public getters must apply to pattern "group" -> "property"

> e.g App(Theme: String = "green") <- App theme getter must be named `def appTheme()`