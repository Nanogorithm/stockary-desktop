package com.stockary.common.ui.summary

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter
import com.stockary.common.repository.order.model.Order
import com.stockary.common.repository.order.model.OrderSummaryTable
import com.stockary.common.toCurrencyFormat
import java.awt.Color
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


fun String.appDataDir(): String {
    val prefix = System.getProperty("user.home").toString()
    val file = File(prefix, "stockary" + File.separatorChar + this)
    if (!file.parentFile.exists()) {
        file.parentFile.mkdirs()
    }
    if (!file.exists()) {
        file.mkdirs()
    }
    return file.absolutePath
}

fun pdfInvoiceForSummary(
    fileName: String,
    orders: List<OrderSummaryTable>,
    specialCategories: List<OrderSummaryTable>
) {
    val invoiceDir = File("invoices".appDataDir())

    // 1) Create a FileOutputStream object with the path and name of the file
    val pdfOutputFile = FileOutputStream(
        File(
            invoiceDir, "${fileName}.pdf"
        )
    )

    val myPDFDoc = Document(PageSize.A4, 40f, 40f, 80f, 80f)

    val footer = Rectangle(30f, 30f, PageSize.A4.getRight(30f), 140f).apply {
        border = Rectangle.BOX
        borderColor = Color.BLACK
        borderWidth = 2f
    }

    val header = Rectangle(30f, 30f, PageSize.A4.getRight(30f), 140f).apply {
        border = Rectangle.BOX
        borderColor = Color.BLUE
        borderWidth = 1f
        top = PageSize.A4.getTop(30f)
        bottom = PageSize.A4.getTop(180f)
    }

    val title = "Order Summary"

    val pdfWriter = PdfWriter.getInstance(myPDFDoc, pdfOutputFile).apply {

        pageEvent = object : PdfPageEventHelper() {

            override fun onEndPage(writer: PdfWriter, doc: Document) {
                with(writer.directContent) {
//                    rectangle(header)
//                    rectangle(footer)
                }
            }
        }
    }

    val myTable = Table(3).apply {
        padding = 2f
        spacing = 1f
        width = 100f
    }

    listOf("Category", "Item", "Total Unit").forEach {
        val current = Cell(Phrase(it)).apply {
            isHeader = true
            backgroundColor = Color.LIGHT_GRAY
        }
        myTable.addCell(current)
    }

    var previousCategory: String? = null

    orders.sortedBy { it.categoryName }.forEach { order ->
        myTable.apply {
            val category = if (previousCategory != order.categoryName) {
                previousCategory = order.categoryName
                order.categoryName
            } else ""

            addCell(
                Cell(Phrase(category)).apply {
                    border = Cell.BOTTOM + Cell.RIGHT
                }
            )
            addCell(
                Cell(Phrase(order.productName)).apply {
                    border = Cell.BOTTOM + Cell.RIGHT
                }
            )
            addCell(
                Cell(Phrase("${order.totalUnit} ${order.unitName}")).apply {
                    border = Cell.BOTTOM
                }
            )
        }
    }

    //1) Create a pdf object with using the class
    //  import com.lowagie.text.Image and the method getInstance
    //  with the url https://kesizo.github.io/assets/images/kesizo-logo-6-832x834.png

    val image =
        Image.getInstance("https://firebasestorage.googleapis.com/v0/b/stockary-33aef.appspot.com/o/ReactJs.png?alt=media&token=04f6af40-0310-48a0-a7ea-0eb8d99edd58")
    image.scaleAbsolute(150f, 150f);

    myPDFDoc.apply {
        addTitle("Order Summary $fileName")
        addSubject("This is a tutorial explaining how to use openPDF")
        addKeywords("Kotlin, OpenPDF, Basic sample")
        addCreator("Stockary")
        addAuthor("Stockary")

        open()

        add(
            Paragraph(
                title,
                Font(Font.COURIER, 20f, Font.BOLDITALIC, Color.BLACK)
            ).apply {
                alignment = Element.ALIGN_CENTER
            }
        )

        add(Paragraph(Chunk.NEWLINE))
        add(myTable)
        add(Paragraph(Chunk.NEWLINE))

        // 2) Add the picture to the pdf
//        add(image)

        //add new page for print for department wise
        orders.filter { it.categoryName != "Birthday Cake" }.groupBy { it.categoryName }.forEach {
            newPage()
            //add category name
            add(
                Paragraph(
                    it.key,
                    Font(Font.COURIER, 20f, Font.BOLDITALIC, Color.BLACK)
                ).apply {
                    alignment = Element.ALIGN_CENTER
                }
            )

            val ordersByCategory = it.value

            val byCategoryTable = Table(2).apply {
                padding = 2f
                spacing = 1f
                width = 100f
            }

            listOf("Item", "Total Unit").forEach {
                val current = Cell(Phrase(it)).apply {
                    isHeader = true
                    backgroundColor = Color.LIGHT_GRAY
                }
                byCategoryTable.addCell(current)
            }

            ordersByCategory.forEach { order ->
                byCategoryTable.apply {
                    addCell(
                        Cell(
                            Phrase(order.productName)
                        ).apply {
                            border = if (order.note != null) Cell.RIGHT else Cell.BOTTOM + Cell.RIGHT
                        }
                    )
                    addCell(
                        Cell(Phrase("${order.totalUnit} ${order.unitName}")).apply {
                            border = if (order.note != null) Cell.NO_BORDER else Cell.BOTTOM
                        }
                    )
                }

                order.note?.let { note ->
                    byCategoryTable.apply {
                        addCell(
                            Cell(
                                Phrase(note.text ?: "")
                            ).apply {
                                border = Cell.BOTTOM + Cell.RIGHT
                            }
                        )
                        addCell(
                            Cell(
                                if (note.photo != null) {
                                    Image.getInstance(note.photo).apply {
                                        scaleAbsolute(350f, 350f)
                                    }
                                } else {
                                    Phrase("")
                                }
                            ).apply {
                                border = Cell.BOTTOM
                            }
                        )
                    }
                }
            }

            add(Paragraph(Chunk.NEWLINE))
            add(byCategoryTable)
            add(Paragraph(Chunk.NEWLINE))
        }

        specialCategories.groupBy { it.categoryName }.forEach {
            newPage()
            //add category name
            add(
                Paragraph(
                    it.key,
                    Font(Font.COURIER, 20f, Font.BOLDITALIC, Color.BLACK)
                ).apply {
                    alignment = Element.ALIGN_CENTER
                }
            )

            val ordersByCategory = it.value

            val byCategoryTable = Table(2).apply {
                padding = 2f
                spacing = 1f
                width = 100f
            }

            listOf("Item", "Quantity").forEach {
                val current = Cell(Phrase(it)).apply {
                    isHeader = true
                    backgroundColor = Color.LIGHT_GRAY
                }
                byCategoryTable.addCell(current)
            }

            ordersByCategory.forEach { order ->
                byCategoryTable.apply {
                    addCell(
                        Cell(
                            Phrase(order.productName)
                        ).apply {
                            border = if (order.note != null) Cell.RIGHT else Cell.BOTTOM + Cell.RIGHT
                        }
                    )
                    addCell(
                        Cell(Phrase("${order.totalUnit} ${order.unitName}")).apply {
                            border = if (order.note != null) Cell.NO_BORDER else Cell.BOTTOM
                        }
                    )
                }

                order.note?.let { note ->
                    byCategoryTable.apply {
                        addCell(
                            Cell(
                                Phrase(note.text ?: "")
                            ).apply {
                                border = Cell.BOTTOM + Cell.RIGHT
                            }
                        )
                        addCell(
                            Cell(
                                if (note.photo != null) {
                                    Image.getInstance(note.photo).apply {
                                        scaleAbsolute(350f, 350f)
                                    }
                                } else {
                                    Phrase("")
                                }
                            ).apply {
                                border = Cell.BOTTOM
                            }
                        )
                    }
                }
            }

            add(Paragraph(Chunk.NEWLINE))
            add(byCategoryTable)
            add(Paragraph(Chunk.NEWLINE))
        }

        close()
    }

    pdfWriter.close()

    openPdf(
        File(
            invoiceDir, "${fileName}.pdf"
        )
    )
}

fun pdfInvoiceForCustomers(
    fileName: String,
    orders: List<Order>
) {
    val invoiceDir = File("invoices".appDataDir())

    // 1) Create a FileOutputStream object with the path and name of the file
    val pdfOutputFile = FileOutputStream(
        File(
            invoiceDir, "${fileName}.pdf"
        )
    )

    val myPDFDoc = Document(PageSize.A4, 40f, 40f, 80f, 80f)

    val footer = Rectangle(30f, 30f, PageSize.A4.getRight(30f), 140f).apply {
        border = Rectangle.BOX
        borderColor = Color.BLACK
        borderWidth = 2f
    }

    val header = Rectangle(30f, 30f, PageSize.A4.getRight(30f), 140f).apply {
        border = Rectangle.BOX
        borderColor = Color.BLUE
        borderWidth = 1f
        top = PageSize.A4.getTop(30f)
        bottom = PageSize.A4.getTop(180f)
    }

    val title = "Order Summary"

    val pdfWriter = PdfWriter.getInstance(myPDFDoc, pdfOutputFile).apply {

        pageEvent = object : PdfPageEventHelper() {

            override fun onEndPage(writer: PdfWriter, doc: Document) {
                with(writer.directContent) {
//                    rectangle(header)
//                    rectangle(footer)
                }
            }
        }
    }

    val myTable = Table(3).apply {
        padding = 2f
        spacing = 1f
        width = 100f
    }

    listOf("Customer", "Total Items", "Delivery Total").forEach {
        val current = Cell(Phrase(it)).apply {
            isHeader = true
            backgroundColor = Color.LIGHT_GRAY
        }
        myTable.addCell(current)
    }

    orders.sortedBy { it.customer_name }.forEachIndexed { index, order ->
        myTable.apply {
            addCell(
                Cell(Phrase(order.customer_name)).apply {
                    border = Cell.BOTTOM + Cell.RIGHT
                }
            )
            addCell(
                Cell(Phrase(order.items.size.toString())).apply {
                    border = Cell.BOTTOM + Cell.RIGHT
                }
            )
//            addCell(
//                Cell(Phrase(order.total.toCurrencyFormat())).apply {
//                    border = Cell.BOTTOM + Cell.RIGHT
//                }
//            )
            addCell(
                Cell(Phrase("")).apply {
                    border = Cell.BOTTOM
                }
            )
        }
    }

    myPDFDoc.apply {
        addTitle("Order Summary $fileName")
        addSubject("This is a tutorial explaining how to use openPDF")
        addKeywords("Kotlin, OpenPDF, Basic sample")
        addCreator("Stockary")
        addAuthor("Stockary")

        open()

        add(
            Paragraph(
                title,
                Font(Font.COURIER, 20f, Font.BOLDITALIC, Color.BLACK)
            ).apply {
                alignment = Element.ALIGN_CENTER
            }
        )

        add(Paragraph(Chunk.NEWLINE))
        add(myTable)
        add(Paragraph(Chunk.NEWLINE))

        //add new page for print for customer wise
        orders.sortedBy { it.customer_name }.forEach {
            newPage()
            //add category name
            add(
                Paragraph(
                    it.customer_name,
                    Font(Font.COURIER, 20f, Font.BOLDITALIC, Color.BLACK)
                ).apply {
                    alignment = Element.ALIGN_CENTER
                }
            )
            add(
                Paragraph(
                    "ID: #${it.id}",
                    Font(Font.COURIER, 16f, Font.NORMAL, Color.BLACK)
                ).apply {
                    alignment = Element.ALIGN_LEFT
                }
            )
            add(
                Paragraph(
                    "Total: ${it.total.toCurrencyFormat()}",
                    Font(Font.COURIER, 16f, Font.NORMAL, Color.BLACK)
                ).apply {
                    alignment = Element.ALIGN_LEFT
                }
            )

            val byCustomerTable = Table(4).apply {
                padding = 2f
                spacing = 1f
                width = 100f
            }

            listOf("Item", "Demand", "Deliver", "Delivery Total").forEach {
                val current = Cell(Phrase(it)).apply {
                    isHeader = true
                    backgroundColor = Color.LIGHT_GRAY
                }
                byCustomerTable.addCell(current)
            }

            it.items.forEachIndexed { index, orderItem ->
                byCustomerTable.apply {
                    addCell(
                        Cell(
                            Phrase(
                                "${orderItem.title}"
                            )
                        ).apply {
                            border = Cell.BOTTOM + Cell.RIGHT
                        }
                    )
                    addCell(
                        Cell(Phrase("${orderItem.quantity} ${orderItem.units?.type}")).apply {
                            border = Cell.BOTTOM + Cell.RIGHT
                        }
                    )
                    addCell(
                        Cell(Phrase("")).apply {
                            border = Cell.BOTTOM + Cell.RIGHT
                        }
                    )
                    addCell(
                        Cell(Phrase("")).apply {
                            border = Cell.BOTTOM
                        }
                    )

                    //add note
                    orderItem.note?.let { note ->
                        addCell(
                            Cell(
                                Phrase(note.text ?: "")
                            ).apply {
                                colspan = 2
                                border = Cell.BOTTOM + Cell.RIGHT
                            }
                        )
                        addCell(
                            Cell(
                                if (note.photo != null) {
                                    Image.getInstance(note.photo).apply {
                                        scaleAbsolute(350f, 350f)
                                    }
                                } else {
                                    Phrase("")
                                }
                            ).apply {
                                colspan = 2
                                border = Cell.BOTTOM
                            }
                        )
                    }
                }
            }

            add(Paragraph(Chunk.NEWLINE))
            add(byCustomerTable)
            add(Paragraph(Chunk.NEWLINE))
        }

        close()
    }

    pdfWriter.close()

    openPdf(
        File(
            invoiceDir, "${fileName}.pdf"
        )
    )
}

fun pdfInvoice(
    fileName: String,
    order: Order
) {
    val invoiceDir = File("invoices".appDataDir())

    // 1) Create a FileOutputStream object with the path and name of the file
    val pdfOutputFile = FileOutputStream(
        File(
            invoiceDir, "${fileName}.pdf"
        )
    )

    val myPDFDoc = Document(PageSize.A4, 40f, 40f, 80f, 80f)

    val footer = Rectangle(30f, 30f, PageSize.A4.getRight(30f), 140f).apply {
        border = Rectangle.BOX
        borderColor = Color.BLACK
        borderWidth = 2f
    }

    val header = Rectangle(30f, 30f, PageSize.A4.getRight(30f), 140f).apply {
        border = Rectangle.BOX
        borderColor = Color.BLUE
        borderWidth = 1f
        top = PageSize.A4.getTop(30f)
        bottom = PageSize.A4.getTop(180f)
    }

    val title = "Order Summary"

    val pdfWriter = PdfWriter.getInstance(myPDFDoc, pdfOutputFile).apply {

        pageEvent = object : PdfPageEventHelper() {

            override fun onEndPage(writer: PdfWriter, doc: Document) {
                with(writer.directContent) {
//                    rectangle(header)
//                    rectangle(footer)
                }
            }
        }
    }

    myPDFDoc.apply {
        addTitle("Order Summary $fileName")
        addSubject("This is a tutorial explaining how to use openPDF")
        addKeywords("Kotlin, OpenPDF, Basic sample")
        addCreator("Stockary")
        addAuthor("Stockary")

        open()

        //add new page for print for customer wise
        //add category name
        add(
            Paragraph(
                order.customer_name,
                Font(Font.COURIER, 20f, Font.BOLDITALIC, Color.BLACK)
            ).apply {
                alignment = Element.ALIGN_CENTER
            }
        )
        add(
            Paragraph(
                "ID: #${order.id}",
                Font(Font.COURIER, 16f, Font.NORMAL, Color.BLACK)
            ).apply {
                alignment = Element.ALIGN_LEFT
            }
        )
        add(
            Paragraph(
                "Total: ${order.total.toCurrencyFormat()}",
                Font(Font.COURIER, 16f, Font.NORMAL, Color.BLACK)
            ).apply {
                alignment = Element.ALIGN_LEFT
            }
        )

        val byCustomerTable = Table(5).apply {
            padding = 2f
            spacing = 1f
            width = 100f
        }

        listOf("Item", "Demand", "Deliver", "Demand Total", "Delivery Total").forEach {
            val current = Cell(Phrase(it)).apply {
                isHeader = true
                backgroundColor = Color.LIGHT_GRAY
            }
            byCustomerTable.addCell(current)
        }

        val size = order.items.size

        order.items.forEachIndexed { index, orderItem ->
            byCustomerTable.apply {
                addCell(
                    Cell(Phrase(orderItem.title)).apply {
                        border = Cell.BOTTOM + Cell.RIGHT
                    }
                )
                addCell(
                    Cell(Phrase(orderItem.quantity.toString())).apply {
                        border = Cell.BOTTOM + Cell.RIGHT
                    }
                )
                addCell(
                    Cell(Phrase("")).apply {
                        border = Cell.BOTTOM + Cell.RIGHT
                    }
                )
                addCell(
                    Cell(
                        Phrase(
                            (orderItem.price * orderItem.quantity).toCurrencyFormat()
                        )
                    ).apply {
                        border = Cell.BOTTOM + Cell.RIGHT
                    }
                )
                addCell(
                    Cell(Phrase("")).apply {
                        border = Cell.BOTTOM
                    }
                )

                //add note
                orderItem.note?.let { note ->
                    addCell(
                        Cell(
                            Phrase(note.text ?: "")
                        ).apply {
                            colspan = 2
                            border = Cell.BOTTOM + Cell.RIGHT
                        }
                    )
                    addCell(
                        Cell(
                            if (note.photo != null) {
                                Image.getInstance(note.photo).apply {
                                    scaleAbsolute(350f, 350f)
                                }
                            } else {
                                Phrase("")
                            }
                        ).apply {
                            colspan = 3
                            border = Cell.BOTTOM
                        }
                    )
                }
            }
        }

        add(Paragraph(Chunk.NEWLINE))
        add(byCustomerTable)
        add(Paragraph(Chunk.NEWLINE))

        close()
    }

    pdfWriter.close()

    openPdf(
        File(
            invoiceDir, "${fileName}.pdf"
        )
    )
}

private fun openPdf(file: File) {
    if (Desktop.isDesktopSupported()) {
        try {
            Desktop.getDesktop().open(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}