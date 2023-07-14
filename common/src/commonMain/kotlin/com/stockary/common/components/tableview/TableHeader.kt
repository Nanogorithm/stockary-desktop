package com.helloanwar.common.ui.components.tableview

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class TableHeader(
    val headerText: String, val columnIndex: Int
)


fun getTableHeader(annotations: List<Annotation>): TableHeader {
    val header = annotations.firstOrNull { a -> a is TableHeader }
        ?: throw IllegalArgumentException("Given annotations don't contain a TableHeader")
    return header as TableHeader
}

sealed interface ColumnType {
    data class Image(val src: String?) : ColumnType
}