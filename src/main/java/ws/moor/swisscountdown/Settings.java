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

import java.time.LocalDate;

public class Settings {

  // Width and height of the topmost arm of the cross - in # of boxes.
  private final int topArmWidth;
  private final int topArmHeight;

  // Width/height of each individual box.
  private final Length boxSize;

  // Distance between the top/sides/bottom of the cross to the edge of the red square.
  private final Length borderWidth;

  // Last day of the countdown - box with value "0".
  private final LocalDate lastDay;

  private Settings(int topArmWidth, int topArmHeight, Length boxSize, Length borderWidth, LocalDate lastDay) {
    this.topArmWidth = topArmWidth;
    this.topArmHeight = topArmHeight;
    this.boxSize = boxSize;
    this.borderWidth = borderWidth;
    this.lastDay = lastDay;
  }

  public static Builder builder() {
    return new Builder();
  }

  public int getTopArmWidth() {
    return topArmWidth;
  }

  public int getTopArmHeight() {
    return topArmHeight;
  }

  public Length getBoxSize() {
    return boxSize;
  }

  public Length getBorderWidth() {
    return borderWidth;
  }

  public LocalDate getLastDay() {
    return lastDay;
  }

  public static class Builder {
    private int topArmWidth = 6;
    private int topArmHeight = 7;
    private Length boxSize = null;
    private Length borderWidth = null;
    private LocalDate lastDay = null;

    Builder setTopArmWidth(int topArmWidth) {
      this.topArmWidth = topArmWidth;
      return this;
    }

    Builder setTopArmHeight(int topArmHeight) {
      this.topArmHeight = topArmHeight;
      return this;
    }

    Builder setBoxSize(Length boxSize) {
      this.boxSize = boxSize;
      return this;
    }

    Builder setBorderWidth(Length borderWidth) {
      this.borderWidth = borderWidth;
      return this;
    }

    Builder setLastDay(LocalDate lastDay) {
      this.lastDay = lastDay;
      return this;
    }

    Settings build() {
      if (boxSize == null) {
        boxSize = Length.ofInches(8).scale(1.0f / (topArmWidth * 3 + topArmHeight * 2));
      }
      if (borderWidth == null) {
        borderWidth = boxSize.scale(topArmWidth);
      }
      return new Settings(
          topArmWidth,
          topArmHeight,
          boxSize,
          borderWidth,
          lastDay);
    }
  }
}
