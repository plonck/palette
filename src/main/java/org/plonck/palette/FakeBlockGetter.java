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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class FakeBlockGetter implements BlockGetter {

  public static final BlockGetter INSTANCE = new FakeBlockGetter();

  private FakeBlockGetter() {}

  @Override
  public int getHeight() {
    // Generic maximum height.
    return 384;
  }

  @Override
  public int getMinY() {
    // Generic minimum height.
    return -64;
  }

  @Override
  public BlockEntity getBlockEntity(final BlockPos pos) {
    // Map colors rarely depend on Tile Entity data, so null is safe.
    return null;
  }

  @Override
  public BlockState getBlockState(final BlockPos pos) {
    // If a block asks "What is next to me?", we say "Air".
    // This prevents infinite recursion.
    return Blocks.AIR.defaultBlockState();
  }

  @Override
  public FluidState getFluidState(final BlockPos pos) {
    // Return empty fluid to prevent water-logging logic crashes.
    return Fluids.EMPTY.defaultFluidState();
  }
}
