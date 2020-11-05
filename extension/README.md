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

## Pre-requisites

Xtext requires a Java VM for running the language server.

Your models must have the file extension *.c4.

## Structurizr DSL 

### Divergences

The goal is, that the Xtext grammar (used in this extension) is compliant to the origin language reference.

However the Xtext grammar is a bit more strict in some points:

* Double quote characters ("...") are **mandatory**, even when a property does not contains whitespaces
* You can't use keywords like **person** or **container** as variable names

### Unsupported DSL Elements

As of now following DSL elements from the language reference are not yet supported:

* **url** and **properties**
* **!include** for importing re-usable model fragments
* **filtered** diagrams
* **dynamic** diagrams
* **branding**
* **configuration** 

Those elements will be provided in subsequent releases.