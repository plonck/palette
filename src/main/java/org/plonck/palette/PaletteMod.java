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
