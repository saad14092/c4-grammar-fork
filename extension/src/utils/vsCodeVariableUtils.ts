import { homedir } from "os";
import { basename, dirname, extname } from "path";
import { window, workspace } from "vscode";

export const substituteVariable = (
  variableName: string
): string | undefined => {
  switch (variableName) {
    /**
     * ${userHome} - the path of the user's home folder
     */
    case "${userHome}":
      return homedir();
    /**
     * ${workspaceFolder} - the path of the folder opened in VS Code
     */
    case "${workspaceFolder}":
      return workspace.workspaceFolders?.[0].uri.fsPath;
    /**
     * ${workspaceFolderBasename} - the name of the folder opened in VS Code without any slashes (/)
     */
    case "${workspaceFolderBasename}":
      return getWorkspaceFolderBase();
    /**
     * ${file} - the current opened file
     */
    case "${file}":
      return getFile();
    /**
     * ${fileWorkspaceFolder} - the current opened file's workspace folder
     */
    case "${fileWorkspaceFolder}":
      return getFileWorkspaceFolder();
    /**
     * ${relativeFile} - the current opened file relative to workspaceFolder
     */
    case "${relativeFile}":
      return getRelativeFile();
    /**
     * ${relativeFileDirname} - the current opened file's dirname relative to workspaceFolder
     */
    case "${relativeFileDirname}":
      return getRelativeFileDirname();
    /**
     * ${fileBasename} - the current opened file's basename
     */
    case "${fileBasename}":
      return getFileBasename();
    /**
     * ${fileBasenameNoExtension} - the current opened file's basename with no file extension
     */
    case "${fileBasenameNoExtension}":
      return getFileBasenameNoExtension();
    /**
     * ${fileExtname} - the current opened file's extension
     */
    case "${fileExtname}":
      return getFileExtname();
    /**
     * ${fileDirname} - the current opened file's folder path
     */
    case "${fileDirname}":
      return getFileDirname();
    /**
     * ${fileDirnameBasename} - the current opened file's folder name
     */
    case "${fileDirnameBasename}":
      return getFileDirnameBasename();
    /**
     * ${cwd} - the task runner's current working directory upon the startup of VS Code
     */
    case "${cwd}":
      return process.cwd();
    /**
     * ${pathSeparator} - the character used by the operating system to separate components in file paths
     */
    case "${pathSeparator}":
    /**
     * ${/} - shorthand for ${pathSeparator}
     */
    case "${/}":
      return getPathSeparator();
    /**
     * When the argument is not a predefined VS Code, simply return the argument
     */
    default:
      return variableName;
  }
};

const getWorkspaceFolderBase = () => {
  const workspaceFolder = workspace.workspaceFolders?.[0].uri.fsPath;
  if (workspaceFolder) {
    return basename(workspaceFolder);
  }
};

const getFileWorkspaceFolder = () => {
  const activeTextEditor = window.activeTextEditor;
  const workspaceFolder = workspace.workspaceFolders?.[0].uri.fsPath;
  if (activeTextEditor && workspaceFolder) {
    const fileWorkspaceFolder = workspace.getWorkspaceFolder(
      activeTextEditor.document.uri
    )?.uri.fsPath;
    if (fileWorkspaceFolder) {
      return fileWorkspaceFolder;
    }
  }
};

const getRelativeFileDirname = () => {
  const pathSeparator = getPathSeparator();
  const relativeFilePath = getRelativeFile();
  if (relativeFilePath) {
    const relativeFilePathAsArray = relativeFilePath.split(pathSeparator);
    const relativeFileDirname = relativeFilePathAsArray[0];
    return relativeFileDirname;
  }
};

const getRelativeFile = () => {
  const pathSeparator = getPathSeparator();
  const filePath = getFile();
  if (filePath) {
    const filePathAsArray = filePath.split(pathSeparator);
    const relativeFileAsArray = filePathAsArray.slice(-2);
    return relativeFileAsArray.join(pathSeparator);
  }
};

const getPathSeparator = () => {
  if (process.platform === "win32") {
    return "\\";
  }
  return "/";
};

const getFile = () => {
  const activeTextEditor = window.activeTextEditor;
  if (activeTextEditor) {
    return activeTextEditor.document.uri.fsPath;
  }
};

const getFileBasename = () => {
  const activeTextEditor = window.activeTextEditor;
  if (activeTextEditor) {
    return basename(activeTextEditor.document.uri.fsPath);
  }
};

const getFileBasenameNoExtension = () => {
  const activeTextEditor = window.activeTextEditor;
  if (activeTextEditor) {
    return basename(
      activeTextEditor.document.uri.fsPath,
      extname(activeTextEditor.document.uri.fsPath)
    );
  }
};

const getFileExtname = () => {
  const activeTextEditor = window.activeTextEditor;
  if (activeTextEditor) {
    return extname(activeTextEditor.document.uri.fsPath);
  }
};

const getFileDirnameBasename = () => {
  const fileDirname = getFileDirname();
  if (fileDirname) {
    return basename(fileDirname);
  }
};

const getFileDirname = () => {
  const activeTextEditor = window.activeTextEditor;
  if (activeTextEditor) {
    return dirname(activeTextEditor.document.uri.fsPath);
  }
};
