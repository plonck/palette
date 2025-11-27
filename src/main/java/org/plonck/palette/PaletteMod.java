/*
 * Copyright (c) 2025 Tarik Hrnjica and contributors. All rights reserved.
 * This source code is licensed under the MIT License.
 *
 * For the full copyright and license information, please view the LICENSE file
 * found in the root directory of this source tree.
 */
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

    final long start = System.currentTimeMillis();

    final int status = executor.run(30);

    final long end = System.currentTimeMillis();

    logger.info("Finished palette generation in {} ms", end - start);

    System.exit(status);
  }
}
