import { WebviewPanel, window, ViewColumn, OutputChannel } from "vscode";

export class C4StructurizrPreview {

    panel: WebviewPanel | undefined

    logger: OutputChannel;

    constructor(logger: OutputChannel) {
        this.logger = logger
    }

    private createPanel() {

        const panel = window.createWebviewPanel(
            'Structurizr Preview',
            'Structurizr Preview',
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

    async updateWebView(encodedJson: string, diagramKey: string, structurizrUri: string) {
        if (!this.panel) {
            this.panel = this.createPanel();
        }

        const html = this.updateViewContent(encodedJson, diagramKey, structurizrUri)
        console.log(html)

        this.panel.webview.html = html

        if(!this.panel.visible) {
            this.panel.reveal();
        }    
    }

    private updateViewContent(encodedJson: string, diagramKey: string, renderUri: string) {

        return `<!DOCTYPE html>
        <html>
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
                <iframe id="structurizrPreview" name="structurizrPreview" width="100%" marginwidth="0" marginheight="0" frameborder="0" scrolling="no"></iframe>
        
                <form id="structurizrPreviewForm" method="post" action="${renderUri}" target="structurizrPreview" style="display: none;">
                    <input type="hidden" name="iframe" value="structurizrPreview" />
                    <input type="hidden" name="preview" value="true" />
                    <input type="hidden" name="source" value="${encodedJson}" />
                    <input type="hidden" name="diagram" value="${diagramKey}" />
                </form>
        
                <script>
                    document.getElementById("structurizrPreviewForm").submit();
                </script>
                <script type="text/javascript" src="https://static.structurizr.com/js/structurizr-embed.js"></script>
            </body>
        </html>
        `
    }

}