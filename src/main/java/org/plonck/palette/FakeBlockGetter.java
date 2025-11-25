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
