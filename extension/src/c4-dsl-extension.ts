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

import {ExtensionContext, workspace, languages, commands, Uri, window, Range, Position} from 'vscode'
import * as path from 'path';
import { LanguageClient, LanguageClientOptions, ServerOptions, Trace, Range as LSRange } from 'vscode-languageclient';
import { C4SemanticTokenProvider, c4Legend } from './c4-semantic-highlight';

const CONF_SEMANTIC_HIGHLIGHTING = "c4.language.SemanticHighlighting"

export function activate(context: ExtensionContext) {

    const isHighlighted = workspace.getConfiguration().get(CONF_SEMANTIC_HIGHLIGHTING)

    if(isHighlighted !== undefined && isHighlighted === true) {
        context.subscriptions.push(languages.registerDocumentSemanticTokensProvider( {language: 'c4', scheme: 'file'}
        , new C4SemanticTokenProvider(), c4Legend ))
    }

    workspace.onDidChangeConfiguration( (event) => {
        if(event.affectsConfiguration(CONF_SEMANTIC_HIGHLIGHTING)) {
            commands.executeCommand("workbench.action.reloadWindow")
        }
    });

    const executable = process.platform === 'win32' ? 'c4-language-server.bat' : 'c4-language-server';
    const languageServerPath =  path.join('server', 'c4-language-server', 'bin', executable);
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
            fileEvents: workspace.createFileSystemWatcher('**/*.dsl')
        }
    };
    const languageClient = new LanguageClient('c4LanguageClient', 'C4 Language Server', serverOptions, clientOptions);

    languageClient.trace = Trace.Verbose
    const disposable = languageClient.start();

    commands.registerCommand("c4.show.diagram", (uri: string) => {
        if(workspace.workspaceFolders) {
            commands.executeCommand("vscode.open", Uri.parse(workspace.workspaceFolders[0].uri+'/plantuml-gen/'+uri)).then(
                 () => commands.executeCommand("plantuml.preview"))
        }
    });     

    commands.registerCommand("c4.goto.taggedElement", (_range: LSRange) => {
        const range = new Range( new Position(_range.start.line, _range.start.character),
            new Position(_range.end.line, _range.end.character))
        window.activeTextEditor?.revealRange(range);
    });      

    context.subscriptions.push(disposable);
    
    return languageClient;
}

export function deactivate() {
    
} 