# A VS Code extension for C4 DSL Models

**CAUTION**

With version > v3.0.0 this extension is under heavy re-construction.
Use on your own risk.

The Xtext Grammar has been removed and thus the language server needs to be reimplemented from scratch. 
Be aware that many features are not yet (fully) available.

Currently it is not running under linux, see (https://gitlab.com/systemticks/c4-grammar/-/issues/81)

The !include is not yet supported, i.e. it only works when the entire model is self-contained in one file.

**DESCRIPTION**

This is a VS Code extension for specifying software architecture models with the [Structurizr DSL](https://github.com/structurizr/dsl).

Structurizr DSL, an example of the [diagram as text](https://structurizr.com/help/text) trend, is the textual representation of the [C4 model](https://c4model.com/).

Every C4 view in the editor will be enriched with a code lense (Show as Structurizr Diagram). When clicking, the corresponding view will be rendered with the origin Structurizr Viewer.

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

![c4 dsl](https://gitlab.com/systemticks/c4-grammar/-/raw/master/extension/images/c4dsl-semantic-highlighting.png)

## Pre-requisites

A [Java VM](http://java.com/en/download/) is required for running the language server. Java 8 or higher is required.

> :warning: In some cases the language server seems to have missing execution rights. Check `<YOUR_HOME>\.vscode\extensions\systemticks.c4-dsl-extension-1.1.0\server\c4-language-server\bin` and add +x if missing to make it executable.

You must open a workspace that contains your models, which need to have the file extension `.dsl` 

Multiple workspaces are supported.

## Examples

The architecture (i.e. its diagrams) of this extension is modeled with - guess what - the C4 DSL itself.
Just open `../workspace/c4-dsl-extension.dsl` in your VS Code.

The workspace folder also contains some basic examples from the structurizr dsl website.
