# The extension is ready to be developed with GitPod

[![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://gitlab.com/systemticks/c4-grammar)


# Build the language server

```
cd extension
yarn build-server
```

# Build the language client

```
cd extension
yarn build
```

# Build everything and package the extension (*.vsix)

```
yarn global add vsce
cd extension
yarn package
```
