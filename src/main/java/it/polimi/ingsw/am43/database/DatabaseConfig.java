package it.polimi.ingsw.am43.database;

/**
 * Holds the connection parameters (URL, user, password) used to access the
 * database. Typically populated from an external configuration source.
 */
public class DatabaseConfig {
    private String url;
    private String user;
    private String password;

    /** @return the database connection URL */
    public String getUrl() { return url; }
    /** @return the database user name */
    public String getUser() { return user; }
    /** @return the database user password */
    public String getPassword() { return password; }


    /** @param url the database connection URL */
    public void setUrl(String url) { this.url = url; }
    /** @param user the database user name */
    public void setUser(String user) { this.user = user; }
    /** @param password the database user password */
    public void setPassword(String password) { this.password = password; }
}
