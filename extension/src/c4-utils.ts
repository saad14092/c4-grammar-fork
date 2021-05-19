import { Uri, workspace } from "vscode";
import * as path from 'path';
import * as os from 'os';

const BASE_DIR: string = path.join( os.homedir(), '.c4dslextension')

export function buildPath(generatedFile: string, workspaceFolder: Uri): string | undefined {
    
    const folder =  workspace.workspaceFolders?.find( (folder) => { 
        return workspaceFolder.fsPath.startsWith(folder.uri.fsPath)
    }); 

    if(folder) {
        const subFolder = workspaceFolder.fsPath.replace(folder.uri.fsPath, '')
        const fullPath = path.join(BASE_DIR, folder.name,  subFolder, Uri.parse(generatedFile).fsPath)
        return fullPath;
    }

    return undefined
}