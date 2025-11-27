package org.plonck.palette;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PaletteMod implements ModInitializer {

  private final Logger logger = LoggerFactory.getLogger("Palette");

  public PaletteMod() {}

  @Override
  public void onInitialize() {
    logger.info("Starting palette generation...");

    final TaskExecutor executor = new TaskExecutor(2, logger);

    executor.register(new ColorsTask());
    executor.register(new BlocksTask());

    final int status = executor.run(30);

    logger.info("Finished palette generation");

    System.exit(status);
  }
}
