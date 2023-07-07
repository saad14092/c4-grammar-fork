import { CommandResultCode } from "./CommandResultCode";
import { DecoratedRange } from "./DecoratedRange";

export type CommandResultTextDecorations = CommandResultCode & {
    resultdata?: DecoratedRange[]
}