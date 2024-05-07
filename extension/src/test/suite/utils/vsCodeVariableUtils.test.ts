import { assert } from "chai";
import { substituteVariable } from "../../../utils";
import { commands, workspace } from "vscode";
import { getFileUri } from "../../testHelper";

suite("VS Code variable test", () => {
  test("${file} returns absolute file path", (done) => {
    workspace.openTextDocument(getFileUri("amazon_web_service.dsl"));
    const separator = substituteVariable("${/}");
    const result = substituteVariable("${file}");
    assert.isTrue(
      result?.includes(
        `c4-grammar${separator}extension${separator}src${separator}test${separator}resources${separator}amazon_web_service.dsl`
      )
    );
    commands.executeCommand("workbench.action.closeActiveEditor");
    done();
  });

  test("${relativeFile} returns relative file path", (done) => {
    workspace.openTextDocument(getFileUri("amazon_web_service.dsl"));
    const separator = substituteVariable("${/}");
    const result = substituteVariable("${relativeFile}");
    assert.equal(result, `resources${separator}amazon_web_service.dsl`);
    commands.executeCommand("workbench.action.closeActiveEditor");
    done();
  });

  test("${relativeFileDirname} returns relative file directory", (done) => {
    workspace.openTextDocument(getFileUri("amazon_web_service.dsl"));
    const result = substituteVariable("${relativeFileDirname}");
    assert.equal(result, "resources");
    commands.executeCommand("workbench.action.closeActiveEditor");
    done();
  });

  test("${fileBasename} returns file name", (done) => {
    workspace.openTextDocument(getFileUri("amazon_web_service.dsl"));
    const result = substituteVariable("${fileBasename}");
    assert.equal(result, "amazon_web_service.dsl");
    commands.executeCommand("workbench.action.closeActiveEditor");
    done();
  });

  test("${fileBasenameNoExtension} returns file name without the extension", (done) => {
    workspace.openTextDocument(getFileUri("amazon_web_service.dsl"));
    const result = substituteVariable("${fileBasenameNoExtension}");
    assert.equal(result, "amazon_web_service");
    commands.executeCommand("workbench.action.closeActiveEditor");
    done();
  });

  test("${fileExtname} returns file extension", (done) => {
    workspace.openTextDocument(getFileUri("amazon_web_service.dsl"));
    const result = substituteVariable("${fileExtname}");
    assert.equal(result, ".dsl");
    commands.executeCommand("workbench.action.closeActiveEditor");
    done();
  });

  test("${fileDirname} returns current file directory", (done) => {
    workspace.openTextDocument(getFileUri("amazon_web_service.dsl"));
    const separator = substituteVariable("${/}");
    const result = substituteVariable("${fileDirname}");
    assert.isTrue(
      result?.includes(
        `c4-grammar${separator}extension${separator}src${separator}test${separator}resources`
      )
    );
    commands.executeCommand("workbench.action.closeActiveEditor");
    done();
  });

  test("${fileDirnameBasename} returns file's directory name", (done) => {
    workspace.openTextDocument(getFileUri("amazon_web_service.dsl"));
    const result = substituteVariable("${fileDirnameBasename}");
    assert.equal(result, "resources");
    commands.executeCommand("workbench.action.closeActiveEditor");
    done();
  });

  test("Substitute variable returns the provided variable", (done) => {
    workspace.openTextDocument(getFileUri("amazon_web_service.dsl"));
    const result = substituteVariable("test");
    assert.equal(result, "test");
    commands.executeCommand("workbench.action.closeActiveEditor");
    done();
  });
});
