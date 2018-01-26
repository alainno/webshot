/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.reniec.pki.cancelacion.screen.capture;

import java.sql.DriverManager;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

/**
 *
 * @author 42447799AUT
 */
public class Dao {

    public static Sql2o sql2o;
    public static Connection con;
    //private static Query query;
    //private static final Logger LOG = Logger.getLogger(Dao.class);
    private static boolean connect;

    static {
//		String dbHost = CFG.prop("db.host");
//		String dbName = CFG.prop("db.name");
//		String dbUser = CFG.prop("db.user");
//		String dbPass = CFG.prop("db.pass");		

        try {
            Class.forName("com.mysql.jdbc.Driver");

//            if (cfg.COMPILE_MODE == "pre") {
//                Dao.sql2o = new Sql2o("java:/sscpp");
//            } else {
            //Dao.sql2o = new Sql2o("jdbc:mysql://" + CFG.prop("db.host") + "/" + CFG.prop("db.name"), CFG.prop("db.user"), CFG.prop("db.pass"));
            Dao.sql2o = new Sql2o("jdbc:mysql://172.24.4.227:3306/pkiep-sirs", "pkiep-sirs", "pkiep-sirs");
//            }

//                if(Dao.sql2o == null){
//                    Dao.connect = false;
//                    LOG.error("sql2o es nulo");
//                }else{
//                    Dao.connect = true;
//                    LOG.info("Conexión con la DB satisfactoria");
//                }
        } catch (Exception /*| NamingException*/ e) {
            //LOG.info("Error al instanciar DB: " + e.getLocalizedMessage());
//			Dao.connect = false;
            //e.printStackTrace();
        }
    }

    public static void beginTransaction() {
        if (Dao.sql2o != null) {
            Dao.con = Dao.sql2o.beginTransaction();
        }
    }

    public static void commit() {
        if (Dao.con == null) {
            return;
        }
        Dao.con.commit();
        Dao.con.close();
    }

    public static void open() {
        if (Dao.sql2o != null) {
            Dao.con = Dao.sql2o.open();
        }
    }

    public static void close() {
        if (Dao.con != null) {
            Dao.con.close();
        }
    }

    public static Query createQuery(String sql, Map<String, Object> values) {
        if (Dao.con == null) {
            return null;
        }
        Query query = Dao.con.createQuery(sql);
        if (values != null && !values.isEmpty()) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                query.addParameter(entry.getKey(), entry.getValue());
            }
        }
        return query;
    }

    public static Query createQuery(String sql) {
        if (Dao.con == null) {
            return null;
        }
        return Dao.con.createQuery(sql);
    }

    /*
	public static Query addParameters(Map<String,Object> values){
		if(values != null && !values.isEmpty()){
			for(Map.Entry<String,Object> entry : values.entrySet()){
				query.addParameter(entry.getKey(), entry.getValue());
			}
		}
		return query;
	}*/
    public static void rollback() {
        if (Dao.con == null) {
            return;
        }
        Dao.con.rollback();
        Dao.con.close();
    }

    public static boolean getConnect() {
//            try{
        Dao.open();
        if (Dao.con == null) {
            return false;
        }
//            }catch(Exception ex){
//                LOG.error(ex.getLocalizedMessage());
//                return false;
//            }
        Dao.close();
        return true;
    }
}
