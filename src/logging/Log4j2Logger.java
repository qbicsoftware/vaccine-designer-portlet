/*******************************************************************************
 * QBiC Project Wizard enables users to create hierarchical experiments including different study conditions using factorial design.
 * Copyright (C) "2016"  Andreas Friedrich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package logging;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;

public class Log4j2Logger implements Logger, Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = -7780269050491615333L;
  org.apache.logging.log4j.Logger logger;

  public Log4j2Logger(Class<?> c) {
    logger = LogManager.getLogger(c);
  }

  @Override
  public void debug(String message) {
    logger.debug(message);

  }

  @Override
  public void info(String message) {
    logger.info(message);

  }

  @Override
  public void warn(String message) {
    logger.warn(message);

  }

  @Override
  public void error(String message) {
    logger.error(message);
  }

  @Override
  public void error(String message, Throwable t) {
    if (t == null || t.getStackTrace() == null) {
      logger.error(message);
      return;
    } else if (t.getStackTrace().length > 10) {
      t.setStackTrace(Arrays.copyOfRange(t.getStackTrace(), 0, 10));
    }
    logger.error(message, t);

  }

  @Override
  public void error(String message, StackTraceElement[] stackTraceElement) {
    if (stackTraceElement == null) {
      logger.error(message);
      return;
    } else if (stackTraceElement.length > 10) {
      stackTraceElement = Arrays.copyOfRange(stackTraceElement, 0, 10);
    }
    Throwable t = new Throwable();
    t.setStackTrace(stackTraceElement);
    logger.error(message, t);
  }
}
