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
