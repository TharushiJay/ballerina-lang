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

import org.wso2.ballerinalang.compiler.tree.BLangIdentifier;

/**
 * Contains symbol based highlight information.
 *
 * @since 1.2.0
 */
public class SymbolHighlightInfo extends HighlightInfo {
    private BLangIdentifier identifier;

    SymbolHighlightInfo(ScopeEnum scopeEnum, BLangIdentifier identifier) {
        super(scopeEnum);
        this.identifier = identifier;
    }

    @Override
    SemanticHighlightingToken getHighlightInfo() {
        int character = this.identifier.pos.sCol - 1;
        int length = this.identifier.pos.eCol - this.identifier.pos.sCol;
        int scope = this.scopeEnum.getScopeId();

        return new SemanticHighlightingToken(character, length, scope);
    }

    @Override
    int getHighlightLine() {
        int line = this.identifier.pos.sLine - 1;
        return line;
    }
}
