import got from "got/dist/source";
import { ViewColumn, WebviewPanel, window } from "vscode";

export class C4DiagramView {

    private _renderService: string;
    private title: string;
    private viewType: string;

    constructor(renderService: string, viewTpe: string, title: string) {
        this.title = title
        this.viewType = viewTpe
        this._renderService = renderService
    }

    panel: WebviewPanel | undefined

    public get renderService() {
        return this._renderService;
    }

    private createPanel() {

        const panel = window.createWebviewPanel(
            this.viewType,
            this.title,
            ViewColumn.Two,
            {}
        );

        panel.onDidDispose( () => {
            this.panel = undefined
        })

        return panel;
    }

    async updateWebView(encodedContent: string, createUri: (content: string) => string) {

        if (!this.panel) {
            this.panel = this.createPanel();
        }

        const svg = await this.toSVG(createUri(encodedContent))
        this.panel.webview.html = this.updateViewContent(svg);
    }

    private async toSVG(url: string): Promise<string> {
        const response = await got(url);
        return response.body;
    }
    
    public updateViewContent(svg: string) {

        return `<!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${this.title}</title>
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
            ${svg}
        </body>
        </html>`;
    }


}