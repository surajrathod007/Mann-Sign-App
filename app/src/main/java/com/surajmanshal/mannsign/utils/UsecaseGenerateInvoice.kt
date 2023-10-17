package com.surajmanshal.mannsign.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Environment
import android.widget.Toast
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.GST
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.data.model.ordering.Order
import java.io.ByteArrayOutputStream

import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate

class UsecaseGenerateInvoice(private val context : Context) {
    operator fun invoke(order: Order, user: User) {

            try {
                var lst = order.orderItems

//            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
                val path = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
                    context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.toString()
                }else{
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
                }
                val file = File(path, "mann_invoice${System.currentTimeMillis()}.pdf")
                val output = FileOutputStream(file)

                val writer = PdfWriter(file)
                val pdfDocument = PdfDocument(writer)
                val document = Document(pdfDocument)
                //document.setMargins(1f,1f,1f,1f)


                //header
                val headerImg = context.getDrawable(R.drawable.invoice_header)
                val bitmap = (headerImg as BitmapDrawable).bitmap
                val opstream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, opstream)
                val bitmapdata = opstream.toByteArray()

                val img = ImageDataFactory.create(bitmapdata)
                val myheader = Image(img)
                document.add(myheader)


                //first table
                val c: FloatArray = floatArrayOf(220F, 220F, 200F)
                val table1 = Table(c)


                // row 1
                table1.addCell(Cell(0,1).add(Paragraph("Bill to Party ").setFontSize(10.0f)).setBold())
                //table1.addCell(Cell().add(Paragraph("").setFontSize(10.0f)).setBold())
                table1.addCell(Cell().add(Paragraph("Date : ").setFontSize(10.0f)).setBold())
                val today = Functions.getFormatedTimestamp(System.currentTimeMillis(),"dd-MM-yyyy")
                table1.addCell(
                    Cell().add(
                        Paragraph(today?: LocalDate.now().toString()).setFontSize(10.0f).setBold()
                    )
                )

                //row 2
//                table1.addCell(Cell(4, 0).add(Paragraph("857,indiranagar -2 ").setFontSize(8.0f)))
                table1.addCell(Cell(4, 0).add(Paragraph("${"Manshal"} ${"Khatri"}\n${"550, Inidiranagr-2, Lambha, Ahemdabad, 382405"}").setFontSize(8.0f)))
//                table1.addCell(Cell(4, 0).add(Paragraph("${user.firstName} ${user.lastName}\n${user.address}").setFontSize(8.0f)))
                //table1.addCell(Cell(4, 0).add(Paragraph("").setFontSize(8.0f)))
                table1.addCell(Cell().add(Paragraph("Invoice No : ").setFontSize(10.0f)))
//            table1.addCell(Cell().add(Paragraph("${order.orderId}").setFontSize(10.0f)))
                val invoiceNo = order.invoiceNo
//                val invoiceId = Functions.getFinancialYearString(LocalDate.now()) + "-MS-$invoiceNo"
                table1.addCell(Cell().add(Paragraph(invoiceNo).setFontSize(10.0f)))

                //row 3
                //table1.addCell(Cell().add(Paragraph("")))
                //table1.addCell(Cell().add(Paragraph("")))
                table1.addCell(Cell().add(Paragraph("Order No. :").setFontSize(10.0f)))
                table1.addCell(Cell().add(Paragraph(order.orderId).setFontSize(10.0f)))

                //row 4
                //table1.addCell(Cell().add(Paragraph("")))
                //table1.addCell(Cell().add(Paragraph("")))
                table1.addCell(Cell().add(Paragraph("Order Date : ").setFontSize(10.0f)))
                table1.addCell(
                    Cell().add(
                        Paragraph(
                           order.orderDate.toString()
                        ).setFontSize(10.0f)
                    )
                )

                //row 5
                //table1.addCell(Cell().add(Paragraph("")))
                //table1.addCell(Cell().add(Paragraph("")))
                table1.addCell(Cell().add(Paragraph("Pin Code : ").setFontSize(10.0f)))
                table1.addCell(Cell().add(Paragraph("").setFontSize(10.0f)))

                //row 6
                table1.addCell(Cell().add(Paragraph("Pin Code : ${user.pinCode}").setFontSize(8.0f).setBold()))
                //table1.addCell(Cell().add(Paragraph("").setFontSize(8.0f).setBold()))
                table1.addCell(
                    Cell(0, 2).add(Paragraph("COMPANY GSTIN NO : ").setFontSize(8.0f).setBold())
                        .setTextAlignment(
                            TextAlignment.CENTER
                        )
                )
                //table1.addCell(Cell().add(Paragraph("")))

                //row 7
                /*table1.addCell(
                    Cell().add(
                        Paragraph("GSTIN NO : ").setFontSize(8.0f).setBold()
                    )
                )*/
            table1.addCell(
                Cell().add(
                    Paragraph("GSTIN NO : ${user.gstNo}").setFontSize(8.0f).setBold()
                )
            )
                table1.addCell(
                    Cell(0, 2).add(Paragraph("24BENPP0006B1Z4").setFontSize(8.0f).setBold())
                        .setTextAlignment(
                            TextAlignment.CENTER
                        )
                )
                //table1.addCell(Cell().add(Paragraph("")))

                document.add(table1)

                //2nd table
                val table2 = Table(6)
                table2.useAllAvailableWidth()
                //row1
                table2.addCell(Cell().add(Paragraph("Sr No.").setBold().setFontSize(10.0f))).setTextAlignment(TextAlignment.CENTER)
                table2.addCell(
                    Cell().add(
                        Paragraph("Product Description").setBold().setFontSize(10.0f)
                    ).setTextAlignment(TextAlignment.CENTER)
                )
                table2.addCell(Cell().add(Paragraph("HSN Code").setBold().setFontSize(10.0f).setTextAlignment(TextAlignment.CENTER)))
                //table2.addCell(Cell().add(Paragraph("UOM").setBold().setFontSize(10.0f)))
                //table2.addCell(Cell().add(Paragraph("Product Type").setBold().setFontSize(10.0f)))
                table2.addCell(Cell().add(Paragraph("Quantity").setBold().setFontSize(10.0f).setTextAlignment(TextAlignment.CENTER)))
                table2.addCell(Cell().add(Paragraph("Rate").setBold().setFontSize(10.0f).setTextAlignment(TextAlignment.CENTER)))
                table2.addCell(Cell().add(Paragraph("Amount").setBold().setFontSize(10.0f).setTextAlignment(TextAlignment.CENTER)))

                //add items
                var sr = 1
                var gtotal = 0.0f
                var extraRows = 15 - lst!!.size
                with(table2) {
                    lst!!.forEach {
                        addCell(sr.toString()).setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
                        addCell(it.product!!.posterDetails!!.title).setFontSize(10f)
                        addCell("Hsn$sr").setFontSize(10f).setTextAlignment(TextAlignment.CENTER)

                        /*
                        addCell("UOM$sr").setFontSize(10f)
                        //set product type
                        if (it.product!!.posterDetails != null)
                            addCell("Poster").setFontSize(10f)
                        if (it.product!!.boardDetails != null)
                            addCell("ACP Board").setFontSize(10f)
                        if (it.product!!.bannerDetails != null)
                            addCell("Banner").setFontSize(10f)

                         */


//                        addCell("${it.quantity}").setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
//                        addCell("${it.variant!!.variantPrice}").setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
//                        addCell("${it.totalPrice.getTwoDecimalValue()}").setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
//                        gtotal += it.totalPrice

                        val gstBreakDownPrice = GST.getInstance(it.variant!!.variantPrice!!).itemPrice
                        val totalGSTExclusivePrice = it.quantity*gstBreakDownPrice
                        addCell("${it.quantity}").setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
                        addCell(gstBreakDownPrice.getTwoDecimalValue()).setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
                        addCell(totalGSTExclusivePrice.getTwoDecimalValue()).setFontSize(10f).setTextAlignment(TextAlignment.CENTER)
                        gtotal += totalGSTExclusivePrice
                        sr++
                    }

                    for (i in 1..extraRows) {
                        addCell(sr.toString()).setFontSize(10f)
                        addCell("").setFontSize(10f)
                        addCell("").setFontSize(10f)
                        addCell("").setFontSize(10f)
                        //addCell("").setFontSize(10f)
                        //addCell("").setFontSize(10f)
                        addCell("").setFontSize(10f)
                        addCell("").setFontSize(10f)
                        sr++
                    }
                }

                val cgst = (gtotal * 9) / 100
                val sgst = (gtotal * 9) / 100

                document.add(table2)

                val col = floatArrayOf(200f, 200f, 200f, 180f)
                val table3 = Table(col)

                //bar
                val barImg = context.getDrawable(R.drawable.upi_barcode)
                val bitmap1 = (barImg as BitmapDrawable).bitmap
                val opstream1 = ByteArrayOutputStream()
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, opstream1)
                val bitmapdata1 = opstream1.toByteArray()

                var img1 = ImageDataFactory.create(bitmapdata1)

                var upibar = Image(img1)
                upibar.setHorizontalAlignment(HorizontalAlignment.CENTER)
                upibar.setHeight(80f)
                upibar.setWidth(80f)

                //row1
                table3.addCell(
                    Cell().add(
                        Paragraph("Bank Detail").setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.CENTER
                        )
                    )
                )
                table3.addCell(
                    Cell().add(
                        Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.CENTER
                        )
                    )
                )
                table3.addCell(
                    Cell().add(
                        Paragraph("Total Amount before Tax : ").setFontSize(10f).setBold()
                            .setTextAlignment(
                                TextAlignment.RIGHT
                            )
                    )
                )
                table3.addCell(
                    Cell().add(
                        Paragraph(gtotal.getTwoDecimalValue()).setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.RIGHT
                        )
                    )
                )

                //row2
                table3.addCell(
                    Cell().add(
                        Paragraph("Bank : Bank of Maharashtra").setFontSize(10f).setBold()
                            .setTextAlignment(
                                TextAlignment.CENTER
                            )
                    )
                )
                table3.addCell(
                    Cell(5, 0).add(upibar).setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setVerticalAlignment(
                            VerticalAlignment.MIDDLE
                        )
                )
                table3.addCell(
                    Cell().add(
                        Paragraph("Add: CGST (9%) : ").setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.RIGHT
                        )
                    )
                )
                table3.addCell(
                    Cell().add(
                        Paragraph(cgst.getTwoDecimalValue()).setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.RIGHT
                        )
                    )
                )

                //row3
                table3.addCell(Cell().add(Paragraph("Bank A/C : 60207512779\n").setFontSize(10f)))
                //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
                table3.addCell(
                    Cell().add(
                        Paragraph("Add: SGST (9%) : ").setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.RIGHT
                        )
                    )
                )
                table3.addCell(
                    Cell().add(
                        Paragraph(sgst.getTwoDecimalValue()).setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.RIGHT
                        )
                    )
                )

                //row4
                table3.addCell(Cell().add(Paragraph("Bank IFSC : MAHB0001632").setFontSize(10f)))
                //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
                table3.addCell(
                    Cell().add(
                        Paragraph("Total Tax Amount : ").setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.RIGHT
                        )
                    )
                )
                table3.addCell(
                    Cell().add(
                        Paragraph((cgst + sgst).getTwoDecimalValue()).setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.RIGHT
                        )
                    )
                )

                //row5
                table3.addCell(Cell().add(Paragraph("PAN No. : BENPP0006B").setFontSize(10f)))
                //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
                table3.addCell(
                    Cell().add(
                        Paragraph("Total Amount after Tax  ₹").setFontSize(10f).setBold()
                            .setTextAlignment(
                                TextAlignment.RIGHT
                            )
                    )
                )
                table3.addCell(
                    Cell().add(
                        Paragraph((gtotal + cgst + sgst).getTwoDecimalValue()).setFontSize(10f).setBold()
                            .setTextAlignment(
                                TextAlignment.RIGHT
                            )
                    )
                )

                //row6
                table3.addCell(Cell().add(Paragraph("").setFontSize(10f)))
                //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
                table3.addCell(
                    Cell().add(
                        Paragraph("Transportation Charge : ").setFontSize(10f).setBold()
                            .setTextAlignment(
                                TextAlignment.RIGHT
                            )
                    )
                )
                table3.addCell(
                    Cell().add(
                        Paragraph("").setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.RIGHT
                        )
                    )
                )

                //row7
                table3.addCell(Cell().add(Paragraph("").setFontSize(10f)))
                table3.addCell(
                    Cell().add(
                        Paragraph("UPI ID: 7405736990@okbizaxis").setFontSize(8f).setBold()
                            .setTextAlignment(
                                TextAlignment.CENTER
                            )
                    )
                )
                table3.addCell(
                    Cell().add(
                        Paragraph("Grand Total : ").setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.RIGHT
                        )
                    )
                )
                table3.addCell(
                    Cell().add(
                        Paragraph((gtotal + cgst + sgst).getTwoDecimalValue()).setFontSize(10f).setBold()
                            .setTextAlignment(
                                TextAlignment.RIGHT
                            )
                    )
                )

                document.add(table3)

                //last table
                val table4 = Table(floatArrayOf(300f, 100f, 100f, 100f))

                //bar
                val stampImg = context.getDrawable(R.drawable.stamp)
                val bitmap2 = (stampImg as BitmapDrawable).bitmap
                val opstream2 = ByteArrayOutputStream()
                bitmap2.compress(Bitmap.CompressFormat.PNG, 100, opstream2)
                val bitmapdata2 = opstream2.toByteArray()

                var img2 = ImageDataFactory.create(bitmapdata2)
                var stamp = Image(img2)
                stamp.setHorizontalAlignment(HorizontalAlignment.CENTER)
                stamp.setHeight(80f)
                stamp.setWidth(80f)


                //row1
                table4.addCell(Cell(2, 0).add(Paragraph("Rupees :").setFontSize(10f)))
                table4.addCell(Cell(6, 0).add(Paragraph("  ").setFontSize(10f)))
                table4.addCell(Cell(6, 0).add(stamp))
                table4.addCell(
                    Cell(5, 0).add(
                        Paragraph("for MANN SIGN:").setFontSize(10f).setBold().setTextAlignment(
                            TextAlignment.CENTER
                        )
                    )
                )

                //row2
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

                //row3
                table4.addCell(Cell().add(Paragraph("** E. & O. E.").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

                //row4
                table4.addCell(
                    Cell(3, 0).add(
                        Paragraph("SUBJECT TO AHMEDABAD JURISDICTION\nThis is a Computer Generated Invoice").setFontSize(
                            10f
                        ).setTextAlignment(
                            TextAlignment.CENTER
                        )
                    )
                )
                //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

                //row5
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

                //row6
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
                //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
                table4.addCell(
                    Cell().add(
                        Paragraph("Authorised Signatory ").setVerticalAlignment(
                            VerticalAlignment.BOTTOM
                        ).setFontSize(8f).setTextAlignment(TextAlignment.CENTER)
                    )
                ).setVerticalAlignment(
                    VerticalAlignment.BOTTOM
                )



                document.add(table4)
                document.close()
                Toast.makeText(context, "Pdf Created", Toast.LENGTH_SHORT).show()
                if (path != null) {
                    context.openFile(file, path)
                }

            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }

        }
}