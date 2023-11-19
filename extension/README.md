# A VS Code extension for C4 DSL Models

## Description

This is a VS Code extension for specifying software architecture models with the [Structurizr DSL](https://github.com/structurizr/dsl).

Structurizr DSL, an example of the [diagram as text](https://structurizr.com/help/text) trend, is the textual representation of the [C4 model](https://c4model.com/).

Technically there is language server built on top of the origin [Structurizr DSL](https://github.com/structurizr/dsl) parser.

## Pre-requisites

A [Java VM](http://java.com/en/download/) is required for running the language server. Java 17 or higher is required.

> :warning: In some cases the language server seems to have missing execution rights. Check `<YOUR_HOME>\.vscode\extensions\systemticks.c4-dsl-extension-<version>\server\c4-language-server\bin` and add +x if missing to make it executable.

You must open a workspace that contains your models, which need to have the file extension `.dsl`

Multiple workspaces are supported.

## Diagram Preview

Every C4 view in the editor will be enriched with a code lense in order to render the corresponding view, either with a kroki.io server (if rendering type is 'plantuml' or with the structurizr.com server (if rendering type is 'structurizr'). In either way your c4 model will be sent as a Base64-encoded string to any of the public webservices for rendering purposes. This feature is deactivated by default, in case of you have concerns making your diagrams public.
You can activate the feature by setting the corresponding configuration property _c4.diagram.structurizr.enabled_ or _c4.diagram.plantuml.enabled_ to true.

> :info: I am working on supporting local plantuml rendering as an alternative solution.

## PlantUML Export

You can trigger the export of all views from one dsl file into plantuml code, either in the context view of the explorer or the editor.
Output folder and Plant UML Renderer are configurable.

## Language Server feaature

- syntax highlighting
- syntax validation
- code completion (partially available, work in progress)
- code lenses
- folding
- re-factoring
- formatting (not yet available, but planned)
- hover (not yet available, but planned)
- goto defintion
- show all references (not yet available, but planned)
- text decorations

![c4 dsl](https://gitlab.com/systemticks/c4-grammar/-/raw/master/extension/images/c4dsl-screenshot-1.png)

## Text Decorations

When describing C4 models textually, there are plenty of raw strings describing different aspects and meta data of a model element. They are visually difficult to distinguish from each other.

Therefore a text decoration option is provided, in order to highlight the different aspects with inline labels, such as `name: ` or `description: ` (see screenshot below).

This feature can become pretty expensive in case of large models. You can switch it off with setting 'c4.decorations.enabled' to 'off'.

![c4 dsl](https://gitlab.com/systemticks/c4-grammar/-/raw/master/extension/images/c4dsl-text-decoration.png)

## Configuration

| Name                             | Values                                                                                           | Default                   | Description                                                                                                                                                                                                            |
| -------------------------------- | ------------------------------------------------------------------------------------------------ | ------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| c4.export.plantuml.generator     | <ul><li>StructurizrPlantUMLWriter</li><li>C4PlantUMLWriter</li><li>BasicPlantUMLWriter</li></ul> | StructurizrPlantUMLWriter | The flavor of the generated Plant UML                                                                                                                                                                                  |
| c4.diagram.renderer              | <ul><li>plantuml</li><li>structurizr</li><li>mermaid</li></ul>                                   | plantuml                  | Detmerines which inline renderer (at the code lenses) is used for displaying views                                                                                                                                     |
| c4.export.plantuml.dir           | <i>PLANTUML_EXPORT_FOLDER</i>                                                                    | ./export                  | The folder for the exported plantuml files. Can be relative or absolute                                                                                                                                                |
| c4.show.plantuml.server          | <i>KROKI_SERVER_URI</i>                                                                          | https://kroki.io          | The server where the kroki diagram rendering service is hosted. Is used when 'plantuml' is selected as renderer (see c4.diagram.renderer)                                                                              |
| c4.languageserver.connectiontype | <ul><li>auto</li><li>process-io</li><li>socket</li></ul>                                         | auto                      | Determines how language client and language server are connected                                                                                                                                                       |
| c4.languageserver.logs.enabled   | true/false                                                                                       | false                     | If enabled language server logs are written to the current workspace folder (c4-language-server.log).                                                                                                                  |
| c4.diagram.structurizr.enabled   | true/false                                                                                       | false                     | If enabled you agree that the workspace of your c4 model will be sent as a Bae64 encoded string to https://structurizr.com for rendering purposes. Do not enable, if you have concerns                                 |
| c4.diagram.plantuml.enabled      | true/false                                                                                       | false                     | If enabled you agree that the view of your c4 model will be sent as a Base64 encoded PlantUML string to the server specified in 'c4.show.plantuml.server' for rendering purposes. Do not enable, if you have concerns. |
| c4.diagram.mermaid.enabled       | true/false                                                                                       | false                     | If enabled you agree that the view of your c4 model will be sent as a Base64 encoded PlantUML string to https://mermaid.ink for rendering purposes. Do not enable, if you have concerns.                               |
| c4.decorations.enabled           | <ul><li>off</li><li>onChange</li><li>onSave</li></ul>                                            | onChange                  | Text decorations can take place when editing (onChange) or when file is saved (onSave). It can also be switched off.                                                                                                   |

## Examples

The architecture (i.e. its diagrams) of this extension is modeled with - guess what - the C4 DSL itself.
Just open `../workspace/c4-dsl-extension.dsl` in your VS Code.

The workspace folder also contains some basic examples from the structurizr dsl website.

## Known Issues

The !include is not yet fully supported, i.e. it might not work correctly, in case the entire model is not self-contained in one file.

**Issues**

Feel free to submit an issue in case you recognize a shortcoming or if you want to request a feature.
Please attach the corresponding dsl file or its content to the issue, so that the problem can be reproduced.
