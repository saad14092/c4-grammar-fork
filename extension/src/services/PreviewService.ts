import got from "got";
import { ViewColumn, WebviewPanel, window } from "vscode";

class PreviewService {
  private renderService: string;
  private title: string;
  private viewType: string;
  private panel: WebviewPanel | undefined;

  constructor(renderService: string, viewTpe: string, title: string) {
    this.title = title;
    this.viewType = viewTpe;
    this.renderService = renderService;
  }

  public async updateWebView(encodedContent: string, args: string) {
    if (!this.panel) {
      this.panel = this.createPanel();
    }

    const content = await this.getViewContent(encodedContent, args);
    this.panel.webview.html = this.updateViewContent(content);
  }

  private createPanel(): WebviewPanel {
    const panel = window.createWebviewPanel(
      this.viewType,
      this.title,
      ViewColumn.Two,
      {
        enableScripts: true,
      }
    );

    panel.onDidDispose(() => {
      this.panel = undefined;
    });

    return panel;
  }

  private async getViewContent(content: string, args: string) {
    if (this.viewType.toLowerCase() === "uml") {
      return await this.toSVG(this.createUri(args, content));
    } else {
      return `
            <iframe id="structurizrPreview" name="structurizrPreview" width="100%" marginwidth="0" marginheight="0" frameborder="0" scrolling="no"></iframe>

            <form id="structurizrPreviewForm" method="post" action="${this.renderService}" target="structurizrPreview" style="display: none;">
                <input type="hidden" name="iframe" value="structurizrPreview" />
                <input type="hidden" name="preview" value="true" />
                <input type="hidden" name="source" value="${content}" />
                <input type="hidden" name="diagram" value="${args}" />
            </form>
    
            <script>
                document.getElementById("structurizrPreviewForm").submit();
            </script>
            <script type="text/javascript" src="https://static.structurizr.com/js/structurizr-embed.js"></script>
            `;
    }
  }

  private createUri(endpoint: string, data: string): string {
    return this.renderService.concat(endpoint).concat(data);
  }

  private async toSVG(url: string): Promise<string> {
    const response = await got(url);
    return response.body;
  }

  private updateViewContent(body: string): string {
    return `
        <!DOCTYPE html>
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
            ${body}
        </body>
        </html>
        `;
  }
}

export { PreviewService };
