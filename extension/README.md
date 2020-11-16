# A VS Code extension for C4 DSL Models

This is a VS Code extension for specifying software architecture models with the [Structurizr DSL](https://github.com/structurizr/dsl).

Structurizr DSL, known as [diagram as text](https://structurizr.com/help/text), is the textual representation of the [C4 model](https://c4model.com/).

This extension is backed by an [Xtext](https://www.eclipse.org/Xtext/) grammar, which is used to represent the [Structurizr DSL language reference](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md) in a formal way.

A generator creates PlantUML diagrams on-the-fly, while editing. The diagrams are located in a sub-folder named *plantuml-gen*. The [PlantUML extension](https://marketplace.visualstudio.com/items?itemName=jebbs.plantuml) will be installed automatically as dependency, if not yet already installed.

Beside that it provides all typical language editor features like:

* syntax highlighting
* syntax validation
* code completion
* outline
* code lenses
* folding
* re-factoring
* hover
* semantic validation

![c4 dsl](https://gitlab.com/systemticks/c4-grammar/-/raw/17706e9b41936def3e1a27f8289f6e138ab92707/extension/images/c4dsl-screenshot-1.png)

## Pre-requisites

Xtext requires a [Java VM](http://java.com/en/download/) for running the language server.

> :warning: There seems to be runtime issues with Java 11 or higher. Please use Java 8 instead.

The PlantUML extension requires [Graphviz](http://www.graphviz.org/download/) for layouting the diagrams. See the section [requirements](https://marketplace.visualstudio.com/items?itemName=jebbs.plantuml) for more information, how to install it for your OS.

Your models must have the file extension ***.c4**.

## Structurizr DSL 

### Divergences

The goal is, that the Xtext grammar (used in this extension) is compliant to the origin language reference.

However the Xtext grammar is a bit more strict in some points:

* Double quote characters ("...") are **mandatory**, even when a property does not contain whitespaces
* You can't use keywords like **person** or **container** as variable names

### Unsupported DSL Elements

As of now following DSL elements from the language reference are not yet supported:

* **url** and **properties**
* **!include** for importing re-usable model fragments
* **!adr** for refering to architecture decision records
* **!docs** for refering to additional markdown/asciidoc documentation
* **filtered** diagrams
* **branding**
* **configuration**
* **impliedRelationShips**
* **persepctives** 

Those elements will be provided in subsequent releases.