package dev.cozmoe.gradle

inline fun <T> T.applyIf(predicate: Boolean, block: T.() -> Unit): T =
    apply { if(predicate) { this.apply(block) } }

inline fun <T> T.applyIf(
    predicate: T.() -> Boolean,
    block: T.() -> Unit
): T = applyIf(predicate(this), block)