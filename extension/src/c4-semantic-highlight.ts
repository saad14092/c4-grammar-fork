import { CancellationToken, DocumentSemanticTokensProvider, SemanticTokensBuilder, ProviderResult, SemanticTokens, TextDocument, SemanticTokensLegend } from 'vscode';

const tokenTypes = [
    'comment', 'string', 'keyword', 'number', 'regexp', 'operator', 'namespace',
    'type', 'struct', 'class', 'interface', 'enum', 'typeParameter', 'function',
    'member', 'macro', 'variable', 'parameter', 'property', 'label', 'event'
];

const tokenModifiers = [
    'declaration', 'definition', 'readonly', 'static', 'deprecated',
    'abstract', 'async', 'modification', 'documentation', 'defaultLibrary'
];

const ID_IDX = tokenTypes.indexOf('member')
const NAME_IDX = tokenTypes.indexOf('macro')
//const DESCRIPTION_IDX = tokenTypes.indexOf('operator')
const TECHNOLOGY_IDX = tokenTypes.indexOf('regexp')
const TAGS_IDX = tokenTypes.indexOf('label')

export const c4Legend = new SemanticTokensLegend(tokenTypes, tokenModifiers);

export class C4SemanticTokenProvider implements DocumentSemanticTokensProvider {
    
    multiLineComment = false

    highlighters: C4SemanticHighlighter[] = [
            new ModelElementHighlighterWithTechnology(),
            new ModelElementHighlighterWithoutTechnology(),
            new RelationShipHighlighter(),
            new InstanceHighlighter(),
            new BasicViewHighlighter(),
            new DynamicAndDeploymentViews(),
            new ConstantHighlighter()
        ];

    ignoreLine(line: string): boolean {

        if(!this.multiLineComment && line.startsWith('/*')) {
            this.multiLineComment = true
        }

        const result = this.multiLineComment || line.length <= 1 || line.startsWith('#') || line.startsWith('//')

        if(this.multiLineComment && line.endsWith('*/')) {
            this.multiLineComment = false
        }

        return result
    }

    provideDocumentSemanticTokens(document: TextDocument, token: CancellationToken): ProviderResult<SemanticTokens> {

        const builder = new SemanticTokensBuilder();
        const text = document.getText();

        const lines = text.split(/\r\n|\r|\n/);
        this.multiLineComment = false

        lines.forEach( (line, index) => {

            // nothing to highlight in case of an empty line
            if(!this.ignoreLine(line.trim())) {
                var applied = false

                this.highlighters.some( ( highlighter) => {
    
                    highlighter.assignHighlights(line).forEach( (token) => {
                        builder.push( index, token.start, token.length, token.type);
                        // break after first highlighter matched
                        applied = true
                    });    
                    
                    return applied
                })    
            }

        });

        return builder.build();
    }
        
}

export type SemanticToken = {
    start: number
    length: number
    type: number
    modifier?: number
}

abstract class C4SemanticHighlighter {

    abstract pattern: RegExp;

    calculateToken(line: string, token: string, fromIdx: number, _type: number, _modifier?: number): SemanticToken {
        return { start: line.indexOf(token, fromIdx), length: token.length, type: _type, modifier: _modifier }
    }

    abstract assignHighlights(text: string): Array<SemanticToken> 

}

class ModelElementHighlighterWithTechnology extends C4SemanticHighlighter {

    pattern: RegExp = /([a-zA-Z0-9_]*)(?:\s*=\s*)?(?:container|component|deploymentNode|infrastructureNode)\s*(\"(?:[^\"]+)\")\s*(\"(?:[^\"]*)\")?\s*(\"(?:[^\"]*)\")?\s*(\"(?:[^\"]*)\")?.*/

    assignHighlights(text: string): Array<SemanticToken> {

        const matched = text.match(this.pattern)
        const tokens: SemanticToken[] = [];
        var fromIdx = 0

        if(matched !== null) {
            if(matched[1]) {
                tokens.push( this.calculateToken(text, matched[1], fromIdx, ID_IDX ))
                fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
            }
            if(matched[2]) {
                tokens.push( this.calculateToken(text, matched[2], fromIdx, NAME_IDX, 0 ))
                fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
            }
            if(matched[3]) {
                fromIdx = text.indexOf(matched[3], fromIdx) + matched[3].length
            }
            if(matched[4]) {
                tokens.push( this.calculateToken(text, matched[4], fromIdx, TECHNOLOGY_IDX ))
                fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
            }
            if(matched[5]) {
                tokens.push( this.calculateToken(text, matched[5], fromIdx, TAGS_IDX ))
            }
        }

        return tokens;
    }
}

class ModelElementHighlighterWithoutTechnology extends C4SemanticHighlighter {

    pattern: RegExp = /([a-zA-Z0-9_]*)(?:\s*=\s*)?(?:person|software[s,S]ystem|workspace|enterprise)\s*(\"(?:[^\"]+)\")\s*(\"(?:[^\"]*)\")?\s*(\"(?:[^\"]*)\")?.*/

    assignHighlights(text: string): Array<SemanticToken> {

        const matched = text.match(this.pattern)
        const tokens: SemanticToken[] = [];
        var fromIdx = 0

        if(matched !== null) {
            if(matched[1]) {
                tokens.push( this.calculateToken(text, matched[1], fromIdx, ID_IDX ))
                fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
            }
            if(matched[2]) {
                tokens.push( this.calculateToken(text, matched[2], fromIdx, NAME_IDX ))
                fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
            }
            if(matched[3]) {
                fromIdx = text.indexOf(matched[3], fromIdx) + matched[3].length
            }
            if(matched[4]) {
                tokens.push( this.calculateToken(text, matched[4], fromIdx, TAGS_IDX ))
            }
        }

        return tokens;
    }
}

class RelationShipHighlighter extends C4SemanticHighlighter {

    pattern: RegExp = /([a-zA-Z0-9_]*)\s*\-\>\s*([a-zA-Z0-9_]*)\s*(\"(?:[^\"]*)\")?\s*(\"(?:[^\"]*)\")?\s*(\"(?:[^\"]*)\")?.*/

    assignHighlights(text: string): Array<SemanticToken> {

        const matched = text.match(this.pattern)
        const tokens: SemanticToken[] = [];
        var fromIdx = 0

        if(matched !== null) {
            if(matched[1]) {
                tokens.push( this.calculateToken(text, matched[1], fromIdx, ID_IDX ))
                fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
            }
            if(matched[2]) {
                tokens.push( this.calculateToken(text, matched[2], fromIdx, ID_IDX ))
                fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
            }
            if(matched[3]) {
                fromIdx = text.indexOf(matched[3], fromIdx) + matched[3].length
            }
            if(matched.length >= 5) {
                if(matched[4]) {
                    tokens.push( this.calculateToken(text, matched[4], fromIdx, TECHNOLOGY_IDX ))
                    fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
                }
            } 
            if(matched.length >= 6) {
                if(matched[5]) {
                    tokens.push( this.calculateToken(text, matched[5], fromIdx, TAGS_IDX ))
                }
            } 
        }

        return tokens;
    }

}

class InstanceHighlighter extends C4SemanticHighlighter {

    pattern: RegExp = /([a-zA-Z0-9_]*)(?:\s*=\s*)?(?:containerInstance|softwareSystemInstance)\s*([a-zA-Z0-9_]+)\s*(\"(?:[^\"]*)\")?.*/

    assignHighlights(text: string): SemanticToken[] {
        const matched = text.match(this.pattern)
        const tokens: SemanticToken[] = [];
        var fromIdx = 0

        if(matched !== null) {
            if(matched[1]) {
                tokens.push( this.calculateToken(text, matched[1], fromIdx, ID_IDX ))
                fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
            }
            if(matched[2]) {
                tokens.push( this.calculateToken(text, matched[2], fromIdx, ID_IDX ))
                fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
            }
            if(matched[3]) {
                tokens.push( this.calculateToken(text, matched[3], fromIdx, TAGS_IDX ))
            }
        }

        return tokens;
   }
    
}

class BasicViewHighlighter extends C4SemanticHighlighter {

    pattern: RegExp = /(?:system[l,L]andscape|system[c,C]ontext|container|component)\s+([a-zA-Z0-9_]*)\s*(\"(?:[^\"]+)\")\s*(\"(?:[^\"]*)\")?.*/

    assignHighlights(text: string): SemanticToken[] {

        const matched = text.match(this.pattern)
        const tokens: SemanticToken[] = [];
        var fromIdx = 0

        if(matched !== null) {
            if(matched[1]) {
                tokens.push( this.calculateToken(text, matched[1], fromIdx, ID_IDX ))
                fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
            }
            if(matched[2]) {
                tokens.push( this.calculateToken(text, matched[2], fromIdx, NAME_IDX ))
            }
        }

        return tokens;
    }
    
}

class DynamicAndDeploymentViews extends C4SemanticHighlighter {

    pattern: RegExp = /(?:deployment|dynamic)\s+(\*|[a-zA-Z0-9_]+)\s*(\"(?:[^\"]+)\")\s*(\"(?:[^\"]+)\")\s*(\"(?:[^\"]*)\")?.*/
    
    assignHighlights(text: string): SemanticToken[] {

        const matched = text.match(this.pattern)
        const tokens: SemanticToken[] = [];
        var fromIdx = 0

        if(matched !== null) {

            if(text.trim().startsWith('dynamic')) {
                if(matched[1]) {
                    tokens.push( this.calculateToken(text, matched[1], fromIdx, ID_IDX ))
                    fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
                }
                if(matched[2]) {
                    tokens.push( this.calculateToken(text, matched[2], fromIdx, NAME_IDX ))
                }    
            }
            else {
                if(matched[1]) {
                    tokens.push( this.calculateToken(text, matched[1], fromIdx, ID_IDX ))
                    fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
                }
                if(matched[2]) {
                    tokens.push( this.calculateToken(text, matched[2], fromIdx, ID_IDX ))
                    fromIdx = tokens.slice(-1)[0].start + tokens.slice(-1)[0].length 
                }    
                if(matched[3]) {
                    tokens.push( this.calculateToken(text, matched[3], fromIdx, NAME_IDX ))
                }    
            }

        }

        return tokens;
    }
    
}

class ConstantHighlighter extends C4SemanticHighlighter {

//    pattern: RegExp = /(?:deployment|dynamic)\s+(\*|[a-zA-Z0-9_]+)\s*(\"(?:[^\"]+)\")\s*(\"(?:[^\"]+)\")\s*(\"(?:[^\"]*)\")?.*/

    pattern: RegExp = /(?:\!constant)\s+(\*|[a-zA-Z0-9_]+)\s+(\"(?:[^\"]+)\").*/;

    assignHighlights(text: string): SemanticToken[] {

        const matched = text.match(this.pattern)
        const tokens: SemanticToken[] = [];
        var fromIdx = 0

        if(matched !== null) {
            if(matched[1]) {
                tokens.push( this.calculateToken(text, matched[1], fromIdx, ID_IDX ))
            }
       }

        return tokens;
    }
}