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

import {ExtensionContext, workspace, commands, window, StatusBarAlignment, Uri } from 'vscode'
import * as path from 'path';

import { LanguageClientOptions, Trace, StateChangeEvent, State} from 'vscode-languageclient';
import { LanguageClient, ServerOptions } from 'vscode-languageclient/node';
import { C4StructurizrPreview } from './c4-structurizr-preview';

const CONF_PLANTUML_GENERATOR = "c4.export.plantuml.generator"
const CONF_PLANTUML_EXPORT_DIR = "c4.export.plantuml.dir"

type PlantUmlExportOptions = {
    uri: string;
    outDir: string;
    renderer: string;
}

export function activate(context: ExtensionContext) {

    const executable = process.platform === 'win32' ? 'c4-language-server.bat' : 'c4-language-server';
    const languageServerPath =  path.join('server', 'c4-language-server', 'bin', executable);
    const serverLauncher = context.asAbsolutePath(languageServerPath);

    const logger = window.createOutputChannel("C4 DSL Extension");
    logger.appendLine("Initializing");
 
    const serverOptions: ServerOptions = {
        run: {            
            command: serverLauncher,
            args: ['-log', '-trace']
        },
        debug: {
            command: serverLauncher,
            args: ['-log', '-trace']
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
    
    const structurizrPanel = new C4StructurizrPreview(logger);

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

    commands.registerCommand("c4.export.puml", ( uri: Uri ) => {

        const renderer = workspace.getConfiguration().get(CONF_PLANTUML_GENERATOR) as string; 
        const exportDir = workspace.getConfiguration().get(CONF_PLANTUML_EXPORT_DIR) as string; 

        const exportOptions: PlantUmlExportOptions = { uri: uri.path, renderer: renderer, outDir: exportDir };

        commands.executeCommand("c4-server.export.puml", exportOptions).then( result => {
            logger.appendLine("Result = "+result);
        });
    });


    logger.appendLine("Initialized");
    return languageClient;
}


export function deactivate() {
    
} 