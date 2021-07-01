# A VS Code extension for C4 DSL Models

This is a VS Code extension for specifying software architecture models with the [Structurizr DSL](https://github.com/structurizr/dsl).

Structurizr DSL, an example of the [diagram as text](https://structurizr.com/help/text) trend, is the textual representation of the [C4 model](https://c4model.com/).

This extension is backed by an [Xtext](https://www.eclipse.org/Xtext/) grammar, which is used to represent the [Structurizr DSL language reference](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md) in a formal way.

A generator creates PlantUML diagrams on-the-fly, while editing. The diagrams are located under `$USERHOME/.c4dslextension/$WORKSPACEFOLDER`

Every C4 view in the editor will be enriched with a code lense (Show as Plant UML). When clicking, the corresponding PlantUML file will be converted into a SVG file, utilizing the free service from [kroki.io](https://kroki.io). 
The graph is then displayed in a separate window.

Besides that, it provides all typical language editor features like:

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

When describing C4 models textually, there are plenty of raw strings describing different aspects and meta data of a model element. They are visually difficult to distinguish from each other.

Therefore a semantic highlighting option is provided, in order to highlight the different aspects in different colors (see screenshot below).

You can switch it off (configuration property `c4.language.SemanticHighlighting`) in case it is too dazzling... It falls back to raw syntax coloring then.

![c4 dsl](https://gitlab.com/systemticks/c4-grammar/-/raw/master/extension/images/c4dsl-semantic-highlighting.png)

## PlantUML Render Options

Supports different PlantUML render options.
You can change the renderer by setting the corresponding configuration: **c4.plantuml.generator**

Default is **StructurizrOrigin** which is integrating the origin PlantUML preview from the structurizr service. 

> :warning: **StructurizrOrigin** seems to have problems with rendering DSL files that contain `!includes`.

As an alternative you can use a custom renderer (rendering the graph), implemented by this extension.
But again the generators (dsl -> puml) are using the origin structurizr libraries:

There are three PlantUML writer implementations:

- `StructurizrPlantUMLWriter`: most closely resembles the diagram notation used on the [C4 model website](https://c4model.com), and the [Structurizr](https://structurizr.com) web-based renderer.
- `PlantUMLWriter`: default PlantUML styling (with UML stereotypes).
- `C4PlantUMLWriter`: produces diagram definitions that use the [C4-PlantUML macros](https://github.com/plantuml-stdlib/C4-PlantUML).
- `MermaidWriter`: produces mermaid files, and renders locally.

## Pre-requisites

Xtext requires a [Java VM](http://java.com/en/download/) for running the language server.

> :warning: In some cases the language server seems to have missing execution rights. Check `<YOUR_HOME>\.vscode\extensions\systemticks.c4-dsl-extension-1.1.0\server\c4-language-server\bin` and add +x if missing to make it executable.

You must open a workspace that contains your models, which need to have the file extension `.dsl` 

Multiple workspaces are supported.

## Examples

The architecture (i.e. its diagrams) of this extension is modeled with - guess what - the C4 DSL itself.
Just open `../workspace/c4-dsl-extension.dsl` in your VS Code.

The workspace folder also contains some basic examples from the structurizr dsl website.

## Structurizr DSL 

### Divergences

The goal is that the Xtext grammar (used in this extension) is compliant to the origin language reference.

However the Xtext grammar is a bit more strict in some points:

* Double quote characters ("...") are **mandatory**, even when a property does not contain whitespace
* You can't use keywords like **person** or **container** as variable names

**About `!includes`**

The *`!include`* feature is not fully supported. This is due to the fact that the `!include` mechanism is quite different to standard Xtext import functionality. In the strucutizr dsl the content of any included files is simply inlined into the parent document, i.e. you can use it almost everywhere. In Xtext an import has the flavor of an _import_ like in Java.

Currently `!includes` are only allowed in the context of
* **model**
* **softwaresystem**
* **styles**

Open the *big_bank_plc* folder as a workspace to see what is possible.

Be aware, that the current implementation of this feature is limited and probably unstable.

### Unsupported DSL Elements

As of now, the following DSL elements from the language reference are not yet supported:

* **deploymentGroups**
* **branding**
* **configuration**
* **!identifiers**
* **expressions in include/exclude**
* **default theme**


Those elements will be provided in subsequent releases.
