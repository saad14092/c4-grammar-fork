import { Uri, window, ViewColumn, WebviewPanel, workspace } from "vscode";
import * as path from 'path';
import * as pako from 'pako';
import * as fs from 'fs';
import got from 'got';

export class C4PlantUMLPreview {

    urlSVG: string;

    constructor(renderer: string) {
        this.urlSVG = renderer + "/" + "svg"
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

    updateWebView(fn: string) {

        if (workspace.workspaceFolders) {
            const pumlFile = Uri.file(path.join(workspace.workspaceFolders[0].uri.fsPath, 'plantuml-gen', fn)).fsPath
            const svgUri = Uri.file(path.join(workspace.workspaceFolders[0].uri.fsPath, 'plantuml-gen', fn.replace('puml', 'svg')))

            if (!this.panel) {
                this.panel = this.createPanel();
            }

            if (this.needsUpdate(pumlFile, svgUri.fsPath)) {
                console.log("Create a new svg file")
                const urlCode = this.encode(pumlFile)
                if (urlCode) {
                    this.toSVG(urlCode).then((svg: string) => {
                        fs.writeFileSync(svgUri.fsPath, svg)
                        if(this.panel) {
                            const plantUmlSvgSrc = this.panel.webview.asWebviewUri(svgUri);
                            this.panel.webview.html = this.updateViewContent(plantUmlSvgSrc);    
                        }
                    });
                }
            }
            else {
                console.log("Re-use existing svg file")
                const plantUmlSvgSrc = this.panel.webview.asWebviewUri(svgUri);
                this.panel.webview.html = this.updateViewContent(plantUmlSvgSrc);
            }
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

        const response = await got(this.urlSVG +"/" + encoded);
        return response.body;
    }

    private updateViewContent(svgFile: Uri) {

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
            <img src="${svgFile}" />
        </body>
        </html>`;
    }

}
