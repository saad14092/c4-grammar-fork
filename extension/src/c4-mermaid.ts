import { window, ViewColumn, WebviewPanel, OutputChannel } from "vscode";
import * as fs from 'fs';

export class MermaidPreview {

    logger: OutputChannel;

    constructor(logger: OutputChannel) {
        this.logger = logger
    }

    panel: WebviewPanel | undefined

    private createPanel() {

        const panel = window.createWebviewPanel(
            'PlantUML Preview',
            'PlantUML Preview',
            ViewColumn.Two,
            {
                enableScripts: true
            }
        );

        panel.onDidDispose( () => {
            this.panel = undefined
        })

        return panel;
    }
    async updateWebView(mermaidFile: string) {

        if (!this.panel) {
            this.panel = this.createPanel();
        }

        const mermaidContent = fs.readFileSync(mermaidFile, 'utf-8')
        this.panel.webview.html = this.updateViewContent(mermaidContent);
    }

    private htmlEscape(str : string) : string {
        return str
          .replace(/&/g, '&amp;')
          .replace(/'/g, "&apos;")
          .replace(/"/g, '&quot;')
          .replace(/>/g, '&gt;')   
          .replace(/</g, '&lt;');    
      }

    private updateViewContent(mermaidContent: string) {

        return `<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <div class="mermaid">
            ${this.htmlEscape(mermaidContent)}
        </div>
        <script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
        <script>mermaid.initialize({startOnLoad:true});</script>
    </body>
</html>`;
    }

}
