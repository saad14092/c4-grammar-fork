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

import {ExtensionContext, workspace, languages, commands, Uri, window, Range, Position, ViewColumn} from 'vscode'
import * as path from 'path';
import { LanguageClient, LanguageClientOptions, ServerOptions, Trace, Range as LSRange} from 'vscode-languageclient';
import { C4SemanticTokenProvider, c4Legend } from './c4-semantic-highlight';
import { getWebViewContent } from './c4-plantuml-preview';
import * as pako from 'pako';
import * as fs from 'fs';
import got from 'got';

const CONF_SEMANTIC_HIGHLIGHTING = "c4.language.SemanticHighlighting"
const CONF_PLANTUML_GENERATOR = "c4.plantuml.generator"

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
        else if(event.affectsConfiguration(CONF_PLANTUML_GENERATOR)) {
            //TODO: cleanup existing plantum files
            commands.executeCommand("workbench.action.reloadWindow")
        }
    });

    const executable = process.platform === 'win32' ? 'c4-language-server.bat' : 'c4-language-server';
    const languageServerPath =  path.join('server', 'c4-language-server', 'bin', executable);
    const serverLauncher = context.asAbsolutePath(languageServerPath);

    const renderer = workspace.getConfiguration().get(CONF_PLANTUML_GENERATOR) !== undefined ? workspace.getConfiguration().get(CONF_PLANTUML_GENERATOR) as string : 'StructurizrPlantUMLWriter'

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
        synchronize: {
            fileEvents: workspace.createFileSystemWatcher('**/*.dsl')
        }
    };
    const languageClient = new LanguageClient('c4LanguageClient', 'C4 Language Server', serverOptions, clientOptions);

    languageClient.trace = Trace.Verbose
    const disposable = languageClient.start();

    commands.registerCommand("c4.goto.taggedElement", (_range: LSRange) => {
        const range = new Range( new Position(_range.start.line, _range.start.character),
            new Position(_range.end.line, _range.end.character))
        window.activeTextEditor?.revealRange(range);
    });      

    context.subscriptions.push(disposable);
    
    const panel = window.createWebviewPanel(
        'PlantUML Preview',
        'PlantUML Preview',
        ViewColumn.One,
        {}
      );

      /*
      if(workspace.workspaceFolders) {
        const onDiskPath = Uri.file(path.join( workspace.workspaceFolders[0].uri.fsPath, 'plantuml-gen', 'issue39_container_Software System.svg'));    
        const plantUmlSvgSrc = panel.webview.asWebviewUri(onDiskPath);
        panel.webview.html = getWebViewContent(plantUmlSvgSrc);
      }
    */

    const updateWebView = (fn: string) => {

        if(workspace.workspaceFolders) {
           const pumlFile = Uri.file(path.join( workspace.workspaceFolders[0].uri.fsPath, 'plantuml-gen', fn)).fsPath
           const svgUri = Uri.file(path.join( workspace.workspaceFolders[0].uri.fsPath, 'plantuml-gen', fn.replace('puml', 'svg')))

          if(needsUpdate(pumlFile, svgUri.fsPath)) {
            console.log("Create a new svg file")
            const urlCode = encode(pumlFile)
            if(urlCode) {
                toSVG(urlCode).then( (svg: string) => {
                    fs.writeFileSync(svgUri.fsPath, svg)
                    const plantUmlSvgSrc = panel.webview.asWebviewUri(svgUri);
                    panel.webview.html = getWebViewContent(plantUmlSvgSrc);         
                });            
           }
          }
          else {
            console.log("Re-use existing svg file")
            const plantUmlSvgSrc = panel.webview.asWebviewUri(svgUri);
            panel.webview.html = getWebViewContent(plantUmlSvgSrc);         
          }           
        }    
    }

    commands.registerCommand("c4.show.diagram", (uri: string) => {
        if(workspace.workspaceFolders) {
            updateWebView(uri)
        }
    });     


      // And set its HTML content

    return languageClient;
}

export function encode(puml: string): string | null {

    try {
        const content = fs.readFileSync(puml, 'utf8')
        const data = Buffer.from(content, 'utf8') 
        const compressed = pako.deflate(data, { level: 9 }) 
        return Buffer.from(compressed)
          .toString('base64') 
          .replace(/\+/g, '-').replace(/\//g, '_')    
      } catch (err) {
        console.error(err)
      }

    return null
}

/**
 * The svg needs to be updated, if it the svg file does not exist at all or the puml file is newer than the svg file
 * @param pumlFile 
 * @param svgFile 
 */
export function needsUpdate(pumlFile: string, svgFile: string): Boolean {

    return !fs.existsSync(svgFile) || fs.statSync(pumlFile).mtime > fs.statSync(svgFile).mtime

}

export async function toSVG(encoded: string): Promise<string> {

    const response = await got('https://kroki.io/plantuml/svg/'+encoded);
    return response.body;
}

/*
function sendPlantUmlRenderer() {

    const renderConf = workspace.getConfiguration().get(CONF_PLANTUML_GENERATOR)

    const renderer: string = renderConf !== undefined ? renderConf as string: "StructurizrPlantUMLWriter"

    commands.executeCommand("c4.generator.type", renderer)
}
*/

export function deactivate() {
    
} 