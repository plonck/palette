/*
 * Copyright (c) 2025 Tarik Hrnjica and contributors. All rights reserved.
 * This source code is licensed under the MIT License.
 *
 * For the full copyright and license information, please view the LICENSE file
 * found in the root directory of this source tree.
 */
package org.plonck.palette;

import java.util.List;
import org.slf4j.Logger;

/**
 * Represents a unit of work that generates a dataset to be saved as a CSV file.
 * <p>
 * Tasks are designed to be executed in parallel by a {@link TaskExecutor}.
 */
public interface Task {
  /**
   * Gets the unique name of the task.
   * <p>
   * This name is used to determine the output filename (e.g., "name.csv").
   *
   * @return the task's unique name
   */
  String getName();

  /**
   * Generates the CSV dataset for this task.
   *
   * @param logger a logger for the task to record its own progress or errors
   * @return a list of comma-separated strings representing the dataset rows
   */
  List<String> generate(Logger logger);
}
