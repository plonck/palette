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

/**
 * Executes a collection of {@link Task} objects in parallel using a fixed-size
 * thread pool and writes their exported content to individual CSV files.
 * <p>
 * Tasks can only be registered before the {@link #run} method is called.
 */
public final class TaskExecutor {

  /**
   * The destination folder for generated CSV files.
   */
  private static final Path FOLDER = Path.of("palette");

  private final ExecutorService executor;
  private final Collection<Task> tasks = new LinkedList<>();
  private final Logger logger;

  /**
   * Flag to ensure tasks are only registered before execution starts.
   */
  private volatile boolean done = false;

  /**
   * Constructs a new executor instance.
   *
   * @param concurrency the maximum number of threads to use for parallel task
   * execution
   * @param logger the logger to use for reporting task execution details
   */
  public TaskExecutor(final int concurrency, final Logger logger) {
    executor = Executors.newFixedThreadPool(
      concurrency,
      new NamedThreadFactory("palette-task-")
    );
    this.logger = logger;
  }

  /**
   * Registers a new task to be executed.
   *
   * @param task the task to register
   * @throws IllegalStateException if called after {@link #run} has been invoked
   */
  public void register(final Task task) {
    checkDone();
    tasks.add(task);
  }

  /**
   * Executes all registered tasks in parallel, each writing its output to a CSV
   * file in the designated folder.
   * <p>
   * This method blocks until all tasks have completed or the specified timeout
   * is reached.
   *
   * @param timeout the maximum time (in seconds) to wait for all tasks to
   * finish
   * @return {@code 0} if all tasks completed successfully within the timemout,
   * or {@code 1} otherwise (e.g., timeout, write failure, interruption)
   * @throws IllegalStateException if called more than once
   */
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

  // Writes the given lines to a file, one line at a time, followed by a break.
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
