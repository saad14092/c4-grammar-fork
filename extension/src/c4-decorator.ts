import { DecorationOptions, Range  } from "vscode";

export type CommandResultTextDecorations = {
    resultcode: number;
    message: string;
    resultdata: Range[]
}

export function nameDecoration(range: Range): DecorationOptions {
    return {
        range,
        renderOptions : {
            before: {
                color: 'gray',
                fontStyle: 'italic',
                contentText: 'name: '
            }
        }
    }
}

export function toTextDecorations(fromLanguageServer: CommandResultTextDecorations): DecorationOptions[] {
    return fromLanguageServer.resultdata.map( range => nameDecoration(range))
}