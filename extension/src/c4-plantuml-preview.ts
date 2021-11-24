import { window, ViewColumn, WebviewPanel } from "vscode";
import got from 'got';

export class C4PlantUMLPreview {

    urlSVG: string;

    constructor(renderer: string) {
        this.urlSVG = renderer + "/" + "plantuml" + "/" + "svg"
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
    async updateWebView(puml: string) {

        if (!this.panel) {
            this.panel = this.createPanel();
        }

        const svg = await this.toSVG(puml);
        this.panel.webview.html = this.updateViewContent(svg);
    }

    private async toSVG(encoded: string): Promise<string> {
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
