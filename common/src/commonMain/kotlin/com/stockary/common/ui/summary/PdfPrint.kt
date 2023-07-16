package com.stockary.common.ui.summary

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter
import com.stockary.common.repository.order.model.Order
import com.stockary.common.repository.order.model.OrderSummaryTable
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

fun pdfInvoice(
    fileName: String,
    orders: List<OrderSummaryTable>
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

    val title = "Kitchen Summary"

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

    orders.sortedBy { it.categoryName }.forEach { order ->
        myTable.apply {
            addCell(
                Cell(Phrase(order.categoryName)).apply {
                    border = Cell.BOTTOM
                }
            )
            addCell(
                Cell(Phrase(order.productName)).apply {
                    border = Cell.BOTTOM
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