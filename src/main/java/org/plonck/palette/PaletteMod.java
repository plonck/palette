package org.plonck.palette;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.MapColor.Brightness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaletteMod implements ModInitializer {

  private static final Path PATH_COLORS = Path.of("colors.csv");
  private static final Path PATH_BLOCKS = Path.of("blocks.csv");

  private final Logger logger = LoggerFactory.getLogger("Palette");

  public PaletteMod() {}

  @Override
  public void onInitialize() {
    logger.info("Starting palette generation...");

    final int tasks = 2;
    final ExecutorService executor = Executors.newFixedThreadPool(
      tasks,
      new NamedThreadFactory("palette-task-")
    );
    final CountDownLatch latch = new CountDownLatch(tasks);

    executor.execute(() -> {
      logger.info("Generating color table...");
      try {
        generateColors();
      } catch (final Exception e) {
        logger.error("Failed to generate color table", e);
      } finally {
        latch.countDown();
      }
    });

    executor.execute(() -> {
      logger.info("Generating block table...");
      try {
        generateBlocks();
      } catch (final Exception e) {
        logger.error("Failed to generate block table", e);
      } finally {
        latch.countDown();
      }
    });

    try {
      if (!latch.await(30, TimeUnit.SECONDS)) {
        logger.error("Parallel generation timed out.");
        System.exit(1);
      }

      executor.shutdown();
      logger.info("Done generating palette");
      System.exit(0);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error("Generation thread interrupted", e);
      System.exit(1);
    } catch (final Exception e) {
      logger.error("Unexpected error after generation tasks completed", e);
      System.exit(1);
    }
  }

  private void generateColors() throws IOException {
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

    save(PATH_COLORS, lines);
  }

  private void generateBlocks() throws IOException {
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
    save(PATH_BLOCKS, lines);
  }

  private void save(final Path path, final List<String> lines)
    throws IOException {
    try (final BufferedWriter writer = Files.newBufferedWriter(path)) {
      for (final String line : lines) {
        writer.write(line);
        writer.newLine();
      }
    }
    logger.info(
      "Saved " + lines.size() + " entries to " + path.toAbsolutePath()
    );
  }

  private static final class NamedThreadFactory implements ThreadFactory {

    private final String prefix;
    private final AtomicInteger id = new AtomicInteger(1);

    NamedThreadFactory(final String prefix) {
      this.prefix = prefix;
    }

    @Override
    public Thread newThread(final Runnable runnable) {
      final Thread thread = new Thread(runnable, prefix + id.getAndIncrement());
      thread.setDaemon(true);
      return thread;
    }
  }
}
