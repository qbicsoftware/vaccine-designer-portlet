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

import java.util.Arrays;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class LiferayLogger implements Logger {

  private Log liferayLog;

  public LiferayLogger(Class<?> c) {
    liferayLog = LogFactoryUtil.getLog(c);
  }

  @Override
  public void debug(String message) {
    liferayLog.debug(message);
  }

  @Override
  public void info(String message) {
    liferayLog.info(message);
  }

  @Override
  public void warn(String message) {
    liferayLog.warn(message);

  }

  @Override
  public void error(String message) {
    liferayLog.error(message);

  }

  @Override
  public void error(String message, Throwable t) {
    // do not fill logfile with millions of lines per error, please
    if (t == null || t.getStackTrace() == null) {
      return;
    } else if (t.getStackTrace().length > 10) {
      t.setStackTrace(Arrays.copyOfRange(t.getStackTrace(), 0, 10));
    }
    liferayLog.error(message, t);
  }

  @Override
  public void error(String message, StackTraceElement[] stackTraceElement) {
    if (stackTraceElement == null) {
      liferayLog.error(message);
      return;
    } else if (stackTraceElement.length > 10) {
      stackTraceElement = Arrays.copyOfRange(stackTraceElement, 0, 10);
    }
    Throwable t = new Throwable();
    t.setStackTrace(stackTraceElement);
    liferayLog.error(message, t);
  }
}
