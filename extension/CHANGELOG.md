# Change Log

## v3.4.2

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/139
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/130

## v3.4.1

First draft version of an auto-formatter (pretty-print)

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/138
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/137

## v3.4.0

Language Server and Client are aligned with the latest LSP specification 3.17

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/136
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/135

## v3.3.2

The language server is synced to structurizr/dsl v1.30.1

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/133

## v3.3.1

The language server is synced to structurizr/dsl v1.30.0

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/131

## v3.3.0

From now on Java 11 or higher is required<br>
The language server is synced to structurizr/dsl v1.28.0

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/129
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/128
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/126
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/124
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/123
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/122

## v3.2.3

Supports mermaid as inline renderer.

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/121
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/120

## v3.2.2

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/119
* Closes https://gitlab.com/systemticks/c4-grammar/-/merge_requests/76

## v3.2.1

Provides basic support for code completion / content assist

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/116

## v3.2.0

Provides text decoration for inline parameter, e.g.:

atm = softwaresystem `name:` "ATM" `description:` "Allows customers to withdraw cash." `tags:` "Existing System"

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/90

## v3.1.8

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/111

## v3.1.7

Improvements regarding !include feature

## v3.1.6

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/99
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/98

## v3.1.5

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/97

## v3.1.4

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/94

## v3.1.3

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/93
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/47

## v3.1.2

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/91

## v3.1.1

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/81

## v3.1.0

Initial support for !include feature

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/88
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/87

## v3.0.3

Provides the capability to export all views inside a dsl file into plantuml code
Output Directory and Plant UML Renderer are configurable.

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/86

## v3.0.2

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/85

## v3.0.1

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/82
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/80

## v3.0.0 

This is the first version of this extension with a refactored language server.

The Xtext Grammar (a formal syntax for the C4 DSL) is no longer maintained and thus its language server implementation has been removed as well.

Instead the origin structurizr/dsl parser is used now and a new language server is implemented (from scratch) on top of it. This comes with some benefits and drawbacks.

__Benefits__

* No additional xtext grammar needs to be maintained in parallel to the structurizr/dsl
* Due to technical reasons it was not possible to parse all c4 dsl files with the xtext based parser (e.g. Xtext does not allow identifiers with the same name as keywords). This will no longer be a problem.

__Drawbacks__

* Xtext comes with an excelleten out-of-the-box language server with default features for code completion, cross-referencing, validation, etc. This needs to be implemented manually now.

Version v3.0.0 has also a constrained feature set:

* Only the origin structurizr renderer is supported
* Semantic highlight is incomplete
* code completion is not yet implemented
* hovering is not yet implemented

What is working already:

* Validation (using the origin structurizr parse exceptions)
* Code Lenses for showing diagrams
* Syntax highlighting (with some minor issues)

## v2.2.5

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/68
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/67

## v2.2.4

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/64
* Supports the generation of mermaid diagrams

## v2.2.3

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/63

## v2.2.2

The PlantUML files (*.puml), which are generated on-the-fly while editing the *.dsl are now located under $USER-HOME/.c4dslextension/$WORKSPACE-FOLDER/
The path will be made configurable via a VS Code extension property in a future release.

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/62
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/61
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/60

## v2.2.1

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/52
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/57
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/58
* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/59

## v2.1.2

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/55

## v2.1.1

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/50

## v2.1.0

With this release the origin structurizr renderer is integrated, which is also the default renderer.
The setting can be changed with the configuration item "c4.plantuml.generator"

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/48

## v2.0.4

* Closes https://gitlab.com/systemticks/c4-grammar/-/issues/46

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