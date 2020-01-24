/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.langserver.extensions.ballerina.semantichighlighter;

import com.google.common.primitives.Ints;
import org.ballerinalang.langserver.client.ExtendedLanguageClient;
import org.ballerinalang.langserver.compiler.CollectDiagnosticListener;
import org.ballerinalang.langserver.compiler.DocumentServiceKeys;
import org.ballerinalang.langserver.compiler.LSContext;
import org.ballerinalang.langserver.compiler.LSModuleCompiler;
import org.ballerinalang.langserver.compiler.exception.CompilationFailedException;
import org.ballerinalang.langserver.compiler.workspace.WorkspaceDocumentManager;
import org.ballerinalang.util.diagnostic.Diagnostic;
import org.ballerinalang.util.diagnostic.DiagnosticListener;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.util.CompilerContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Highlight provider for Semantic Highlighting.
 *
 * @since 1.2.0
 */
public class SemanticHighlightProvider {
    // Publishes the highlighting information
    public static void sendHighlights(ExtendedLanguageClient client, LSContext context,
                                      WorkspaceDocumentManager docManager)
            throws CompilationFailedException, HighlightingFailedException {
        client.publishTextHighlighting(getHighlights(context, docManager));
    }
    // Identifies the tokens to be highlighted
    public static SemanticHighlightingParams getHighlights(LSContext context, WorkspaceDocumentManager docManager)
            throws CompilationFailedException, HighlightingFailedException {

        // Identifies endpoint variables
        LSModuleCompiler.getBLangPackages(context, docManager, null, true, true, true);

        List<HighlightInfo> highlights = new ArrayList<>();
        context.put(SemanticHighlightingKeys.SEMANTIC_HIGHLIGHTING_KEY, highlights);

        SemanticHighlightingVisitor semanticHighlightingVisitor = new SemanticHighlightingVisitor(context);
        BLangPackage bLangPackage = context.get(DocumentServiceKeys.CURRENT_BLANG_PACKAGE_CONTEXT_KEY);
        //TODO Trace log update
        if (bLangPackage != null) {
            bLangPackage.accept(semanticHighlightingVisitor);
        }

        if (context.get(SemanticHighlightingKeys.SEMANTIC_HIGHLIGHTING_KEY) == null) {
            throw new HighlightingFailedException("Couldn't find any highlight information!");
        }

        // Identifies unused imports
        List<Diagnostic> diagnostics = new ArrayList<>();
        LSModuleCompiler.getBLangPackages(context, docManager, null, true, true, true);
        CompilerContext compilerContext = context.get(DocumentServiceKeys.COMPILER_CONTEXT_KEY);

        if (compilerContext.get(DiagnosticListener.class) instanceof CollectDiagnosticListener) {
            diagnostics = ((CollectDiagnosticListener) compilerContext.get(DiagnosticListener.class)).getDiagnostics();
        }

        diagnostics.forEach(diagnostic -> {
            if (diagnostic.getCode().getValue().equals("unused.import.module")) {
                DiagnosticHighlightInfo highlightInfo = new DiagnosticHighlightInfo(ScopeEnum.UNUSED, diagnostic);
                highlights.add(highlightInfo);
            }
        });

        Map<Integer, int[]> lineInfo = new HashMap<>();
        ArrayList<SemanticHighlightingInformation> highlightsArr = new ArrayList<>();
        context.get(SemanticHighlightingKeys.SEMANTIC_HIGHLIGHTING_KEY).forEach(element-> {
                int[] token = getToken(element);
                int line = getLine(element);
            if (lineInfo.get(line) != null) {
                int[] cur = lineInfo.get(line);
                lineInfo.put(line, Ints.concat(cur, token));
            } else {
                lineInfo.put(line, token);
            }
        });
        for (Map.Entry mapElement : lineInfo.entrySet()) {
            highlightsArr.add(getEncodedToken(mapElement, lineInfo));
        }
        return new SemanticHighlightingParams
                (context.get(DocumentServiceKeys.FILE_URI_KEY), highlightsArr);
    }

    private static int[] getToken(HighlightInfo element) {
        SemanticHighlightingToken highlightingToken = element.getHighlightInfo();
        int[] token = {highlightingToken.getCharacter(),
                highlightingToken.getLength(), highlightingToken.getScope()};
        return token;

    }

    private static int getLine(HighlightInfo element) {
        return element.getHighlightLine();

    }

    private static SemanticHighlightingInformation getEncodedToken(Map.Entry mapElement, Map<Integer, int[]> lineInfo) {
        Integer key = (Integer) mapElement.getKey();
        String tokenArr = Arrays.toString(lineInfo.get(key));
        String encodedToken = Base64.getEncoder()
                .encodeToString(tokenArr.getBytes(StandardCharsets.UTF_8));
        SemanticHighlightingInformation highlightingInformation
                = new SemanticHighlightingInformation(key, encodedToken);
        return highlightingInformation;
    }
}
