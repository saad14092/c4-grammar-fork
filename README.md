# Build the language server

```
cd c4-language-server
./gradlew deployToVSCode
```

# Build the VS Code Extension

```
cd extension
yarn
```

# Currently supported DSL Elements

- [Grammar](#grammar)
	- [!include](#include) :x:
	- [workspace](#workspace):white_check_mark:
		- [model](#model):white_check_mark:
			- [enterprise](#enterprise) :white_check_mark:
			- [person](#person) :white_check_mark:
				- [url](#url) :x:
				- [properties](#properties) :x:
			- [softwareSystem](#softwareSystem) :white_check_mark:
				- [url](#url) :x:
				- [properties](#properties) :x:
				- [container](#container):white_check_mark:
					- [url](#url) :x:
					- [properties](#properties) :x:
					- [component](#component):white_check_mark:
						- [url](#url) :x:
						- [properties](#properties) :x:
			- [deploymentEnvironment](#deploymentEnvironment):x:
				- [deploymentNode](#deploymentNode):x:
					- [url](#url):x:
					- [properties](#properties):x:
					- [infrastructureNode](#infrastructureNode):x:
						- [url](#url):x:
						- [properties](#properties):x:
					- [softwareSystemInstance](#softwareSystemInstance):x:
					- [containerInstance](#containerInstance):x:
			- [-> (relationship)](#relationship) :white_check_mark:
				- [url](#url) :x:
		- [views](#views):white_check_mark:
			- [systemLandscape](#systemLandscape-view) :white_check_mark:
				- [include](#include) :white_check_mark:
				- [exclude](#exclude) :white_check_mark:
				- [autoLayout](#autoLayout) :white_check_mark:
				- [animationStep](#animationStep) :white_check_mark:
				- [title](#title) :white_check_mark:
			- [systemContext](#systemContext-view) :white_check_mark:
				- [include](#include) :white_check_mark:
				- [exclude](#exclude) :white_check_mark:
				- [autoLayout](#autoLayout) :white_check_mark:
				- [animationStep](#animationStep) :white_check_mark:
				- [title](#title) :white_check_mark:
			- [container](#container-view) :white_check_mark:
				- [include](#include) :white_check_mark:
				- [exclude](#exclude) :white_check_mark:
				- [autoLayout](#autoLayout) :white_check_mark:
				- [animationStep](#animationStep) :white_check_mark:
				- [title](#title) :white_check_mark:
			- [component](#component-view) :white_check_mark:
				- [include](#include) :white_check_mark:
				- [exclude](#exclude) :white_check_mark:
				- [autoLayout](#autoLayout) :white_check_mark:
				- [animationStep](#animationStep) :white_check_mark:
				- [title](#title) :white_check_mark:
			- [filtered](#filtered-view) :x:
			- [dynamic](#dynamic-view) :x:
				- [autoLayout](#autoLayout) :x:
				- [title](#title) :x:
			- [deployment](#deployment-view) :x:
				- [include](#include) :x:
				- [exclude](#exclude) :x:
				- [autoLayout](#autoLayout) :x:
				- [animationStep](#animationStep) :x:
				- [title](#title) :x:
			- [styles](#styles) :white_check_mark:
				- [element](#element-style) :white_check_mark:
				- [relationship](#relationship-style) :white_check_mark:
			- [themes](#themes) :x:
			- [branding](#branding) :x:
		- [configuration](#configuration) :x:
			- [users](#users) :x:

