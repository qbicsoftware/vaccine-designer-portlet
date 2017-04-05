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


/**
 * Interface to have implementation and library independent way of logging. Depending on our needs
 * we might want to use log4j, java.util.logging, com.liferay.portal.kernel.log.LogFactoryUtil or
 * some other logging mechanism.
 */

public interface Logger {


  /**
   * Logs debug messages (used for developing/testing only)
   * 
   * @param message string to log
   */
  public void debug(String message);

  /**
   * Logs info messages (used for important non-error messages, for real problems use warn/error
   * methods)
   * 
   * @see #warn(String)
   * @see #error(String)
   * @param message string to log
   */
  public void info(String message);

  /**
   * Logs non-critical error messages
   * 
   * @param message string to log
   */
  public void warn(String message);

  /**
   * Logs error messages (severe faults). If you need more output see
   * 
   * @see #error(String, Throwable)
   * @param message string to log
   */
  public void error(String message);

  /**
   * Logs error message (severe fault) and adds the top 10 lines of the stack trace (to not overload
   * the console)
   * 
   * @param message string to log
   * @param t throwable to expand log
   */
  public void error(String message, Throwable t);

  /**
   * Logs error message (severe fault) and adds the top 10 lines of the stack trace (to not overload
   * the console)
   * 
   * @param message string to log
   * @param stackTraceElement StackTraceElement to expand log
   */
  public void error(String message, StackTraceElement[] stackTraceElement);

}
