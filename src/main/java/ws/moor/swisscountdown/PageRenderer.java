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

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;

public class PageRenderer {
  private final Settings settings;

  public PageRenderer(Settings settings) {
    this.settings = settings;
  }

  public void render(PDPageContentStream stream, LengthVector pageSize) throws IOException {
    int boxCount = settings.getTopArmWidth() * settings.getTopArmHeight() * 4
        + settings.getTopArmWidth() * settings.getTopArmWidth();
    Length flagTotalLength = settings.getBoxSize().scale(settings.getTopArmHeight() * 2 + settings.getTopArmWidth())
        .plus(settings.getBorderWidth().scale(2));
    LengthVector flagDimensions = LengthVector.of(flagTotalLength, flagTotalLength);

    LengthVector pageMiddle = pageSize.scale(0.5f);

    // from https://en.wikipedia.org/wiki/Flag_of_Switzerland:
    // Example using RGB #D52B1E, intended as approximating Pantone 485
    stream.setNonStrokingColor(new Color(0xD5, 0x2B, 0x1E));
    traceRectangle(stream, pageMiddle.minus(flagDimensions.scale(0.5f)), pageMiddle.plus(flagDimensions.scale(0.5f)));
    stream.fill();

    int counter = boxCount - 1;
    LocalDate date = settings.getLastDay().minusDays(counter);

    Length topY = pageMiddle.y
        .plus(settings.getBoxSize().scale(settings.getTopArmHeight() + settings.getTopArmWidth() / 2.0f));
    for (int j = 0; j < settings.getTopArmHeight(); j++) {
      Length topX = pageMiddle.x.minus(settings.getBoxSize().scale(settings.getTopArmWidth() / 2.0f));
      for (int i = 0; i < settings.getTopArmWidth(); i++) {
        renderBox(stream, LengthVector.of(topX, topY), counter--, date);
        topX = topX.plus(settings.getBoxSize());
        date = date.plusDays(1);
      }
      topY = topY.minus(settings.getBoxSize());
    }

    for (int j = 0; j < settings.getTopArmWidth(); j++) {
      Length topX = pageMiddle.x
          .minus(settings.getBoxSize().scale(settings.getTopArmWidth() / 2.0f + settings.getTopArmHeight()));
      for (int i = 0; i < settings.getTopArmHeight() * 2 + settings.getTopArmWidth(); i++) {
        renderBox(stream, LengthVector.of(topX, topY), counter--, date);
        topX = topX.plus(settings.getBoxSize());
        date = date.plusDays(1);
      }
      topY = topY.minus(settings.getBoxSize());
    }

    for (int j = 0; j < settings.getTopArmHeight(); j++) {
      Length topX = pageMiddle.x.minus(settings.getBoxSize().scale(settings.getTopArmWidth() / 2.0f));
      for (int i = 0; i < settings.getTopArmWidth(); i++) {
        renderBox(stream, LengthVector.of(topX, topY), counter--, date);
        topX = topX.plus(settings.getBoxSize());
        date = date.plusDays(1);
      }
      topY = topY.minus(settings.getBoxSize());
    }
  }

  private void renderBox(PDPageContentStream stream, LengthVector topLeft, int counter, LocalDate day) throws IOException {
    LengthVector bottomRight = topLeft.plus(LengthVector.of(settings.getBoxSize(), settings.getBoxSize().scale(-1)));
    LengthVector middle = bottomRight.plus(topLeft).scale(0.5f);

    // Draw the white box + black outline.
    stream.setStrokingColor(Color.BLACK);
    stream.setNonStrokingColor(Color.WHITE);
    stream.setLineWidth(0.5f);
    traceRectangle(stream, topLeft, bottomRight);
    stream.fillAndStroke();

    // Write countdown label.
    String label = String.format("%d", counter);
    stream.setNonStrokingColor(Color.BLACK);
    float fontSize = settings.getBoxSize().points / 2.2f;
    PDType1Font font = PDType1Font.HELVETICA_BOLD;
    stream.setFont(font, fontSize);

    LengthVector textDimensions = computeDimensions(font, fontSize, label);
    LengthVector textOrigin = middle.minus(textDimensions.scale(0.5f));

    stream.beginText();
    stream.setTextMatrix(textOrigin.toTranslationMatrix());
    stream.showText(label);
    stream.endText();

    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendValue(DAY_OF_MONTH)
        .appendLiteral('.')
        .appendValue(MONTH_OF_YEAR)
        .appendLiteral('.')
        .toFormatter();
    label = day.format(formatter);
    stream.setNonStrokingColor(Color.BLACK);
    fontSize = fontSize / 3;
    font = PDType1Font.HELVETICA;
    stream.setFont(font, fontSize);

    textDimensions = computeDimensions(font, fontSize, label);

    stream.beginText();

    textOrigin = LengthVector.of(
        middle.x.minus(textDimensions.x.scale(0.5f)),
        bottomRight.y.plus(textOrigin.y).scale(0.5f).minus(textDimensions.y.scale(0.5f)));
    stream.setTextMatrix(textOrigin.toTranslationMatrix());
    stream.showText(label);
    stream.endText();
  }

  private LengthVector computeDimensions(PDType1Font font, float fontSize, String text) throws IOException {
    return LengthVector.of(
        Length.ofPoints(font.getStringWidth(text) * fontSize / 1000),
        Length.ofPoints(font.getHeight('0') * fontSize / 1000));
  }

  private void traceRectangle(PDPageContentStream stream, LengthVector bottomLeft, LengthVector topRight) throws IOException {
    stream.addRect(
        bottomLeft.x.points, bottomLeft.y.points,
        topRight.minus(bottomLeft).x.points, topRight.minus(bottomLeft).y.points);
  }
}
