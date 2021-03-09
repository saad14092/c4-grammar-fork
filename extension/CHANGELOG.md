# Change Log

## v2.0.3

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/44
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/10
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/9
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/8

## v2.0.2

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/43
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/41

## v2.0.1

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/42

## v2.0.0

With version v2.0.0 the Plant UML Diagram is no longer rendered with the Plant UML extension, but with a built-in Webview.
The corresponding SVG is requested by the kroki.io server on-demand. See https://kroki.io/ for more details.
Nevertheless you can still use the Plant UML extension, when opening a (generated) *.puml file.

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/24

## v1.1.9

* Supports language feature **group**
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/38
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/37

## v1.1.8

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/31

## v1.1.7
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/33
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/32
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/22
* Provide C4 DSL model for this extension itself as an example (workspace/c4-dsl-extension.dsl)

## v1.1.6

* Enables semantic highlighting
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/28

## v1.1.5

* Supports language features **url**, **properties** and **perspectives**
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/26

## v1.1.4

* Provide support for _filtered_ views. There is currently no PlantUML writer available in the structuritr plantuml project. Closes https://gitlab.com/systemticks/c4-grammar/-/issues/4
* Fixed Bug in case of empty dsl file: Closes https://gitlab.com/systemticks/c4-grammar/-/issues/25

## v1.1.3

* Provide code templates (code snippets) for creating new elements Closes https://gitlab.com/systemticks/c4-grammar/-/issues/21
* Name of environment in deployment view is not checked against availabe deployment environments Closes https://gitlab.com/systemticks/c4-grammar/-/issues/23

## v1.1.2

* Plant UML is not rendered in case the view key contains a whitespace. Closes https://gitlab.com/systemticks/c4-grammar/-/issues/20

## v1.1.1

* Provide capability to include/exclude relationships. Closes https://gitlab.com/systemticks/c4-grammar/-/issues/18
* Validate allowed RelationShips between Model Elements. Closes https://gitlab.com/systemticks/c4-grammar/-/issues/17

## v1.1.0

* The file extension is now *.dsl instead of *.c4. This is to be compliant with the origin language reference and to allow e.g. imports. This means an incompatible change to earlier releases! Closes https://gitlab.com/systemticks/c4-grammar/-/issues/16
* It is now possible to import other *.dsl files. As of now it is restricted within the model section. Closes https://gitlab.com/systemticks/c4-grammar/-/issues/6

## v1.0.3

* Fixed : https://gitlab.com/systemticks/c4-grammar/-/issues/13
* Fixed : https://gitlab.com/systemticks/c4-grammar/-/issues/12

## v1.0.2

* Supporting dynamic views

## v1.0.1

* Fixed : View keys should be optional (auto-generated)
* Fixed : Workspace name should be optional

## v1.0.0

* Initial commit