// Copyright (c) 2020 systemticks GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import {
  ExtensionContext,
  workspace,
  commands,
  window,
  StatusBarAlignment,
  Uri,
  TextDocument,
} from "vscode";
import * as path from "path";
import * as cp from "child_process";
import * as readline from "node:readline";
import * as findJavaHome from "find-java-home";

import {
  LanguageClientOptions,
  StateChangeEvent,
  State,
} from "vscode-languageclient";
import { LanguageClient } from "vscode-languageclient/node";
import {
  CommandResultCode,
  ConfigurationOptions,
  PlantUmlExportOptions,
  TextDocumentChangeConfig,
} from "./types";
import { DecorationService, PreviewService } from "./services";
import { C4Utils, substituteVariable } from "./utils";

const CONF_PLANTUML_GENERATOR = "c4.export.plantuml.generator";
const CONF_PLANTUML_EXPORT_DIR = "c4.export.plantuml.dir";
const CONF_LANGUAGESERVER_CONNECTIONTYPE = "c4.languageserver.connectiontype";
const CONF_DIAGRAM_STRUCTURIZR_ENABLED = "c4.diagram.structurizr.enabled";
const CONF_DIAGRAM_STRUCTURIZR_RENDER_URL = "c4.structurizr.render.url";
const CONF_DIAGRAM_STRUCTURIZR_RENDER_STATIC_URL = "c4.structurizr.render.staticurl";
const CONF_DIAGRAM_PLANTUML_ENABLED = "c4.diagram.plantuml.enabled";
const CONF_DIAGRAM_MERMAID_ENABLED = "c4.diagram.mermaid.enabled";
const CONF_PLANTUML_SERVER = "c4.show.plantuml.server";
const CONF_INLINE_RENDERER = "c4.diagram.renderer";
const CONF_TEXT_DECORATIONS = "c4.decorations.enabled";
const CONF_LANGUAGESERVER_LOGS_ENABLED = "c4.languageserver.logs.enabled";
const CONF_AUTO_FORMAT_INDENT = "c4.editor.autoformat.indent";
const CONF_LANGUAGESERVER_JAVA = "c4.languageserver.java";

var proc: cp.ChildProcess;

function getJavaPath() {
  let javaPath = workspace
    .getConfiguration()
    .get(CONF_LANGUAGESERVER_JAVA) as string;

  // Check if a path is provided, if not, use JAVA_HOME or find a default
  if (!javaPath || javaPath.trim() === "") {
    javaPath = process.env.JAVA_HOME ?? "";
    if (!javaPath) {
      findJavaHome((err, home) => {
        if (err) {
          console.error("Java home not found automatically.");
          return;
        }
        javaPath = home;
      });
      console.log(
        "No custom JDK path set. Attempting to use JAVA_HOME or system default."
      );
    }
  } else if (javaPath.includes("${")) {
    javaPath = parseDir(javaPath);
  }

  return javaPath;
}

export function activate(context: ExtensionContext) {
  const javaPath = getJavaPath();
  const javaBinPath = path.join(javaPath, "bin");
  const env = {
    ...process.env,
    PATH: `${javaBinPath}${path.delimiter}${process.env.PATH}`,
  };

  cp.exec("java -version", { env }, (err, stdOut, stdErr) => {
    if (
      err?.message.includes("'java' is not recognized") ||
      err?.message.includes("'java' not found")
    ) {
      window.showErrorMessage(
        "Java is needed to run the Language server. Please install java"
      );
    } else if (C4Utils.getJavaVersion(stdErr) < 17) {
      window.showErrorMessage(
        "Java 17 or higher is needed to run the Language server. Please upgrade your java version"
      );
    } else {
      initExtension(context);
    }
  });
}

function initExtension(context: ExtensionContext) {
  const executable =
    process.platform === "win32"
      ? "c4-language-server.bat"
      : "c4-language-server";
  const languageServerPath = path.join(
    "server",
    "c4-language-server",
    "bin",
    executable
  );
  const serverLauncher = context.asAbsolutePath(languageServerPath);

  // Defined here and not in the decoration service, as the decorations were being appended multiple times
  const decType = window.createTextEditorDecorationType({});

  const logger = window.createOutputChannel("C4 DSL Extension");
  logger.appendLine("Initializing");

  const clientOptions: LanguageClientOptions = {
    documentSelector: [{ scheme: "file", language: "c4" }],
    outputChannel: logger,
    synchronize: {
      fileEvents: workspace.createFileSystemWatcher("**/*.dsl"),
    },
  };
  const connectionType = workspace
    .getConfiguration()
    .get(CONF_LANGUAGESERVER_CONNECTIONTYPE) as string;
  const renderer = workspace
    .getConfiguration()
    .get(CONF_INLINE_RENDERER) as string;
  const textDecorations = workspace
    .getConfiguration()
    .get(CONF_TEXT_DECORATIONS) as TextDocumentChangeConfig;

  const languageClient = new LanguageClient(
    "c4LanguageClient",
    "C4 Language Server",
    C4Utils.getServerOptions(connectionType, serverLauncher, renderer),
    clientOptions
  );

  const statusBarItem = window.createStatusBarItem(
    StatusBarAlignment.Right,
    100
  );
  statusBarItem.show();
  context.subscriptions.push(statusBarItem);
  languageClient.onDidChangeState((e: StateChangeEvent) => {
    switch (e.newState) {
      case State.Starting:
        statusBarItem.text = "C4 DSL Language Server is starting up...";
        statusBarItem.color = "white";
        break;
      case State.Running:
        statusBarItem.text = "C4 DSL Language Server is ready";
        statusBarItem.color = "white";
        updateServerConfigurationIndent();
        break;
      case State.Stopped:
        statusBarItem.text = "C4 Language Server has stopped";
        statusBarItem.color = "red";
        break;
    }
  });

  if (
    connectionType === "socket" ||
    (connectionType === "auto" && process.platform !== "win32")
  ) {
    const READY_ECHO = "READY_TO_CONNECT";

    statusBarItem.text = "C4 DSL Socket Server is starting up...";
    statusBarItem.color = "white";

    const args = ["-c=socket", "-e=" + READY_ECHO, "-ir=" + renderer];

    const serverLogsEnabled = workspace
      .getConfiguration()
      .get(CONF_LANGUAGESERVER_LOGS_ENABLED) as boolean;
    if (serverLogsEnabled && workspace.workspaceFolders) {
      const wsFolder = workspace.workspaceFolders[0].uri;
      proc = cp.spawn(serverLauncher, args, { cwd: wsFolder.fsPath, shell: true });
    } else {
      proc = cp.spawn(serverLauncher, args, { shell: true});
    }

    if (proc.stdout) {
      const reader = readline.createInterface({
        input: proc.stdout,
        terminal: false,
      });

      reader.on("line", function (line: string) {
        if (line.endsWith(READY_ECHO)) {
          languageClient.start();
        }
      });
    } else {
      statusBarItem.text =
        "Connection to C4 DSL Socket Server could not be established";
      statusBarItem.color = "red";
    }
  } else {
    languageClient.start();
  }

  const renderService = workspace
      .getConfiguration()
      .get(CONF_DIAGRAM_STRUCTURIZR_RENDER_URL) as string;
  
  const staticResources = workspace
      .getConfiguration()
      .get(CONF_DIAGRAM_STRUCTURIZR_RENDER_STATIC_URL) as string;

  const structurizrPreviewService = new PreviewService(
    renderService,
    "Structurizr Preview",
    "Structurizr Preview",
    staticResources
  );
  commands.registerCommand("c4.show.diagram", async (...args: string[]) => {
    const diagramEnabled = workspace
      .getConfiguration()
      .get(CONF_DIAGRAM_STRUCTURIZR_ENABLED) as boolean;

    if (!diagramEnabled) {
      window.showInformationMessage(
        "You have to set the config item 'c4.diagram.structurizr.enabled' to true, if you want to use the public structurizr renderer"
      );
    } else {
      const encodedWorkspaceJson = args[0];
      const diagramKey = args[1];
      structurizrPreviewService.currentDiagram = diagramKey;
      structurizrPreviewService.currentDocument = window.activeTextEditor
        ?.document as TextDocument;

      try {
        await structurizrPreviewService.updateWebView(encodedWorkspaceJson);
      } catch (err) {
        logger.appendLine("Error displaying preview: " + JSON.stringify(err));
      }
    }
  });

  const plantumlRendererUri = workspace
    .getConfiguration()
    .get(CONF_PLANTUML_SERVER) as string;
  const plantumlPreviewService = new PreviewService(
    plantumlRendererUri.concat("/plantuml/svg/"),
    "UML",
    "PlantUML Preview"
  );
  commands.registerCommand("c4.show.plantuml", async (...args: string[]) => {
    const diagramEnabled = workspace
      .getConfiguration()
      .get(CONF_DIAGRAM_PLANTUML_ENABLED) as boolean;

    if (!diagramEnabled) {
      window.showInformationMessage(
        "You have to set the config item 'c4.diagram.plantuml.enabled' to true, if you want to use the public kroki rendering service"
      );
    } else {
      const encodedPlantUML = args[0];
      const diagramKey = args[1];
      plantumlPreviewService.currentDiagram = diagramKey;
      plantumlPreviewService.currentDocument = window.activeTextEditor
        ?.document as TextDocument;

      try {
        await plantumlPreviewService.updateWebView(encodedPlantUML);
      } catch (err) {
        logger.appendLine("Error displaying preview: " + JSON.stringify(err));
      }
    }
  });

  const mermaidPreviewService = new PreviewService(
    "https://mermaid.ink/svg/",
    "UML",
    "Mermaid Preview"
  );
  commands.registerCommand("c4.show.mermaid", async (...args: string[]) => {
    const diagramEnabled = workspace
      .getConfiguration()
      .get(CONF_DIAGRAM_MERMAID_ENABLED) as boolean;

    if (!diagramEnabled) {
      window.showInformationMessage(
        "You have to set the config item 'c4.diagram.mermaid.enabled' to true, if you want to use the public mermaid rendering service"
      );
    } else {
      const encodedMermaid = args[0];
      const diagramKey = args[1];
      mermaidPreviewService.currentDiagram = diagramKey;
      mermaidPreviewService.currentDocument = window.activeTextEditor
        ?.document as TextDocument;

      try {
        await mermaidPreviewService.updateWebView(encodedMermaid);
      } catch (err) {
        logger.appendLine("Error displaying preview: " + JSON.stringify(err));
      }
    }
  });

  commands.registerCommand("c4.export.puml", (uri: Uri) => {
    const renderer = workspace
      .getConfiguration()
      .get(CONF_PLANTUML_GENERATOR) as string;
    const exportDir = getExportDir();

    const exportOptions: PlantUmlExportOptions = {
      uri: uri.path,
      renderer: renderer,
      outDir: exportDir,
    };

    commands
      .executeCommand("c4-server.export.puml", exportOptions)
      .then((callback) => {
        const result = callback as CommandResultCode;
        if (result.resultcode == 100) {
          window.showInformationMessage(result.message);
        } else {
          window.showErrorMessage(
            "(Code:" + result.resultcode + ") " + result.message
          );
        }
      });
  });

  if (textDecorations !== "off") {
    const decorationService = new DecorationService(decType);

    if (textDecorations === "onSave") {
      workspace.onDidSaveTextDocument((savedDocument) => {
        decorationService.triggerDecorations(undefined, savedDocument);
      });
    } else if (textDecorations === "onChange") {
      workspace.onDidChangeTextDocument((changed) => {
        decorationService.triggerDecorations(undefined, changed.document);
      });
    }

    window.onDidChangeActiveTextEditor((editor) => {
      decorationService.triggerDecorations(editor, undefined);
    });

    decorationService.triggerDecorations(window.activeTextEditor, undefined);
  }

  workspace.onDidSaveTextDocument((document: TextDocument) => {
    switch (renderer) {
      case "structurizr":
        structurizrPreviewService.triggerRefresh(document, renderer);
        break;
      case "plantuml":
        plantumlPreviewService.triggerRefresh(document, renderer);
        break;
      case "mermaid":
        mermaidPreviewService.triggerRefresh(document, renderer);
        break;
      default:
        plantumlPreviewService.triggerRefresh(document, renderer);
        break;
    }
  });

  workspace.onDidChangeConfiguration((event) => {
    if (event.affectsConfiguration(CONF_AUTO_FORMAT_INDENT)) {
      updateServerConfigurationIndent();
    }
  });

  logger.appendLine("Initialized");
}

function getExportDir() {
  const exportDir = workspace
    .getConfiguration()
    .get(CONF_PLANTUML_EXPORT_DIR) as string;
  if (exportDir.includes("${")) return parseDir(exportDir);
  return exportDir;
}

function parseDir(dir: string) {
  const separator = substituteVariable("${/}") as string;
  const paths = dir.split(separator);
  const parsedPath: Array<string> = [];
  for (let path of paths) {
    const pathValue = substituteVariable(path);
    if (!pathValue) {
      window.showErrorMessage("Incorrect path");
      return "";
    }
    parsedPath.push(pathValue);
  }
  return parsedPath.join(separator);
}

function updateServerConfigurationIndent() {
  const spaces = workspace
    .getConfiguration()
    .get(CONF_AUTO_FORMAT_INDENT) as number;
  commands.executeCommand("c4-server.autoformat.indent", { indent: spaces });
}

export function updateServerConfiguration() {
  const configOptions: ConfigurationOptions = {
    renderer: workspace.getConfiguration().get(CONF_INLINE_RENDERER) as string,
  };

  commands
    .executeCommand("c4-server.configuration", configOptions)
    .then((callback) => {
      window.showInformationMessage("Configuration Updated");
    });
}

export function deactivate() {
  if (proc) {
    proc.kill("SIGINT");
  }
}
