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

import org.apache.pdfbox.util.Matrix;

// TODO: See if this can/should be replaced by PDRectangle?
public class LengthVector {
  public final Length x;
  public final Length y;

  private LengthVector(Length x, Length y) {
    this.x = x;
    this.y = y;
  }

  public static LengthVector of(Length x, Length y) {
    return new LengthVector(x, y);
  }

  public LengthVector scale(float f) {
    return new LengthVector(x.scale(f), y.scale(f));
  }

  public LengthVector plus(LengthVector vector) {
    return new LengthVector(x.plus(vector.x), y.plus(vector.y));
  }

  public LengthVector minus(LengthVector scale) {
    return plus(scale.scale(-1));
  }

  public Matrix toTranslationMatrix() {
    Matrix m = new Matrix();
    m.translate(x.points, y.points);
    return m;
  }
}
