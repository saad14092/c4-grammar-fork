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

import {ExtensionContext, workspace, commands, window, StatusBarAlignment } from 'vscode'
import * as path from 'path';
import * as os from 'os';

import { LanguageClientOptions, Trace, StateChangeEvent, State} from 'vscode-languageclient';
import { LanguageClient, ServerOptions } from 'vscode-languageclient/node';
//import { C4PlantUMLPreview } from './c4-plantuml-preview';
import { C4StructurizrPreview } from './c4-structurizr-preview';
//import { MermaidPreview } from './c4-mermaid';
//import { buildPath } from './c4-utils';

const CONF_SEMANTIC_HIGHLIGHTING = "c4.language.SemanticHighlighting"
const CONF_PLANTUML_GENERATOR = "c4.plantuml.generator"
//const CONF_PLANTUML_RENDERER = "c4.plantuml.renderer"

export function activate(context: ExtensionContext) {

    console.log("HOME DIR: "+os.homedir());

    workspace.onDidChangeConfiguration( (event) => {
        if(event.affectsConfiguration(CONF_SEMANTIC_HIGHLIGHTING)) {
            commands.executeCommand("workbench.action.reloadWindow")
        }
        /*
        else if(event.affectsConfiguration(CONF_PLANTUML_GENERATOR)) {
            commands.executeCommand("workbench.action.reloadWindow")
        }
        */
    });

    const executable = process.platform === 'win32' ? 'c4-language-server.bat' : 'c4-language-server';
    const languageServerPath =  path.join('server', 'c4-language-server', 'bin', executable);
    const serverLauncher = context.asAbsolutePath(languageServerPath);

    const renderer = workspace.getConfiguration().get(CONF_PLANTUML_GENERATOR) !== undefined ? workspace.getConfiguration().get(CONF_PLANTUML_GENERATOR) as string : 'StructurizrPlantUMLWriter'

    const logger = window.createOutputChannel("C4 DSL Extension");
    logger.appendLine("Initializing");
 
    const serverOptions: ServerOptions = {
        run: {            
            command: serverLauncher,
            args: ['-log', '-trace', '-renderer', renderer]
        },
        debug: {
            command: serverLauncher,
            args: ['-log', '-trace', '-renderer', renderer]
        }
    };
    const clientOptions: LanguageClientOptions = {
        documentSelector: [{ scheme: 'file', language: 'c4' }],
        outputChannel: logger,
//        revealOutputChannelOn: RevealOutputChannelOn.Info,
        synchronize: {
            fileEvents: workspace.createFileSystemWatcher('**/*.dsl')
        }
    };
    const languageClient = new LanguageClient('c4LanguageClient', 'C4 Language Server', serverOptions, clientOptions);

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
                break;
            case State.Stopped:
                statusBarItem.text = "C4 Language Server has stopped"
                statusBarItem.color = 'red'
                break;                
        }
    })

    languageClient.trace = Trace.Verbose
    const disposable = languageClient.start();

    /*
    commands.registerCommand("c4.goto.taggedElement", (_range: LSRange) => {
        const range = new Range( new Position(_range.start.line, _range.start.character),
            new Position(_range.end.line, _range.end.character))
        window.activeTextEditor?.revealRange(range);
    });      
    */
    context.subscriptions.push(disposable);
    
    //const svgPreviewPanel = new C4PlantUMLPreview(workspace.getConfiguration().get(CONF_PLANTUML_RENDERER) as string, logger)
    const structurizrPanel = new C4StructurizrPreview(logger);
    //const mermaidPanel = new MermaidPreview(logger);

    commands.registerCommand("c4.show.diagram", async(...args: string[]) => {
        
        const encodedWorkspaceJson = args[0]
        const diagramKey = args[1]

        try {
            await structurizrPanel.updateWebView(encodedWorkspaceJson, diagramKey);
        }
        catch (err) {
            logger.appendLine("Error displaying preview: " + JSON.stringify(err))
        }
        
    });

    /*
    commands.registerCommand("c4.show.diagram", async(...args: string[]) => {
        if(workspace.workspaceFolders) {
            const puml = args[0]
            const workspaceFolder = args[1]
            const encodedWorkspaceJson = args[2]
            const diagramKey = args[3]

            try {
                if( renderer == "StructurizrOrigin" ) {
                    const genEncJsonPath = buildPath(encodedWorkspaceJson, Uri.parse(workspaceFolder))
                    if(!genEncJsonPath) {
                        throw new Error(`Could not map to local file path for ${encodedWorkspaceJson} and ${workspaceFolder}`)
                    }
                    await structurizrPanel.updateWebView(genEncJsonPath, diagramKey)
                }
                else if ( renderer == "MermaidWriter" ) {
                    const tempPumlPath = buildPath(puml, Uri.parse(workspaceFolder))
                    if (!tempPumlPath?.endsWith('.puml')) {
                        throw new Error(`Invalid puml path: ${tempPumlPath}`)
                    }
                    const mermaidPath = tempPumlPath.substring(0, tempPumlPath.length-5) + '.mmd'
                    await mermaidPanel.updateWebView(mermaidPath)
                }
                else {
                    const pumlPath = buildPath(puml, Uri.parse(workspaceFolder))
                    if(!pumlPath) {
                        throw new Error(`Could not map to local file path for ${pumlPath} and ${workspaceFolder}`)
                    }
                    await svgPreviewPanel.updateWebView(pumlPath)
                }
            } catch (err) {
                logger.appendLine("Error displaying preview: " + JSON.stringify(err))
            }
        }
    });     
    */
    logger.appendLine("Initialized");
    return languageClient;
}


export function deactivate() {
    
} 