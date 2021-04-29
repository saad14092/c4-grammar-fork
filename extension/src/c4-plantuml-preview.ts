import { window, ViewColumn, WebviewPanel, OutputChannel, Uri } from "vscode";
import * as path from 'path';
import * as pako from 'pako';
import * as fs from 'fs';
import got from 'got';
import { determineWorkspaceFolder } from "./c4-utils";

export class C4PlantUMLPreview {

    urlSVG: string;
    logger: OutputChannel;

    constructor(renderer: string, logger: OutputChannel) {
        this.urlSVG = renderer + "/" + "svg"
        this.logger = logger
    }

    panel: WebviewPanel | undefined

    private createPanel() {

        const panel = window.createWebviewPanel(
            'PlantUML Preview',
            'PlantUML Preview',
            ViewColumn.Two,
            {}
        );

        panel.onDidDispose( () => {
            this.panel = undefined
        })

        return panel;
    }
    async updateWebView(generatedPuml: string, folderFromServer: string) {

        const workspaceFolder = determineWorkspaceFolder(Uri.parse(folderFromServer))
        if(!workspaceFolder) {
            throw new Error("Could not find workspace for folder: " +  folderFromServer)
        }

        const subFolder = Uri.parse(folderFromServer).fsPath.replace(workspaceFolder.uri.fsPath,'')

        const pumlFile = Uri.file(path.join(workspaceFolder.uri.fsPath, 'plantuml-gen', subFolder, generatedPuml)).fsPath
        const svgUri = Uri.file(path.join(workspaceFolder.uri.fsPath, 'plantuml-gen', subFolder, generatedPuml.replace('puml', 'svg')))

        if (!this.panel) {
            this.panel = this.createPanel();
        }

        if (this.needsUpdate(pumlFile, svgUri.fsPath)) {
            //this.logger.appendLine("Create a new svg file")
            const urlCode = this.encode(pumlFile)
            if (urlCode) {
                const svg = await this.toSVG(urlCode)
                fs.writeFileSync(svgUri.fsPath, svg)
                if(this.panel) {
                    this.panel.webview.html = this.updateViewContent(svg);    
                }
            }
        }
        else {
            //this.logger.appendLine("Re-use existing svg file")
            const svg = fs.readFileSync(svgUri.fsPath, 'utf-8')
            this.panel.webview.html = this.updateViewContent(svg);
        }
    }

    private encode(puml: string): string | null {

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
    private needsUpdate(pumlFile: string, svgFile: string): Boolean {

        return !fs.existsSync(svgFile) || fs.statSync(pumlFile).mtime > fs.statSync(svgFile).mtime

    }

    private async toSVG(encoded: string): Promise<string> {
        this.logger.appendLine("Creating new svg file via " + this.urlSVG)
        const response = await got(this.urlSVG +"/" + encoded);
        return response.body;
    }

    private updateViewContent(svgFile: string) {

        return `<!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Cat Coding</title>
            <style>
                body.vscode-light {
                    background-color: white;
                }              
                body.vscode-dark {
                    background-color: white;
                }            
            </style>
        </head>
        <body>
            ${svgFile}
        </body>
        </html>`;
    }

}
