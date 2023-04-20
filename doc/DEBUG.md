# Build

## Build the language server

```
cd extension
yarn build-server
```

## Build the language client

```
cd extension
yarn build
```

## Build everything and package the extension (*.vsix)

```
yarn global add vsce
cd extension
yarn package
```

# Debug

Uninstall or disable an existing c4-dsl-extension-<x.x.x>.vsix, in order to guarantee the language-server is not used from an installed extension, but from the development sources.

## Debug the language client only

As default behaviour the language client (i.e. the VS Code extension) launches the language server and connects to it automatically. 

1. Check if the language server code was already built, i.e. the folder extension/server/c4-language-server/lib must contain the jar files. In case it is empty force a build with

    ```
    cd extension
    yarn build-server
    ```

2. Change the setting _c4.languageserver.connectiontype_ to _socket_ or _process-io_

3. Launch the Language Client from within VS Code. Goto _RUN AND DEBUG_ and select _C4 DSL Extension_

4. A new VS Code instance is started. Open a *.dsl file there.

## Debug the language server and the language client

If you want to debug the language server you must start it separately and connect the client afterwards.

Therefore Following steps need to be done:

1. Check if the language server code was already built, i.e. the folder extension/server/c4-language-server/lib must contain the jar files. In case it is empty force a build with

    ```
    cd extension
    yarn build-server
    ```

2. Change the setting _c4.languageserver.connectiontype_ to _socket-debug_

3. Launch the Language Server from within VS Code. Goto _RUN AND DEBUG_ and select _C4 DSL Language Server_

4. Wait until you see following logs in the Terminal

    ```
    11:36:12.960 [main] INFO  d.s.c.ls.C4LanguageServerLauncher - Starting Socket Connection
    11:36:12.985 [main] INFO  d.s.c.ls.C4LanguageServerLauncher - READY_TO_CONNECT
    ```

5. Launch the Language Client from within VS Code. Goto _RUN AND DEBUG_ and select _C4 DSL Extension_

6. A new VS Code instance is started. Open a *.dsl file there

7. Client and Server should connect


