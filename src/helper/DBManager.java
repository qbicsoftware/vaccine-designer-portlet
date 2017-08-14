/*******************************************************************************
 * QBiC Project Wizard enables users to create hierarchical experiments including different study
 * conditions using factorial design. Copyright (C) "2016" Andreas Friedrich
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package helper;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import logging.Log4j2Logger;
import model.Person;


public class DBManager {
  private DBConfig config;

  logging.Logger logger = new Log4j2Logger(DBManager.class);

  public DBManager(DBConfig config) {
    this.config = config;
  }

  private void logout(Connection conn) {
    try {
      if (conn != null)
        conn.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private Connection login() {
    String DB_URL = "jdbc:mariadb://" + config.getHostname() + ":" + config.getPort() + "/"
        + config.getSql_database();

    Connection conn = null;

    try {
      Class.forName("org.mariadb.jdbc.Driver");
      conn = DriverManager.getConnection(DB_URL, config.getUsername(), config.getPassword());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return conn;
  }

  public Person getPersonForProject(String projectIdentifier, String role) {
    String sql =
        "SELECT * FROM persons LEFT JOIN projects_persons ON persons.id = projects_persons.person_id "
            + "LEFT JOIN projects ON projects_persons.project_id = projects.id WHERE "
            + "projects.openbis_project_identifier = ? AND projects_persons.project_role = ?";
    Person res = null;

    Connection conn = login();
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, projectIdentifier);
      statement.setString(2, role);

      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        String zdvID = rs.getString("username");
        String first = rs.getString("first_name");
        String last = rs.getString("family_name");
        String email = rs.getString("email");
        String tel = rs.getString("phone");
        int instituteID = -1;// TODO fetch correct id
        res = new Person(zdvID, first, last, email, tel, instituteID);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      logout(conn);
      // LOGGER.debug("Project not associated with Investigator. PI will be set to 'Unknown'");
    }

    logout(conn);
    return res;
  }

  public String getProjectName(String projectIdentifier) {
    String sql = "SELECT short_title from projects WHERE openbis_project_identifier = ?";
    String res = "";
    Connection conn = login();
    try {
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setString(1, projectIdentifier);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = rs.getString(1);
      }
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } catch (NullPointerException n) {
      logger.error("Could not reach SQL database, resuming without project names.");
    }
    logout(conn);
    return res;
  }

  public int isProjectInDB(String projectIdentifier) {
    logger.info("Looking for project " + projectIdentifier + " in the DB");
    String sql = "SELECT * from projects WHERE openbis_project_identifier = ?";
    int res = -1;
    Connection conn = login();
    try {
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setString(1, projectIdentifier);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = rs.getInt("id");
        logger.info("project found!");
      }
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    logout(conn);
    return res;
  }

  public int addProjectToDB(String projectIdentifier, String projectName) {
    int exists = isProjectInDB(projectIdentifier);
    if (exists < 0) {
      logger.info("Trying to add project " + projectIdentifier + " to the person DB");
      String sql = "INSERT INTO projects (openbis_project_identifier, short_title) VALUES(?, ?)";
      Connection conn = login();
      try (PreparedStatement statement =
          conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, projectIdentifier);
        statement.setString(2, projectName);
        statement.execute();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
          logout(conn);
          logger.info("Successful.");
          return rs.getInt(1);
        }
      } catch (SQLException e) {
        logger.error("SQL operation unsuccessful: " + e.getMessage());
        e.printStackTrace();
      }
      logout(conn);
      return -1;
    }
    return exists;
  }

  public boolean hasPersonRoleInProject(int personID, int projectID, String role) {
    logger.info("Checking if person already has this role in the project.");
    String sql =
        "SELECT * from projects_persons WHERE person_id = ? AND project_id = ? and project_role = ?";
    boolean res = false;
    Connection conn = login();
    try {
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setInt(1, personID);
      statement.setInt(2, projectID);
      statement.setString(3, role);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = true;
        logger.info("person already has this role!");
      }
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    logout(conn);
    return res;
  }

  public void addPersonToProject(int projectID, int personID, String role) {
    if (!hasPersonRoleInProject(personID, projectID, role)) {
      logger.info("Trying to add person with role " + role + " to a project.");
      String sql =
          "INSERT INTO projects_persons (project_id, person_id, project_role) VALUES(?, ?, ?)";
      Connection conn = login();
      try (PreparedStatement statement =
          conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setInt(1, projectID);
        statement.setInt(2, personID);
        statement.setString(3, role);
        statement.execute();
        logger.info("Successful.");
      } catch (SQLException e) {
        logger.error("SQL operation unsuccessful: " + e.getMessage());
        e.printStackTrace();
      }
      logout(conn);
    }
  }

  /**
   * returns a map of principal investigator first+last names along with the pi_id. only returns
   * active investigators
   * 
   * @return
   */
  public Map<String, Integer> getPrincipalInvestigatorsWithIDs() {
    String sql = "SELECT id, first_name, family_name FROM persons WHERE active = 1";
    Map<String, Integer> res = new HashMap<String, Integer>();
    Connection conn = login();
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int pi_id = rs.getInt("id");
        String first = rs.getString("first_name");
        String last = rs.getString("family_name");
        res.put(first + " " + last, pi_id);
      }
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    logout(conn);
    return res;
  }

  public int addExperimentToDB(String id) {
    int exists = isExpInDB(id);
    if (exists < 0) {
      String sql = "INSERT INTO experiments (openbis_experiment_identifier) VALUES(?)";
      Connection conn = login();
      try (PreparedStatement statement =
          conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, id);
        statement.execute();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
          logout(conn);
          return rs.getInt(1);
        }
      } catch (SQLException e) {
        logger.error("Was trying to add experiment " + id + " to the person DB");
        logger.error("SQL operation unsuccessful: " + e.getMessage());
      }
      logout(conn);
      return -1;
    }
    logger.info("added experiment do mysql db");
    return exists;
  }

  private int isExpInDB(String id) {
    logger.info("Looking for experiment " + id + " in the DB");
    String sql = "SELECT * from experiments WHERE openbis_experiment_identifier = ?";
    int res = -1;
    Connection conn = login();
    try {
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setString(1, id);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        logger.info("experiment found!");
        res = rs.getInt("id");
      }
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    logout(conn);
    return res;
  }

  public void addPersonToExperiment(int expID, int personID, String role) {
    if (expID == 0 || personID == 0)
      return;

    if (!hasPersonRoleInExperiment(personID, expID, role)) {
      logger.info("Trying to add person with role " + role + " to an experiment.");
      String sql =
          "INSERT INTO experiments_persons (experiment_id, person_id, experiment_role) VALUES(?, ?, ?)";
      Connection conn = login();
      try (PreparedStatement statement =
          conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        statement.setInt(1, expID);
        statement.setInt(2, personID);
        statement.setString(3, role);
        statement.execute();
        logger.info("Successful.");
      } catch (SQLException e) {
        logger.error("SQL operation unsuccessful: " + e.getMessage());
        e.printStackTrace();
      }
      logout(conn);
    }
  }

  private boolean hasPersonRoleInExperiment(int personID, int expID, String role) {
    logger.info("Checking if person already has this role in the experiment.");
    String sql =
        "SELECT * from experiments_persons WHERE person_id = ? AND experiment_id = ? and experiment_role = ?";
    boolean res = false;
    Connection conn = login();
    try {
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setInt(1, personID);
      statement.setInt(2, expID);
      statement.setString(3, role);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = true;
        logger.info("person already has this role!");
      }
    } catch (SQLException e) {
      logger.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    logout(conn);
    return res;
  }

//  private void endQuery(Connection c, PreparedStatement p) {
//    if (p != null)
//      try {
//        p.close();
//      } catch (Exception e) {
//        logger.error("PreparedStatement close problem");
//      }
//    if (c != null)
//      try {
//        logout(c);
//      } catch (Exception e) {
//        logger.error("Database Connection close problem");
//      }
//  }

}
