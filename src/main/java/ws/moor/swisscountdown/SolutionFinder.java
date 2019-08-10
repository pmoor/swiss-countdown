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

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class SolutionFinder {
  private final int targetCount;

  public SolutionFinder(int targetCount) {
    this.targetCount = targetCount;
  }

  public List<Solution> findBestN(int n) {
    // Sorted by highest (=worst) score first.
    PriorityQueue<Solution> solutions = new PriorityQueue<>(Comparator.comparingInt(Solution::score).reversed());

    for (int topArmWidth = 1; topArmWidth < Math.sqrt(targetCount); topArmWidth++) {
      // The flag's proper dimensions are 6 width vs. 7 height, but let's be a bit generous
      // and allow +/- 25% deviation from that.
      int smallestHeightAllowed = (int) Math.ceil(topArmWidth * 7.0 / 6.0 * 0.75);
      int largestHeightAllowed = (int) Math.floor(topArmWidth * 7.0 / 6.0 * 1.25);
      for (int topArmHeight = smallestHeightAllowed; topArmHeight <= largestHeightAllowed; topArmHeight++) {
        Solution solution = new Solution(topArmWidth, topArmHeight);

        if (solutions.size() < n) {
          solutions.offer(solution);
        } else if (solution.score() < solutions.peek().score()) {
          solutions.poll();
          solutions.offer(solution);
        }
      }
    }

    // Sort by best (lowest) score first.
    return solutions.stream().sorted(Comparator.comparingInt(Solution::score)).collect(Collectors.toList());
  }

  public class Solution {
    public final int topArmWidth;
    public final int topArmHeight;

    private Solution(int topArmWidth, int topArmHeight) {
      this.topArmWidth = topArmWidth;
      this.topArmHeight = topArmHeight;
    }

    public int count() {
      return topArmWidth * (topArmHeight * 4 + topArmWidth);
    }

    private int score() {
      return Math.abs(count() - targetCount);
    }
  }
}
