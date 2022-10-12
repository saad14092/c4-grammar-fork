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
import * as readline from 'readline'

import { LanguageClientOptions, StateChangeEvent, State } from 'vscode-languageclient';
import { LanguageClient, ServerOptions, StreamInfo } from 'vscode-languageclient/node';
//import { C4StructurizrPreview } from './c4-structurizr-preview';
import { C4PlantUMLPreview } from './c4-plantuml-preview';
import { C4StructurizrPreview } from './c4-structurizr-preview';
import { toTextDecorations, CommandResultTextDecorations } from './c4-decorator';

const CONF_PLANTUML_GENERATOR = "c4.export.plantuml.generator"
const CONF_PLANTUML_EXPORT_DIR = "c4.export.plantuml.dir"
const CONF_LANGUAGESERVER_CONNECTIONTYPE = "c4.languageserver.connectiontype"
const CONF_DIAGRAM_STRUCTURIZR_ENABLED = "c4.diagram.structurizr.enabled"
const CONF_DIAGRAM_STRUCTURZR_URI = "c4.diagram.structurizr.uri"
const CONF_DIAGRAM_PLANTUML_ENABLED = "c4.diagram.plantuml.enabled"
const CONF_PLANTUML_SERVER = "c4.show.plantuml.server"
const CONF_INLINE_RENDERER = "c4.diagram.renderer"
const CONF_TEXT_DECORATIONS = "c4.decorations.enabled"

const decType = window.createTextEditorDecorationType({});

type PlantUmlExportOptions = {
    uri: string;
    outDir: string;
    renderer: string;
}

type ConfigurationOptions = {
    renderer: string
    flavour?: string
}

type CommandResultCode = {
    resultcode: number;
    message: string;
}

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
    const textDecorations = workspace.getConfiguration().get(CONF_TEXT_DECORATIONS) as boolean

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

        //proc = cp.spawn(path.join(serverLauncher), ['--socket', READY_ECHO], {shell: true})
        proc = cp.exec( '"' + serverLauncher + '" '+ ['-c=socket', '-e='+READY_ECHO, '-ir='+renderer].join(' '))

        readline.createInterface({
            input     : proc.stdout,
            terminal  : false
          }).on('line', function(line: string) {
            if(line.endsWith(READY_ECHO)) {
                languageClient.start();
//                context.subscriptions.push(disposable);
            }
        });
    }

    else {
        languageClient.start();
//        context.subscriptions.push(disposable);
    }

    /*
    commands.registerCommand("c4.goto.taggedElement", (_range: LSRange) => {
        const range = new Range( new Position(_range.start.line, _range.start.character),
            new Position(_range.end.line, _range.end.character))
        window.activeTextEditor?.revealRange(range);
    });      
    */
    
    const structurizrPanel = new C4StructurizrPreview(logger)
    const svgPreviewPanel = new C4PlantUMLPreview( workspace.getConfiguration().get(CONF_PLANTUML_SERVER) as string)

    commands.registerCommand("c4.show.diagram", async(...args: string[]) => {        
        const diagramEnabled = workspace.getConfiguration().get(CONF_DIAGRAM_STRUCTURIZR_ENABLED) as boolean
        const structurizrUri = workspace.getConfiguration().get(CONF_DIAGRAM_STRUCTURZR_URI) as string

        if(!diagramEnabled) {
            window.showInformationMessage("You have to set the config item 'c4.diagram.structurizr.enabled' to true, if you want to use the public structurizr renderer");
        }

        else {
            const encodedWorkspaceJson = args[0]
            const diagramKey = args[1]
    
            try {
                await structurizrPanel.updateWebView(encodedWorkspaceJson, diagramKey, structurizrUri);
            }
            catch (err) {
                logger.appendLine("Error displaying preview: " + JSON.stringify(err))
            }    
        }        
    });

    commands.registerCommand("c4.show.plantuml", async(...args: string[]) => {        

        const diagramEnabled = workspace.getConfiguration().get(CONF_DIAGRAM_PLANTUML_ENABLED) as boolean

        if(!diagramEnabled) {
            window.showInformationMessage("You have to set the config item 'c4.diagram.plantuml.enabled' to true, if you want to use the public kroki rendering service");
        }
        else {
            const encodedPlantUML = args[0]
    
            try {
                await svgPreviewPanel.updateWebView(encodedPlantUML);
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

    if(textDecorations) {
        workspace.onDidSaveTextDocument( document => {
            triggerTextDecorations(undefined, document)
        })
    
        window.onDidChangeActiveTextEditor( editor => {
            triggerTextDecorations(editor, undefined)
        });
    
        triggerTextDecorations(window.activeTextEditor, undefined)    
    }

    logger.appendLine("Initialized");
    return languageClient;
}

function triggerTextDecorations(editor: TextEditor | undefined, document: TextDocument | undefined) {

    if(!editor) {
        editor = window.activeTextEditor
    }

    if(!document) {
        document = editor?.document
    }

    if(editor && document && document.languageId === 'c4') {
        commands.executeCommand("c4-server.text-decorations", { uri: document.uri.path }).then( callback => {
            editor?.setDecorations( decType, toTextDecorations(callback as CommandResultTextDecorations))
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