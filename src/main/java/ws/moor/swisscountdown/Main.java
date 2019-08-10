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

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;

public class Main {

  public static void main(String[] args) throws IOException {
    LocalDate lastDay = LocalDate.of(2020, 3, 1);

    LocalDate today = LocalDate.now();
    long desiredNumberOfDays = Duration.between(today.atStartOfDay(), lastDay.plusDays(1).atStartOfDay()).toDays();
    SolutionFinder finder = new SolutionFinder((int) desiredNumberOfDays);

    PDDocument document = new PDDocument();

    for (SolutionFinder.Solution solution : finder.findBestN(10)) {
      PDPage page = new PDPage();
      page.setMediaBox(PDRectangle.LETTER);
      document.addPage(page);

      LengthVector pageSize = LengthVector.of(Length.ofPoints(page.getMediaBox().getWidth()), Length.ofPoints(page.getMediaBox().getHeight()));
      Settings settings = Settings.builder()
          .setTopArmWidth(solution.topArmWidth)
          .setTopArmHeight(solution.topArmHeight)
          .setLastDay(lastDay)
          .build();

      PDPageContentStream contentStream = new PDPageContentStream(document, page);
      new PageRenderer(settings).render(contentStream, pageSize);
      contentStream.close();
    }

    document.save("/tmp/swiss-countdown.pdf");
    document.close();
  }
}
