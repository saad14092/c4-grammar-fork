import { Uri, workspace } from "vscode";
import * as path from 'path';
import * as os from 'os';

export default class C4Utils {

    static buildPath(generatedFile: string, workspaceFolder: Uri): string | undefined {
        const folder =  workspace.workspaceFolders?.find( (folder) => { 
            return workspaceFolder.fsPath.startsWith(folder.uri.fsPath)
        }); 
    
        if(folder) {
            const baseDir = path.join( os.homedir(), '.c4dslextension');
            const subFolder = workspaceFolder.fsPath.replace(folder.uri.fsPath, '')
            const fullPath = path.join(baseDir, folder.name,  subFolder, Uri.parse(generatedFile).fsPath)
            return fullPath;
        }
    
        return undefined
    }
}