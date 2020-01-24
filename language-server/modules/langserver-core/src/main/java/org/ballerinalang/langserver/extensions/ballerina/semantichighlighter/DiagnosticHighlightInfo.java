/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.ballerinalang.util.diagnostic.Diagnostic;

/**
 * Contains diagnostic based highlight information.
 *
 * @since 1.2.0
 */
public class DiagnosticHighlightInfo extends HighlightInfo {
    private Diagnostic diagnostic;

    public DiagnosticHighlightInfo(ScopeEnum scopeEnum, Diagnostic diagnostic) {
        super(scopeEnum);
        this.diagnostic = diagnostic;
    }

    @Override
    SemanticHighlightingToken getHighlightInfo() {
        int character = this.diagnostic.getPosition().getStartColumn() - 1;
        int length = this.diagnostic.getPosition().getEndColumn() - this.diagnostic.getPosition().getStartColumn();
        int scope = this.scopeEnum.getScopeId();

        return new SemanticHighlightingToken(character, length, scope);
    }

    @Override
    int getHighlightLine() {
        int line = this.diagnostic.getPosition().getStartLine() - 1;
        return line;
    }
}
