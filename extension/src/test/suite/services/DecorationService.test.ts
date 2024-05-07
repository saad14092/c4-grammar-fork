import { commands, window, workspace } from "vscode";
import * as Sinon from "sinon";
import { assert } from "chai";

import { DecorationService } from "../../../services/DecorationService";
import { getFileUri } from "../../testHelper";

suite("Decoration Service tests", () => {
  test("Decorations should be triggered", (done) => {
    const service = new DecorationService(
      window.createTextEditorDecorationType({})
    );
    const spy = Sinon.spy(commands, "executeCommand");

    workspace
      .openTextDocument(getFileUri("amazon_web_service.dsl"))
      .then((document) => {
        window.showTextDocument(document).then(() => {
          service.triggerDecorations(undefined, document);
          assert.isTrue(
            spy.calledWith("c4-server.text-decorations", {
              uri: document.uri.path,
            })
          );
          done();
          spy.restore();
          commands.executeCommand("workbench.action.closeActiveEditor");
        });
      });
  });
});
