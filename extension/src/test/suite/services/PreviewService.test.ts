import { commands, window, workspace } from "vscode";
import * as Sinon from "sinon";
import { assert } from "chai";
import axios from "axios";

import { PreviewService } from "../../../services/PreviewService";
import { getFileUri } from "../../testHelper";

suite("Preview Service tests", () => {
  suite("Update web view tests", () => {
    test("New panel should be created and render service should be called", (done) => {
      const plantumlRendererUri = workspace
        .getConfiguration()
        .get("c4.show.plantuml.server") as string;
      const service = new PreviewService(
        plantumlRendererUri.concat("/plantuml/svg/"),
        "UML",
        "PlantUML Preview"
      );
      workspace
        .openTextDocument(getFileUri("amazon_web_service.dsl"))
        .then((document) => {
          const windowSpy = Sinon.spy(window, "createWebviewPanel");
          const axiosSpy = Sinon.spy(axios, "get");
          const encodedeString =
            "rLTHRzCm47xFhpYDLnRQ8GIQsegk6m8q42oRzh0XT4kkhJN73hRJRY3-EsSlRLDMsBe564cKAFQT_TtvxZxxfNLeN9dBo983ImKQTDg0qeeY9vmaI0eZr0W-a1j8eKGAcx1BXTINEId7F_jYGb4aQUZ0QJ1YD7QG2KEf4rf5aJqNoe-QmxS808tHqrTQkKHy9MYtk2cZ8PRI7TA5wqinKc7Ob5zak7Tgi3WMcHl3LijBvtZnZgp54LKZjABlKJGM6O5b3TBkig3wh1u8gf6aJiSZZVivVjNgcCuICXEHaesFaZsqhXttIwTjYcpaQ6HqMLIYUuEjNY-0ty7qtFUeRA0bE-dHC5pSuosgsbhXyjBQP6IgrXVXujPAtIWoR14ts35cUkezF4HfYQsv4_RGSx2RF5Ye_u4FVrh_VuKuGuTdQDTOzFZTPV9nFzwj53-txm_zRClVZVbsJ_97fFiQinsKg58o3MByqqbrvT-77y47kdJqxEc3z-p_LmiMN12pUByef4ZH4roZkNOBOwAv3R1nXGaO4CmCE5KToxoutMxrJcei_LvdzAdpn7VrDk0t9HejhEuBDjRcFaeslQSss_UQiHOGlvnv9XCGB66exES4RGJ44edU26SXNG5RWXOAHiEevbLe3MnNEdE4C2SNsEXsPtnHnpdWO4N10RWD1oH3YADSQDbqJ3dEq9-gKvNmTf73GwYXDdcGPp-Cq6G-QjsOO4AyBnCAZK1b7uUcL0fs1F-Wodo3C52Gi_7wtbZclBKTKrElFCGPLllKLhJR3TKLdZsjk-kD6XeUs9IfAmsjkxIBUdqpyahsEgoAMi5pAyp8jJcCgvdxPhmhzAxbxwB8GgYo3INCzNtSBGBZ9wLLILtL6oQ1FmDqwS2nFkI5UYtVbqB5CqMDktwHzsznWY4pgvFVZjQkdl5zAVNK0kNURkBSvsFVHD0qwEIe-6pszyh1MZnz7vzU6sz5mU0NFyrD0V79e-SjVpzsOv6UApxvThS-nRsxbpdOXerEwlSTszNGlGF2pCBGw3mimDG85nQLmsDPy2fQNlHEtkxk39uaoVuyrgFR5QyxsxEYcAmmv9KsKuuqon7tfIJhVc_4wyF33ycjBLY5uCwhiqO4D6pbIr9PcSiV";
          service.currentDiagram = "AmazonWebServicesDeployment";
          service.currentDocument = document;
          service.updateWebView(encodedeString).then(() => {
            assert.isTrue(windowSpy.called);
            assert.isTrue(
              axiosSpy.calledWith(
                plantumlRendererUri
                  .concat("/plantuml/svg/")
                  .concat(encodedeString)
              )
            );
            windowSpy.restore();
            axiosSpy.restore();
            commands.executeCommand("workbench.action.closeActiveEditor");
          });
          done();
        });
    });
  });

  suite("Trigger refresh tests", () => {
    const plantumlRendererUri = workspace
      .getConfiguration()
      .get("c4.show.plantuml.server") as string;
    const service = new PreviewService(
      plantumlRendererUri.concat("/plantuml/svg/"),
      "UML",
      "PlantUML Preview"
    );

    test("Should not trigger refresh", (done) => {
      const spy = Sinon.spy(commands, "executeCommand");
      workspace
        .openTextDocument(getFileUri("amazon_web_service.dsl"))
        .then((document) => {
          service.triggerRefresh(document, "plantuml");
          assert.isFalse(spy.calledWith("c4.refresh"));
          done();
          spy.restore();
          commands.executeCommand("workbench.action.closeActiveEditor");
        });
    });

    test("Should trigger refresh", (done) => {
      const spy = Sinon.spy(commands, "executeCommand");
      workspace
        .openTextDocument(getFileUri("amazon_web_service.dsl"))
        .then((document) => {
          const encodedeString =
            "rLTHRzCm47xFhpYDLnRQ8GIQsegk6m8q42oRzh0XT4kkhJN73hRJRY3-EsSlRLDMsBe564cKAFQT_TtvxZxxfNLeN9dBo983ImKQTDg0qeeY9vmaI0eZr0W-a1j8eKGAcx1BXTINEId7F_jYGb4aQUZ0QJ1YD7QG2KEf4rf5aJqNoe-QmxS808tHqrTQkKHy9MYtk2cZ8PRI7TA5wqinKc7Ob5zak7Tgi3WMcHl3LijBvtZnZgp54LKZjABlKJGM6O5b3TBkig3wh1u8gf6aJiSZZVivVjNgcCuICXEHaesFaZsqhXttIwTjYcpaQ6HqMLIYUuEjNY-0ty7qtFUeRA0bE-dHC5pSuosgsbhXyjBQP6IgrXVXujPAtIWoR14ts35cUkezF4HfYQsv4_RGSx2RF5Ye_u4FVrh_VuKuGuTdQDTOzFZTPV9nFzwj53-txm_zRClVZVbsJ_97fFiQinsKg58o3MByqqbrvT-77y47kdJqxEc3z-p_LmiMN12pUByef4ZH4roZkNOBOwAv3R1nXGaO4CmCE5KToxoutMxrJcei_LvdzAdpn7VrDk0t9HejhEuBDjRcFaeslQSss_UQiHOGlvnv9XCGB66exES4RGJ44edU26SXNG5RWXOAHiEevbLe3MnNEdE4C2SNsEXsPtnHnpdWO4N10RWD1oH3YADSQDbqJ3dEq9-gKvNmTf73GwYXDdcGPp-Cq6G-QjsOO4AyBnCAZK1b7uUcL0fs1F-Wodo3C52Gi_7wtbZclBKTKrElFCGPLllKLhJR3TKLdZsjk-kD6XeUs9IfAmsjkxIBUdqpyahsEgoAMi5pAyp8jJcCgvdxPhmhzAxbxwB8GgYo3INCzNtSBGBZ9wLLILtL6oQ1FmDqwS2nFkI5UYtVbqB5CqMDktwHzsznWY4pgvFVZjQkdl5zAVNK0kNURkBSvsFVHD0qwEIe-6pszyh1MZnz7vzU6sz5mU0NFyrD0V79e-SjVpzsOv6UApxvThS-nRsxbpdOXerEwlSTszNGlGF2pCBGw3mimDG85nQLmsDPy2fQNlHEtkxk39uaoVuyrgFR5QyxsxEYcAmmv9KsKuuqon7tfIJhVc_4wyF33ycjBLY5uCwhiqO4D6pbIr9PcSiV";
          service.currentDiagram = "AmazonWebServicesDeployment";
          service.currentDocument = document;
          service.updateWebView(encodedeString).then(() => {
            service.triggerRefresh(document, "plantuml");
            assert.isTrue(spy.calledWith("c4.refresh"));
            spy.restore();
            commands.executeCommand("workbench.action.closeActiveEditor");
          });
          done();
        });
    });
  });
});
