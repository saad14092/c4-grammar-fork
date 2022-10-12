import { DecorationOptions, Range  } from "vscode";

type DecoratedRange = {
    type: string
    range: Range
}

export type CommandResultTextDecorations = {
    resultcode: number;
    message: string;
    resultdata: DecoratedRange[]
}

export function nameDecoration(decoRange: DecoratedRange): DecorationOptions {
    return {
        range: decoRange.range,
        renderOptions : {
            before: {
                color: 'gray',
                fontStyle: 'italic',
                contentText: decoRange.type
            }
        }
    }
}

export function toTextDecorations(fromLanguageServer: CommandResultTextDecorations): DecorationOptions[] {
    return fromLanguageServer.resultdata.map( range => nameDecoration(range))
}