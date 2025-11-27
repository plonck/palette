/*
 * Copyright (c) 2025 Tarik Hrnjica and contributors. All rights reserved.
 * This source code is licensed under the MIT License.
 *
 * For the full copyright and license information, please view the LICENSE file
 * found in the root directory of this source tree.
 */
package org.plonck.palette;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class NamedThreadFactory implements ThreadFactory {

  private final String prefix;
  private final AtomicInteger id = new AtomicInteger(1);

  public NamedThreadFactory(final String prefix) {
    this.prefix = prefix;
  }

  @Override
  public Thread newThread(final Runnable runnable) {
    final Thread thread = new Thread(runnable, prefix + id.getAndIncrement());
    thread.setDaemon(true);
    return thread;
  }
}
