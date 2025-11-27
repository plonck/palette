/*
 * Copyright (c) 2025 Tarik Hrnjica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.plonck.palette;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.MapColor.Brightness;
import org.slf4j.Logger;

public final class ColorsTask implements Task {

  public ColorsTask() {}

  @Override
  public String getName() {
    return "colors";
  }

  @Override
  public List<String> generate(final Logger logger) {
    final List<String> lines = new ArrayList<>(
      MapColor.MATERIAL_COLORS.length * 4
    );

    for (int i = 0; i < MapColor.MATERIAL_COLORS.length; i++) {
      final MapColor base = MapColor.MATERIAL_COLORS[i];
      if (base == null || base.id == 0) continue;

      // Compute each shade variant of the base color.
      for (final Brightness variant : Brightness.VALUES) {
        final int rgba = base.calculateARGBColor(variant);

        // Ignore fully transparent colors.
        if (((rgba >> 24) & 0xFF) == 0) continue; // A

        lines.add(
          new StringBuilder()
            .append((base.id * 4) + variant.id)
            .append(',')
            .append((rgba >> 16) & 0xFF) // R
            .append(',')
            .append((rgba >> 8) & 0xFF) // G
            .append(',')
            .append(rgba & 0xFF) // B
            .toString()
        );
      }
    }
    return lines;
  }
}
