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

import * as vscode from 'vscode';
import * as path from 'path';
import { LanguageClient, LanguageClientOptions, ServerOptions, Trace, Range as LSRange } from 'vscode-languageclient';

export function activate(context: vscode.ExtensionContext) {

    const executable = process.platform === 'win32' ? 'c4-language-server.bat' : 'c4-language-server';
    const languageServerPath =  path.join('server', 'c4-language-server-1.0.0-SNAPSHOT', 'bin', executable);
    const serverLauncher = context.asAbsolutePath(languageServerPath);
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
        synchronize: {
            fileEvents: vscode.workspace.createFileSystemWatcher('**/*.c4')
        }
    };
    const languageClient = new LanguageClient('c4LanguageClient', 'C4 Language Server', serverOptions, clientOptions);

    languageClient.trace = Trace.Verbose
    const disposable = languageClient.start();

    vscode.commands.registerCommand("c4.show.diagram", (uri: string) => {
        if(vscode.workspace.workspaceFolders) {
            vscode.commands.executeCommand("vscode.open", vscode.Uri.parse(vscode.workspace.workspaceFolders[0].uri+'/src-gen/'+uri)).then(
                 () => vscode.commands.executeCommand("plantuml.preview"))
        }
    });     

    vscode.commands.registerCommand("c4.goto.taggedElement", (_range: LSRange) => {
        const range = new vscode.Range( new vscode.Position(_range.start.line, _range.start.character),
            new vscode.Position(_range.end.line, _range.end.character))
        vscode.window.activeTextEditor?.revealRange(range);
    });      

    context.subscriptions.push(disposable);
    
    return languageClient;
}

export function deactivate() {
    
} 