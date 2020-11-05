# Build the language server

```
cd c4-language-server
./gradlew deployToVSCode
```

# Build the language client

```
cd extension
yarn
```

# Build everything and package the extesion (*.vsix)

```
yarn global add vsce
cd extension
yarn package
```
