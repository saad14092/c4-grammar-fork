import * as path from "path";
import * as Mocha from "mocha";
import * as glob from "glob";

const mocha = new Mocha({
  ui: "tdd",
});

export function run(): Promise<void> {
  const testDir = getTestDir();

  return new Promise((resolve, reject) => {
    glob("**/**.test.js", { cwd: testDir }, (err, files) => {
      if (err) {
        return reject(err);
      }
      addTestFilesToMocha(files);
      try {
        runTests(resolve, reject);
      } catch (err) {
        console.error(err);
        reject(err);
      }
    });
  });
}

const addTestFilesToMocha = (files: Array<string>): void => {
  const testDir = getTestDir();
  files.forEach((file) => mocha.addFile(path.resolve(testDir, file)));
};

const getTestDir = (): string => {
  return path.resolve(__dirname, "..");
};

const runTests = (
  resolve: (value: void | PromiseLike<void>) => void,
  reject: (reason?: any) => void
): void => {
  mocha.run((failures) => {
    if (failures > 0) {
      reject(new Error(`${failures} tests failed.`));
    } else {
      resolve();
    }
  });
};
