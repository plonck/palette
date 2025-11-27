/*
 * Copyright (c) 2025 Tarik Hrnjica and contributors. All rights reserved.
 * This source code is licensed under the MIT License.
 *
 * For the full copyright and license information, please view the LICENSE file
 * found in the root directory of this source tree.
 */
package org.plonck.palette;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

public final class TaskExecutor {

  private static final Path FOLDER = Path.of("palette");

  private final ExecutorService executor;
  private final Collection<Task> tasks = new LinkedList<>();
  private final Logger logger;

  private volatile boolean done = false;

  public TaskExecutor(final int concurrency, final Logger logger) {
    executor = Executors.newFixedThreadPool(
      concurrency,
      new NamedThreadFactory("palette-task-")
    );
    this.logger = logger;
  }

  public void register(final Task task) {
    checkDone();
    tasks.add(task);
  }

  public int run(final int timeout) {
    checkDone();
    done = true;

    try {
      Files.createDirectories(FOLDER);
    } catch (final IOException e) {
      logger.error("Could not create output folder {}", FOLDER, e);
      return 1;
    }

    final CountDownLatch latch = new CountDownLatch(tasks.size());
    for (final Task task : tasks) {
      executor.execute(() -> {
        final Path path = FOLDER.resolve(task.getName() + ".csv");
        logger.info("Generating {}", path);
        try {
          final List<String> lines = task.generate(logger);
          save(path, lines);
        } catch (final Exception e) {
          logger.error("Failed to generate {}", path, e);
        } finally {
          latch.countDown();
        }
      });
    }
    try {
      if (!latch.await(timeout, TimeUnit.SECONDS)) {
        logger.error("Parallel generation timed out");
        return 1;
      }
      executor.shutdown();

      if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        logger.warn("Shutdown took too long. Forcing immediate shutdown");
        executor.shutdownNow();
        return 1;
      }
      return 0;
    } catch (final InterruptedException e) {
      logger.error("Task execution interrupted", e);
      executor.shutdownNow();
      Thread.currentThread().interrupt();
      return 1;
    } catch (final Exception e) {
      logger.error("Unexpected error after tasks completed", e);
      return 1;
    }
  }

  private void save(final Path path, final List<String> lines) {
    try (final BufferedWriter writer = Files.newBufferedWriter(path)) {
      for (final String line : lines) {
        writer.write(line);
        writer.newLine();
      }
    } catch (final IOException e) {
      logger.error("Failed to save entries to {}", path, e);
      return;
    }
    logger.info("Saved " + lines.size() + " entries to " + path);
  }

  private void checkDone() {
    if (done) {
      throw new IllegalStateException("run() has already been called");
    }
  }
}
