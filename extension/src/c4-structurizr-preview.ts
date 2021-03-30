import { WebviewPanel, window, ViewColumn, Uri, WorkspaceFolder, workspace } from "vscode";
import * as fs from 'fs';
import * as path from 'path';

export class C4StructurizrPreview {

    panel: WebviewPanel | undefined

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

    private determineWorkspaceFolder(fn: string): WorkspaceFolder | undefined  {
        return workspace.workspaceFolders?.find( (folder) => { return fn.startsWith(folder.uri.fsPath) }); 
    }

    public updateWebView(dslFile: Uri) {

        const ws = this.determineWorkspaceFolder(dslFile.fsPath)

        if(ws) {
            const filename = dslFile.fsPath.replace(/^.*[\\\/]/, '').replace('.dsl', '')
            const encodedJsonFile = path.join(ws.uri.fsPath, 'plantuml-gen', filename+'_workspace.json')

            if(fs.existsSync(encodedJsonFile)) {
                const content = fs.readFileSync(encodedJsonFile, 'utf8')
                if (!this.panel) {
                    this.panel = this.createPanel();
                }
                const html = this.updateViewContent(content)
                console.log(html)

                this.panel.webview.html = html

                if(!this.panel.visible) {
                    this.panel.reveal();
                }
    
            }
        }
    }

    private updateViewContent(encodedJson: string) {

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
        
                <form id="structurizrPreviewForm" method="post" action="https://structurizr.com/json" target="structurizrPreview" style="display: none;">
                    <input type="hidden" name="iframe" value="structurizrPreview" />
                    <input type="hidden" name="preview" value="true" />
                    <input type="hidden" name="source" value="${encodedJson}" />
                    <input type="hidden" name="diagram" value="" />
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