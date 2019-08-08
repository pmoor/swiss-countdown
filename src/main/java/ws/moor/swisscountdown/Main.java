/*
 * Copyright 2019 Patrick Moor <patrick@moor.ws>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.moor.swisscountdown;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;

public class Main {

  public static void main(String[] args) throws IOException {
    PDDocument document = new PDDocument();
    PDPage page = new PDPage();
    page.setMediaBox(PDRectangle.LETTER);

    document.addPage(page);

    PDPageContentStream contentStream = new PDPageContentStream(document, page);

//    int topCrossWidth = 6;
//    int topCrossHeight = 7;
    int topCrossWidth = 4;
    int topCrossHeight = 4;
    float smallSquareSize = 20;
    LocalDate lastDay = LocalDate.of(2020, 3, 1);


    int totalCount = topCrossWidth * topCrossHeight * 4 + topCrossWidth * topCrossWidth;
    float squareSize = (topCrossHeight * 2 + topCrossWidth * 3) * smallSquareSize;
    float midX = page.getMediaBox().getWidth() / 2;
    float midY = page.getMediaBox().getHeight() / 2;

    // from https://en.wikipedia.org/wiki/Flag_of_Switzerland:
    // Example using RGB #D52B1E, intended as approximating Pantone 485
    contentStream.setNonStrokingColor(new Color(0xD5, 0x2B, 0x1E));
    contentStream.moveTo(midX - squareSize / 2, midY + squareSize / 2);
    contentStream.lineTo(midX + squareSize / 2, midY + squareSize / 2);
    contentStream.lineTo(midX + squareSize / 2, midY - squareSize / 2);
    contentStream.lineTo(midX - squareSize / 2, midY - squareSize / 2);
    contentStream.lineTo(midX - squareSize / 2, midY + squareSize / 2);
    contentStream.fill();

    int counter = totalCount - 1;
    LocalDate date = lastDay.minusDays(totalCount - 1);

    for (int j = 0; j < topCrossHeight; j++) {
      for (int i = 0; i < topCrossWidth; i++) {
        float topLeftX = midX + i * smallSquareSize - topCrossWidth * smallSquareSize / 2;
        float topLeftY = midY - j * smallSquareSize + topCrossHeight * smallSquareSize + topCrossWidth * smallSquareSize / 2;
        renderBox(contentStream, smallSquareSize, topLeftX, topLeftY, counter--, date);
        date = date.plusDays(1);
      }
    }

    for (int j = 0; j < topCrossWidth; j++) {
      for (int i = 0; i < topCrossHeight * 2 + topCrossWidth; i++) {
        float topLeftX = midX + i * smallSquareSize - (topCrossHeight * 2 + topCrossWidth) * smallSquareSize / 2;
        float topLeftY = midY - j * smallSquareSize + topCrossWidth * smallSquareSize / 2;
        renderBox(contentStream, smallSquareSize, topLeftX, topLeftY, counter--, date);
        date = date.plusDays(1);
      }
    }

    for (int j = 0; j < topCrossHeight; j++) {
      for (int i = 0; i < topCrossWidth; i++) {
        float topLeftX = midX + i * smallSquareSize - topCrossWidth * smallSquareSize / 2;
        float topLeftY = midY - j * smallSquareSize - topCrossWidth * smallSquareSize / 2;
        renderBox(contentStream, smallSquareSize, topLeftX, topLeftY, counter--, date);
        date = date.plusDays(1);
      }
    }

    contentStream.close();

    document.save("/tmp/swiss-countdown.pdf");
    document.close();
  }

  private static void renderBox(PDPageContentStream contentStream, float smallSquareSize, float topLeftX, float topLeftY, int counter, LocalDate day) throws IOException {
    contentStream.setStrokingColor(Color.BLACK);
    contentStream.setNonStrokingColor(Color.WHITE);
    contentStream.setLineWidth(0.5f);
    contentStream.moveTo(topLeftX, topLeftY );
    contentStream.lineTo(topLeftX + smallSquareSize, topLeftY );
    contentStream.lineTo(topLeftX + smallSquareSize, topLeftY - smallSquareSize );
    contentStream.lineTo(topLeftX, topLeftY - smallSquareSize );
    contentStream.lineTo(topLeftX, topLeftY );
    contentStream.fillAndStroke();


    String label = String.format("%d", counter);
    contentStream.setNonStrokingColor(Color.BLACK);
    float fontSize = smallSquareSize / 2 - 1;
    PDType1Font font = PDType1Font.HELVETICA_BOLD;
    contentStream.setFont(font, fontSize);

    float textWidth = font.getStringWidth(label) * fontSize / 1000;
    float textHeight = font.getHeight('0') * fontSize / 1000;

    contentStream.beginText();
    Matrix m = new Matrix();
    m.translate(topLeftX + smallSquareSize / 2 - textWidth / 2, topLeftY - smallSquareSize / 2 - textHeight / 2);
    contentStream.setTextMatrix(m);
    contentStream.showText(label);
    contentStream.endText();


    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendValue(DAY_OF_MONTH)
        .appendLiteral('.')
        .appendValue(MONTH_OF_YEAR)
        .appendLiteral('.')
        .toFormatter();
    label = day.format(formatter);
    contentStream.setNonStrokingColor(Color.BLACK);
    fontSize = fontSize / 2;
    font = PDType1Font.HELVETICA;
    contentStream.setFont(font, fontSize);

    textWidth = font.getStringWidth(label) * fontSize / 1000;
    textHeight = font.getHeight('0') * fontSize / 1000;

    contentStream.beginText();
    m = new Matrix();
    m.translate(topLeftX + smallSquareSize / 2 - textWidth / 2, topLeftY - smallSquareSize + smallSquareSize / 20);
    contentStream.setTextMatrix(m);
    contentStream.showText(label);
    contentStream.endText();


  }
}
