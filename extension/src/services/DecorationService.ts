import { DecorationOptions } from "vscode";
import { CommandResultTextDecorations, DecoratedRange } from "../types";

export class DecorationService {

    public toTextDecorations(fromLanguageServer: CommandResultTextDecorations): DecorationOptions[] {
        return fromLanguageServer.resultdata?.map( range => this.nameDecoration(range)) ?? []
    }

    private nameDecoration(decoRange: DecoratedRange): DecorationOptions {
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
}