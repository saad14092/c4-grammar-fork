# A VS Code extension for C4 DSL Models

This is a VS Code extension for specifying software architecture models with the [Structurizr DSL](https://github.com/structurizr/dsl).

Structurizr DSL, known as [diagram as text](https://structurizr.com/help/text), is the textual representation of the [C4 model](https://c4model.com/).

This extension is backed by an [Xtext](https://www.eclipse.org/Xtext/) grammar, which is used to represent the [Structurizr DSL language reference](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md) in a formal way.

A generator creates PlantUML diagrams on-the-fly, while editing. The diagrams are located in a sub-folder named *plantuml-gen*. 

Every C4 view in the editor will be enriched with a code lense (Show as Plant UML). When clicking, the corresponding plantuml file will be converted into a svg file, utilizing the free service from [kroki.io](https://kroki.io). 
The graph is then displayed in a separate window.

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

## Semantic Highlighting

When describing C4 models textually, there are plenty of raw strings describing different aspects and meta data of a model element. They are visually difficult to be distinguished from each other.

Therefore a semantic highlighting options is provided, in order to highlight the different aspects in different colors (see screenshot below).

You can switch it off (configuration property 'c4.language.SemanticHighlighting') in case it is too dazzling... It falls back to raw syntax coloring then.

![c4 dsl](https://gitlab.com/systemticks/c4-grammar/-/raw/master/extension/images/c4dsl-semantic-highlighting.png)

## PlantUML Render Options

Supports the different Plant UML render options provided by the origin structurizr-plantuml project.
You can change the renderer by setting the corresponding configuration: **c4.plantuml.generator**

Explanation is taken from the origin README:

```
There are three PlantUML writer implementations:

- `StructurizrPlantUMLWriter`: most closely resembles the diagram notation used on the [C4 model website](https://c4model.com), and the [Structurizr](https://structurizr.com) web-based renderer.
- `PlantUMLWriter`: default PlantUML styling (with UML stereotypes).
- `C4PlantUMLWriter`: produces diagram definitions that use the [C4-PlantUML macros](https://github.com/plantuml-stdlib/C4-PlantUML).
```

## Pre-requisites

Xtext requires a [Java VM](http://java.com/en/download/) for running the language server.

> :warning: There seems to be runtime issues with Java 11 or higher. Please use Java 8 instead.

> :warning: In some cases (especially reported under MacOS) the language server seems to have missing execution rights. Check <YOUR_HOME>\.vscode\extensions\systemticks.c4-dsl-extension-1.1.0\server\c4-language-server\bin and add +x if missing to make it executable.

You must open a workspace, that contains you models, which need to have the file extension **.dsl** 

Multiple workspaces are supported.

## Examples

The architecture (i.e. its diagrams) of this extension is modeled with - guess what - the c4 dsl itself.
Just open "../workspace/c4-dsl-extension.dsl" in your VS Code.

The workspace folder does also contain some basic examples from the structurizr dsl website.

## Structurizr DSL 

### Divergences

The goal is, that the Xtext grammar (used in this extension) is compliant to the origin language reference.

However the Xtext grammar is a bit more strict in some points:

* Double quote characters ("...") are **mandatory**, even when a property does not contain whitespaces
* You can't use keywords like **person** or **container** as variable names

### Unsupported DSL Elements

As of now following DSL elements from the language reference are not yet supported:

* **branding**
* **configuration**

Those elements will be provided in subsequent releases.