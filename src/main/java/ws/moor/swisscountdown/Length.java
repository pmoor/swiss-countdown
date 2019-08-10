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

public class Length {
  private static final float POINT_PER_INCH = 72f;
  private static final float METERS_PER_INCH = 0.0254f;

  public final float points;

  private Length(float points) {
    this.points = points;
  }

  public static Length ofCentimeters(float cm) {
    return ofInches(cm / 100 / METERS_PER_INCH);
  }

  public static Length ofInches(float inches) {
    return new Length(inches * POINT_PER_INCH);
  }

  public static Length ofPoints(float points) {
    return new Length(points);
  }

  public Length scale(float factor) {
    return new Length(this.points * factor);
  }

  public Length plus(Length length) {
    return new Length(points + length.points);
  }

  public Length minus(Length length) {
    return plus(length.scale(-1));
  }
}
