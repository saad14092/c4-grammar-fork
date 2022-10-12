# A VS Code extension for C4 DSL Models

**CAUTION**

With version > v3.0.0 this extension is under heavy re-construction.
Use on your own risk.

The Xtext Grammar has been removed and thus the language server needs to be reimplemented from scratch. 
Be aware that many features are not yet (fully) available.

The !include is not yet fully supported, i.e. it might not work correctly, in case the entire model is not self-contained in one file.

**DESCRIPTION**

This is a VS Code extension for specifying software architecture models with the [Structurizr DSL](https://github.com/structurizr/dsl).

Structurizr DSL, an example of the [diagram as text](https://structurizr.com/help/text) trend, is the textual representation of the [C4 model](https://c4model.com/).

Technically there is language server built on top of the origin [Structurizr DSL](https://github.com/structurizr/dsl) parser.

## Diagram Preview

Every C4 view in the editor will be enriched with a code lense in order to render the corresponding view, either with a kroki.io server (if rendering type is 'plantuml' or with the structurizr.com  server (if rendering type is 'structurizr'). In either way your c4 model will be sent as a Base64-encoded string to any of the public webservices for rendering purposes. This feature is deactivated by default, in case of you have concerns making your diagrams public.
You can activate the feature by setting the corresponding configuration property *c4.diagram.structurizr.enabled* or *c4.diagram.plantuml.enabled* to true.

> :info: I am working on supporting local plantuml rendering as an alternative solution.

## PlantUML Export

You can trigger the export of all views from one dsl file into plantuml code, either in the context view of the explorer or the editor.
Output folder and Plant UML Renderer are configurable.

## Language Server feaature

* syntax highlighting (in progress)
* syntax validation
* code completion (not yet available, but planned)
* code lenses
* folding
* re-factoring
* formatting (not yet available, but planned)
* hover (not yet available, but planned)
* goto defintion (in progress)
* show all references (not yet available, but planned)
* semantic validation

![c4 dsl](https://gitlab.com/systemticks/c4-grammar/-/raw/master/extension/images/c4dsl-screenshot-1.png)

## Semantic Highlighting

When describing C4 models textually, there are plenty of raw strings describing different aspects and meta data of a model element. They are visually difficult to distinguish from each other.

Therefore a semantic highlighting option is provided, in order to highlight the different aspects in different colors (see screenshot below).

![c4 dsl](https://gitlab.com/systemticks/c4-grammar/-/raw/master/extension/images/c4dsl-semantic-highlighting.png)

## Configuration

|Name | Values | Default  | Description|
--- | --- | --- | ---
|c4.export.plantuml.generator|<ul><li>StructurizrPlantUMLWriter</li><li>C4PlantUMLWriter</li><li>BasicPlantUMLWriter</li></ul>|StructurizrPlantUMLWriter|The flavor of the generated Plant UML
|c4.diagram.renderer|<ul><li>plantuml</li><li>structurizr</li></ul>|plantuml|Detmerines which inline renderer (at the code lenses) is used for displaying views
|c4.export.plantuml.dir|<i>PLANTUML_EXPORT_FOLDER</i>|./export|The folder for the exported plantuml files. Can be relative or absolute
|c4.show.plantuml.server|<i>KROKI_SERVER_URI</i>|https://kroki.io|The server where the kroki diagram rendering service is hosted. Is used when 'plantuml' is selected as renderer (see c4.diagram.renderer)
|c4.languageserver.connectiontype|<ul><li>auto</li><li>process-io</li><li>socket</li></ul>|auto|Determines how language client and language server are connected
|c4.diagram.structurizr.enabled|true/false|false|If enabled you agree that the workspace of your c4 model will be sent as a Bae64 encoded string to https://structurizr.com for rendering purposes. Do not enable, if you have concerns
|c4.diagram.structurizr.uri|STRUCTURIZR_URI|https://structurizr.com/json|The URL to call when rendering the diagram using a Structurizr server.  Override if you want to point to a on-premise installation rather than the main Structurizr site, for example.
|c4.diagram.plantuml.enabled|true/false|false|If enabled you agree that the view of your c4 model will be sent as a Base64 encoded PlantUML string to the server specified in 'c4.show.plantuml.server' for rendering purposes. Do not enable, if you have concerns.
|c4.decorations.enabled|true/false|true|Switch inline text decorations on or off.

## Pre-requisites

A [Java VM](http://java.com/en/download/) is required for running the language server. Java 8 or higher is required.

> :warning: In some cases the language server seems to have missing execution rights. Check `<YOUR_HOME>\.vscode\extensions\systemticks.c4-dsl-extension-1.1.0\server\c4-language-server\bin` and add +x if missing to make it executable.

You must open a workspace that contains your models, which need to have the file extension `.dsl` 

Multiple workspaces are supported.

## Examples

The architecture (i.e. its diagrams) of this extension is modeled with - guess what - the C4 DSL itself.
Just open `../workspace/c4-dsl-extension.dsl` in your VS Code.

The workspace folder also contains some basic examples from the structurizr dsl website.

**Issues**

Feel free to submit an issue in case you recognize a shortcoming or if you want to request a feature.
Please attach the corresponding dsl file or its content to the issue, so that the problem can be reproduced.