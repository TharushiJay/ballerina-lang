import { Range } from "vscode";
// import { VersionedTextDocumentIdentifier } from 'vscode-languageserver-types';


export interface SemanticHighlightingInformation{
    line: number;
    token: string;
}

  export interface HighlightToken {
    scope: number;
    range: Range;
}

export interface SemanticHighlightingCapabilities{
    semanticHighlighting: boolean;
}

export interface SemanticHighlighting{
    scopes: string[][];
}

export interface SemanticHighlightingParams{
    textDocument: string;
    // versionedTextDocumentIdentifier: VersionedTextDocumentIdentifier;
    lines: SemanticHighlightingInformation[];
}
