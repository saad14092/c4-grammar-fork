import axios from "axios";
import {
  TextDocument,
  ViewColumn,
  WebviewPanel,
  commands,
  window,
} from "vscode";
import { RefreshOptions } from "../types/RefreshOptions";
import { CommandResultCode } from "../types/CommandResultCode";

class PreviewService {
  private renderService: string;
  private title: string;
  private viewType: string;
  private panel: WebviewPanel | undefined;
  private _currentDiagram: string;
  private _currentDocument: TextDocument;

  constructor(renderService: string, viewTpe: string, title: string) {
    this.title = title;
    this.viewType = viewTpe;
    this.renderService = renderService;
  }

  public get currentDiagram() {
    return this._currentDiagram;
  }

  public set currentDiagram(diagram: string) {
    this._currentDiagram = diagram;
  }

  public get currentDocument() {
    return this._currentDocument;
  }

  public set currentDocument(document: TextDocument) {
    this._currentDocument = document;
  }

  public triggerRefresh(savedDoc: TextDocument, renderer: string) {
    if (this.currentDiagram && this.currentDocument === savedDoc) {
      const refreshOptions: RefreshOptions = {
        viewKey: this.currentDiagram,
        document: savedDoc.uri.path,
        renderer: renderer,
      };
      commands.executeCommand("c4.refresh", refreshOptions).then((callback) => {
        const result = callback as CommandResultCode;
        this.updateWebView(result.message);
      });
    }
  }

  public async updateWebView(encodedContent: string) {
    if (!this.panel) {
      this.panel = this.createPanel();
    }
    const content = await this.getViewContent(encodedContent);
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
      this.currentDiagram = "";
      this.panel = undefined;
    });
    return panel;
  }

  private async getViewContent(content: string) {
    if (this.viewType.toLowerCase() === "uml") {
      const diagram = await this.toSVG(this.createUri(content));
      return `
        <div id="diagram">${diagram}</div>
        <script src="https://unpkg.com/@panzoom/panzoom@4.5.1/dist/panzoom.min.js"></script>
        <script>
          const elem = document.getElementById("diagram");
          const panzoom = Panzoom(elem, {
            maxScale: 3
          })
          elem.parentElement.addEventListener('wheel', panzoom.zoomWithWheel)
          elem.parentElement.addEventListener('dblclick', () => {
            panzoom.reset();
          });
        </script>
      `;
    } else {
      return `
            <iframe id="structurizrPreview" name="structurizrPreview" width="100%" marginwidth="0" marginheight="0" frameborder="0" scrolling="no"></iframe>

            <form id="structurizrPreviewForm" method="post" action="${this.renderService}" target="structurizrPreview" style="display: none;">
                <input type="hidden" name="iframe" value="structurizrPreview" />
                <input type="hidden" name="preview" value="true" />
                <input type="hidden" name="source" value="${content}" />
                <input type="hidden" name="diagram" value="${this.currentDiagram}" />
            </form>
    
            <script>
                document.getElementById("structurizrPreviewForm").submit();
            </script>
            <script type="text/javascript" src="https://static.structurizr.com/js/structurizr-embed.js"></script>
            `;
    }
  }

  private createUri(data: string): string {
    return this.renderService.concat(data);
  }

  private async toSVG(url: string): Promise<string> {
    const response = await axios.get(url);
    return response.data;
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
