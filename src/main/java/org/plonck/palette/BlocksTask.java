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
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import org.slf4j.Logger;

public final class BlocksTask implements Task {

  public BlocksTask() {}

  @Override
  public String getName() {
    return "blocks";
  }

  @Override
  public List<String> generate(final Logger logger) {
    final DefaultedRegistry<Block> registry = BuiltInRegistries.BLOCK;
    final List<String> lines = new ArrayList<>(registry.size());
    int skipped = 0;
    for (final Block block : registry) {
      final ResourceLocation key = registry.getKey(block);
      MapColor color = null;
      try {
        color = block
          .defaultBlockState()
          .getMapColor(FakeBlockGetter.INSTANCE, BlockPos.ZERO);
      } catch (final Exception e) {
        // Faking the world context did not suffice to determine the color.
        if (logger.isDebugEnabled()) {
          logger.debug("Skipping block {}", key, e);
        }
        skipped++;
      }
      if (color != null) {
        lines.add(key.toString() + "," + Integer.toUnsignedString(color.id));
      }
    }
    if (skipped > 0) {
      logger.warn("Skipped " + skipped + " blocks");
    }
    return lines;
  }
}
