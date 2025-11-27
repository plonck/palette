package org.plonck.palette;

import java.util.List;
import org.slf4j.Logger;

public interface Task {
  String getName();

  List<String> generate(Logger logger);
}
