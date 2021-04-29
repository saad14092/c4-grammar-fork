import { Uri, workspace, WorkspaceFolder } from "vscode";

export function determineWorkspaceFolder(fn: Uri): WorkspaceFolder | undefined {
    return workspace.workspaceFolders?.find( (folder) => { return fn.fsPath.startsWith(folder.uri.fsPath)}); 
}