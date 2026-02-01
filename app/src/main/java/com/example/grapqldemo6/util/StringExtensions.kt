package com.example.grapqldemo6.util

/**
 * String? 类型的扩展函数，用于判断字符串是否为 null
 */
fun String?.isNull(): Boolean = this == null

/**
 * String? 类型的扩展函数，用于判断字符串是否不为 null
 */
fun String?.isNotNull(): Boolean = this != null

/**
 * String? 类型的扩展函数，用于判断字符串是否为 null 或空
 */
fun String?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()

/**
 * String? 类型的扩展函数，用于判断字符串是否不为 null 且不为空
 */
fun String?.isNotNullOrEmpty(): Boolean = this != null && this.isNotEmpty()
