package PKG;

import java.sql.ResultSet;
import java.util.List;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
//import jennom.jpa.JpaNodes;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

@Repository
//@Service
//@Transactional
public class DaoJDBC extends JdbcDaoSupport { //JdbcTemplate { //JdbcDaoSupport {
    
    //@Inject
    //private JdbcTemplate jdbcTemplate;

    @Inject
    public DaoJDBC(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    //@Transactional(readOnly=true)
    /*public List<JpaNodes> getAllNodes() {
        String sql = "select * from nodes";
        // queryForList(String sql, Class<T> elementType)
        List<JpaNodes> list = this.getJdbcTemplate().queryForList(sql, JpaNodes.class);
        return list;
    }*/
    
    //@Transactional(readOnly=true)
    /*public List<JpaNodes> getNodeByIP(String searchName) {
        String sql = "select * from nodes where ip like ?";
        // queryForList(String sql, Class<T> elementType, Object... args)
        List<JpaNodes> list = 
                this.getJdbcTemplate().queryForList(sql, JpaNodes.class, "%" + searchName + "%");
        return list;
    } */
    
    public void clearAll() {
        String sql = "delete from hosts";
        // queryForList(String sql, Class<T> elementType)
        this.getJdbcTemplate().update(sql);
        //this.update(sql);
        //jdbcTemplate.update(sql);
        System.out.println("ejbDaoJDBC clearALL");
    } 
    
    public void deleteByID (String id) {
        String sql = "DELETE FROM hosts WHERE id = " + id;
        this.getJdbcTemplate().update(sql);
        //this.update(sql);
        //jdbcTemplate.update(sql);
        System.out.println("ejbDaoJDBC deleteByID = "+id);
    }
    
    public void insertRow (String title, String login, String pass, String descr, String url) {
        String sql = "INSERT INTO hosts(title,login,passw,descr,url) VALUES('" + title + "','" + login + "','" + pass + "','" + descr + "','" + url + "')";
        this.getJdbcTemplate().update(sql);
        //this.update(sql);
        //jdbcTemplate.update(sql);
        System.out.println("ejbDaoJDBC insertRow");
    } 
    
    public void editRowByID (String title, String login, String pass, String descr, String url, int id) {
        String sql = "UPDATE hosts SET title='"+title+"',login='"+login+"',passw='"+pass+"',descr='"+descr+"',url='"+url+"'WHERE id = "+id;
        this.getJdbcTemplate().update(sql);
        //this.update(sql);
        //jdbcTemplate.update(sql);
        System.out.println("ejbDaoJDBC changeRowByID");
    }     
    
    public SqlRowSet getAllasSRS() {
        String sql = "SELECT * FROM hosts";
        System.out.println("ejbDaoJDBC getALLasSRS");
        return //jdbcTemplate.queryForRowSet(sql);
                //this.queryForRowSet(sql);
                this.getJdbcTemplate().queryForRowSet(sql);
    } 

    public ResultSet getAllasRS() {
        String sql = "SELECT * FROM hosts";
        System.out.println("ejbDaoJDBC getALLasRS");
        return //((ResultSetWrappingSqlRowSet) jdbcTemplate.queryForRowSet(sql)).getResultSet();
                //((ResultSetWrappingSqlRowSet) this.queryForRowSet(sql)).getResultSet();
                ((ResultSetWrappingSqlRowSet) this.getJdbcTemplate().queryForRowSet(sql)).getResultSet();
    } 

    public SqlRowSet searchByFieldasSRS(String field, String search) {
        String sql = "SELECT * FROM hosts WHERE "+field+" LIKE '%" + search + "%'";
        System.out.println("ejbDaoJDBC searchByFieldasSRS");
        return //jdbcTemplate.queryForRowSet(sql);
                //this.queryForRowSet(sql);
                this.getJdbcTemplate().queryForRowSet(sql);
    } 

    public void createTable() {
        //String sql = "CREATE TABLE hosts(id int not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), title varchar(255), login varchar(255), passw varchar(255), descr varchar(255), url varchar(255))";
        String sql = "CREATE TABLE hosts(id int not null auto_increment primary key, title varchar(255), login varchar(255), passw varchar(255), descr varchar(255), url varchar(255))";
        this.getJdbcTemplate().execute(sql);
        //this.execute(sql);
        //jdbcTemplate.execute(sql);
        System.out.println("ejbDaoJDBC createTable");
    }     

}
