package com.swift.android.ktlint

import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider

class CustomRuleSetProvider : RuleSetProvider {

    override fun get() = RuleSet(
        "ktlint",
        SortRule()
    )
}