import * as assert from "assert";
import { extensions } from "vscode";

suite("Extension Tests", () => {
  test("Extension should be present", () => {
    assert.notEqual(
      extensions.getExtension("systemticks.c4-dsl-extension"),
      null
    );
  });

  test("should activate", function () {
    this.timeout(1 * 60 * 1000);
    return extensions
      .getExtension("systemticks.c4-dsl-extension")
      ?.activate()
      .then((api) => {
        assert.ok(true);
      });
  });
});
