package com.stockary.common.ui.summary

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter
import com.stockary.common.repository.order.model.Order
import java.awt.Color
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


fun pdfInvoice(
    fileName: String,
    orders: List<Order>
){
    // 1) Create a FileOutputStream object with the path and name of the file
    val pdfOutputFile = FileOutputStream("./${fileName}.pdf")

    val myPDFDoc = Document(PageSize.A4, 40f, 40f, 200f, 150f)
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

        pageEvent = object : PdfPageEventHelper(){

            override fun onEndPage(writer: PdfWriter, doc: Document) {
                with (writer.directContent){
                    rectangle(header);
                    rectangle(footer);
                }
            }
        }
    }

    val myTable = Table(4).apply {
        padding = 2f
        spacing = 1f
        width = 100f
    }

    listOf("Category", "Item", "No of Customers", "Total Unit").forEach {
        val current = Cell(Phrase(it)).apply{
            isHeader = true
            backgroundColor = Color.LIGHT_GRAY
        }
        myTable.addCell(current)
    }

    orders.forEach { order ->

        myTable.apply {
            addCell(Cell(Phrase(order.profile?.firstName)))
            addCell(Cell(Phrase(order.total)))
            addCell(Cell(Phrase(order.discount)))
            addCell(Cell(Phrase(order.orderItems.sumOf { it.quantity }.toString())))
        }
    }

    //1) Create a pdf object with using the class
    //  import com.lowagie.text.Image and the method getInstance
    //  with the url https://kesizo.github.io/assets/images/kesizo-logo-6-832x834.png

    val image = Image.getInstance("https://kesizo.github.io/assets/images/kesizo-logo-6-832x834.png")
    image.scaleAbsolute(150f,150f);

    myPDFDoc.apply {

        addTitle("Order Summary $fileName")
        addSubject("This is a tutorial explaining how to use openPDF")
        addKeywords("Kotlin, OpenPDF, Basic sample")
        addCreator("Stockary")
        addAuthor("Stockary")

        open()
        add(
            Paragraph(title, Font(Font.COURIER, 20f, Font.BOLDITALIC, Color.BLUE)).apply {
                alignment = Element.ALIGN_CENTER
            }
        )

        add(Paragraph(Chunk.NEWLINE))
        add(myTable)
        add(Paragraph(Chunk.NEWLINE))

        // 2) Add the picture to the pdf
        add(image)
        close()
    }

    pdfWriter.close()

    openPdf(File("./${fileName}.pdf"))

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