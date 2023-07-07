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

import {ExtensionContext, workspace, commands, window, StatusBarAlignment, Uri, TextEditor, TextDocument } from 'vscode'
import * as path from 'path';
import * as net from 'net';
import * as cp from 'child_process'
import * as readline from 'node:readline'

import { LanguageClientOptions, StateChangeEvent, State } from 'vscode-languageclient';
import { LanguageClient, ServerOptions, StreamInfo } from 'vscode-languageclient/node';
import { CommandResultCode, CommandResultTextDecorations, ConfigurationOptions, PlantUmlExportOptions, TextDocumentChangeConfig } from './types';
import { DecorationService, PreviewService } from './services';

const CONF_PLANTUML_GENERATOR = "c4.export.plantuml.generator"
const CONF_PLANTUML_EXPORT_DIR = "c4.export.plantuml.dir"
const CONF_LANGUAGESERVER_CONNECTIONTYPE = "c4.languageserver.connectiontype"
const CONF_DIAGRAM_STRUCTURIZR_ENABLED = "c4.diagram.structurizr.enabled"
const CONF_DIAGRAM_STRUCTURZR_URI = "c4.diagram.structurizr.uri"
const CONF_DIAGRAM_PLANTUML_ENABLED = "c4.diagram.plantuml.enabled"
const CONF_DIAGRAM_MERMAID_ENABLED = "c4.diagram.mermaid.enabled"
const CONF_PLANTUML_SERVER = "c4.show.plantuml.server"
const CONF_INLINE_RENDERER = "c4.diagram.renderer"
const CONF_TEXT_DECORATIONS = "c4.decorations.enabled"
const CONF_LANGUAGESERVER_LOGS_ENABLED = "c4.languageserver.logs.enabled"

const decType = window.createTextEditorDecorationType({});

var proc: cp.ChildProcess

export function activate(context: ExtensionContext) {

    const executable = process.platform === 'win32' ? 'c4-language-server.bat' : 'c4-language-server';
    const languageServerPath =  path.join('server', 'c4-language-server', 'bin', executable);
    const serverLauncher = context.asAbsolutePath(languageServerPath);

    const logger = window.createOutputChannel("C4 DSL Extension");
    logger.appendLine("Initializing");
 
    const clientOptions: LanguageClientOptions = {
        documentSelector: [{ scheme: 'file', language: 'c4' }],
        outputChannel: logger,
        synchronize: {
            fileEvents: workspace.createFileSystemWatcher('**/*.dsl')
        }
    };
    const connectionType = workspace.getConfiguration().get(CONF_LANGUAGESERVER_CONNECTIONTYPE) as string; 
    const renderer = workspace.getConfiguration().get(CONF_INLINE_RENDERER) as string
    const textDecorations = workspace.getConfiguration().get(CONF_TEXT_DECORATIONS) as TextDocumentChangeConfig

    //
    const getServerOptions = function (): ServerOptions {


        if(connectionType === "process-io" || (connectionType === "auto" && process.platform === 'win32')) {
            return {
                run: {            
                    command: serverLauncher,
                    args: ['-ir='+renderer]
                },
                debug: {
                    command: serverLauncher,
                    args: ['-ir='+renderer]
                }    
            }    
        }
        else {
            const serverDebugOptions = () => {
                let socket = net.connect( { port: 5008 });
                let result: StreamInfo = {
                    writer: socket,
                    reader: socket
                };
                return Promise.resolve(result);
            }  
            return serverDebugOptions            
        }
    }
    //

    const languageClient = new LanguageClient('c4LanguageClient', 'C4 Language Server', getServerOptions(), clientOptions);

    const statusBarItem = window.createStatusBarItem(StatusBarAlignment.Right, 100)
    statusBarItem.show()
    context.subscriptions.push(statusBarItem)
    languageClient.onDidChangeState( (e:StateChangeEvent) => {
        switch (e.newState) {
            case State.Starting:
                statusBarItem.text = "C4 DSL Language Server is starting up..."
                statusBarItem.color = 'white'
                break;
            case State.Running:
                statusBarItem.text = "C4 DSL Language Server is ready"
                statusBarItem.color = 'white'
                //updateServerConfiguration()
                break;
            case State.Stopped:
                statusBarItem.text = "C4 Language Server has stopped"
                statusBarItem.color = 'red'
                break;                
        }
    })

    if(connectionType === "socket" || (connectionType === "auto" && process.platform !== 'win32')) {
        
        const READY_ECHO = "READY_TO_CONNECT"

        statusBarItem.text = "C4 DSL Socket Server is starting up..."
        statusBarItem.color = 'white'

        const args = ['-c=socket', '-e='+READY_ECHO, '-ir='+renderer]

        const serverLogsEnabled = workspace.getConfiguration().get(CONF_LANGUAGESERVER_LOGS_ENABLED) as boolean
        if(serverLogsEnabled && workspace.workspaceFolders) {
            const wsFolder = workspace.workspaceFolders[0].uri;
            proc = cp.spawn( serverLauncher, args, { cwd: wsFolder.fsPath })
        }
        else {
            proc = cp.spawn( serverLauncher, args)
        }

        if(proc.stdout) {           
            const reader = readline.createInterface({
                input: proc.stdout,
                terminal: false})

            reader.on('line', function(line: string) {
                if(line.endsWith(READY_ECHO)) {
                    languageClient.start();
                }
            });    
        }
        else {
            statusBarItem.text = "Connection to C4 DSL Socket Server could not be established"
            statusBarItem.color = 'red'    
        }
    }

    else {
        languageClient.start();
    }
    
    const structurizrPreviewService = new PreviewService(workspace.getConfiguration().get(CONF_DIAGRAM_STRUCTURZR_URI) as string, "Structurizr Preview", "Structurizr Preview")
    commands.registerCommand("c4.show.diagram", async(...args: string[]) => {        
        const diagramEnabled = workspace.getConfiguration().get(CONF_DIAGRAM_STRUCTURIZR_ENABLED) as boolean

        if(!diagramEnabled) {
            window.showInformationMessage("You have to set the config item 'c4.diagram.structurizr.enabled' to true, if you want to use the public structurizr renderer");
        }

        else {
            const encodedWorkspaceJson = args[0]
            const diagramKey = args[1]
    
            try {
                await structurizrPreviewService.updateWebView(encodedWorkspaceJson, diagramKey);
            }
            catch (err) {
                logger.appendLine("Error displaying preview: " + JSON.stringify(err))
            }    
        }        
    });

    const plantumlPreviewService = new PreviewService(workspace.getConfiguration().get(CONF_PLANTUML_SERVER) as string, "UML", "PlantUML Preview")
    commands.registerCommand("c4.show.plantuml", async(...args: string[]) => {        

        const diagramEnabled = workspace.getConfiguration().get(CONF_DIAGRAM_PLANTUML_ENABLED) as boolean

        if(!diagramEnabled) {
            window.showInformationMessage("You have to set the config item 'c4.diagram.plantuml.enabled' to true, if you want to use the public kroki rendering service");
        }
        else {
            const encodedPlantUML = args[0]
    
            try {
                await plantumlPreviewService.updateWebView(encodedPlantUML, "/plantuml/svg/");
            }
            catch (err) {
                logger.appendLine("Error displaying preview: " + JSON.stringify(err))
            } 
        }   
    });

    const mermaidPreviewService = new PreviewService("https://mermaid.ink", "UML", "Mermaid Preview")
    commands.registerCommand("c4.show.mermaid", async(...args: string[]) => {        

        const diagramEnabled = workspace.getConfiguration().get(CONF_DIAGRAM_MERMAID_ENABLED) as boolean

        if(!diagramEnabled) {
            window.showInformationMessage("You have to set the config item 'c4.diagram.mermaid.enabled' to true, if you want to use the public mermaid rendering service");
        }
        else {
            const encodedMermaid = args[0]
    
            try {
                await mermaidPreviewService.updateWebView(encodedMermaid, "/svg/");
            }
            catch (err) {
                logger.appendLine("Error displaying preview: " + JSON.stringify(err))
            } 
        }   
    });

    commands.registerCommand("c4.export.puml", ( uri: Uri ) => {

        const renderer = workspace.getConfiguration().get(CONF_PLANTUML_GENERATOR) as string; 
        const exportDir = workspace.getConfiguration().get(CONF_PLANTUML_EXPORT_DIR) as string; 

        const exportOptions: PlantUmlExportOptions = { uri: uri.path, renderer: renderer, outDir: exportDir };

        commands.executeCommand("c4-server.export.puml", exportOptions).then( callback => {
            const result = callback as CommandResultCode
            if(result.resultcode == 100) {
                window.showInformationMessage(result.message);
            }      
            else {
                window.showErrorMessage("(Code:"+result.resultcode+") "+result.message);
            }
        });
    });

    if(textDecorations !== 'off') {

        if(textDecorations === "onSave") {
            workspace.onDidSaveTextDocument( savedDocument => {
                triggerTextDecorations(undefined, savedDocument)
            })    
        }
        else if(textDecorations === "onChange") {
            workspace.onDidChangeTextDocument( changed => {
                triggerTextDecorations(undefined, changed.document)
            })    
        }
    
        window.onDidChangeActiveTextEditor( editor => {
            triggerTextDecorations(editor, undefined)
        });
    
        triggerTextDecorations(window.activeTextEditor, undefined)    
    }

    logger.appendLine("Initialized");
    return languageClient;
}

function triggerTextDecorations(editor: TextEditor | undefined, document: TextDocument | undefined) {

    const decorationService = new DecorationService();

    if(!editor) {
        editor = window.activeTextEditor
    }

    if(!document) {
        document = editor?.document
    }

    if(editor && document && document.languageId === 'c4') {
        commands.executeCommand("c4-server.text-decorations", { uri: document.uri.path }).then( callback => {
            editor?.setDecorations( decType, decorationService.toTextDecorations(callback as CommandResultTextDecorations))
        })        
    }
}

export function updateServerConfiguration() {

    const configOptions: ConfigurationOptions = { renderer:  workspace.getConfiguration().get(CONF_INLINE_RENDERER) as string};

    commands.executeCommand("c4-server.configuration", configOptions).then( callback => {
        window.showInformationMessage("Configuration Updated")
    });

}

export function deactivate() {
    
    if(proc) {
        proc.kill('SIGINT');
    }

} 