import { resolve } from "path";
import { Uri } from "vscode";

const getFileUri = (filename: string): Uri => {
  return Uri.file(resolve(__dirname, "../../src/test/resources/", filename));
};

export { getFileUri };
