/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.impl

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.impl.FirClassImpl
import org.jetbrains.kotlin.fir.resolve.FirScopeProvider
import org.jetbrains.kotlin.fir.resolve.buildSubstitutionScope
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.scopes.FirScope
import org.jetbrains.kotlin.fir.scopes.impl.FirClassDeclaredMemberScope
import org.jetbrains.kotlin.fir.scopes.impl.FirClassUseSiteScope
import org.jetbrains.kotlin.fir.scopes.impl.FirCompositeScope
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeClassErrorType

class FirRegularScopeProvider : FirScopeProvider {
    override fun getUseSiteMemberScope(klass: FirRegularClass, useSiteSession: FirSession): FirScope {
        if (klass !is FirClassImpl) return FirScopeProvider.emptyScope

        val superTypeScope = FirCompositeScope(mutableListOf())
        val declaredScope = FirClassDeclaredMemberScope(klass, useSiteSession)
        lookupSuperTypes(klass, lookupInterfaces = true, deep = false, useSiteSession = useSiteSession)
            .mapNotNullTo(superTypeScope.scopes) { useSiteSuperType ->
                if (useSiteSuperType is ConeClassErrorType) return@mapNotNullTo null
                val symbol = useSiteSuperType.lookupTag.toSymbol(useSiteSession)
                if (symbol is FirClassSymbol) {
                    val useSiteScope = getUseSiteMemberScope(symbol.fir, useSiteSession)
                    useSiteSuperType.buildSubstitutionScope(useSiteScope, symbol.fir) ?: useSiteScope
                } else {
                    null
                }
            }
        return FirClassUseSiteScope(useSiteSession, superTypeScope, declaredScope)
    }

}