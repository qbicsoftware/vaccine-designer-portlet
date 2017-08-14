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
package helper;

public class DBConfig {

  private String hostname;
  private String port;
  private String sql_database;
  private String username;
  private String password;

  public DBConfig(String hostname, String port, String sql_database, String username,
      String password) {
    this.hostname = hostname;
    this.port = port;
    this.sql_database = sql_database;
    this.username = username;
    this.password = password;
  }

  public String getHostname() {
    return hostname;
  }

  public String getPort() {
    return port;
  }

  public String getSql_database() {
    return sql_database;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }



}
