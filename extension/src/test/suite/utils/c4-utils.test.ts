import { assert } from "chai";
import { C4Utils } from "../../../utils/c4-utils";

suite("Utils tests", () => {
  suite("Get java version tests", () => {
    test("Should return the correct version", async () => {
      const version = 12;
      const calculatedVersion = C4Utils.getJavaVersion(
        `Test 123 version "${version}"`
      );
      assert.equal(calculatedVersion, version);
    });

    test("Should return '0'", async () => {
      const version = C4Utils.getJavaVersion('Test 123 versio"');
      assert.equal(version, 0);
    });
  });

  suite("Get server options tests", () => {
    const serverLauncher = "Test Launcher";
    const renderer = "Test Renderer";
    test("Should return server options for 'process-io' connetion", () => {
      const options = {
        run: {
          command: serverLauncher,
          options: {shell: true},
          args: ["-ir=" + renderer],
        },
        debug: {
          command: serverLauncher,
          options: {shell: true},
          args: ["-ir=" + renderer],
        },
      };

      const result = C4Utils.getServerOptions(
        "process-io",
        serverLauncher,
        renderer
      );
      assert.deepEqual(result, options);
    });
  });
});
