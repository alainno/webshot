/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.reniec.pki.cancelacion.screen.capture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.sql2o.Query;
import org.sql2o.data.Row;

/**
 *
 * @author aalain
 */
public class ScreenCapture {
    
    public final static String SC_URL_DEFAULT = "http://ecep.reniec.gob.pe/pkiep-sirs/pages/web/serviciosWeb.jsfx";
    public final static int SC_WIDTH = 600;
    public final static int SC_HEIGHT = 2100;
    public final static int SC_TIMEOUT = 5;
    //public final static String PHANTOM_BINARY_PATH = "D:\\Software\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe";
    public final static String PHANTOM_BINARY_PATH = "/home/serviciospki/phantomjs";
    /*
    private String MAIL_HOST = "localhost";
    private String MAIL_PORT = "25";
    private String MAIL_USER = "25";
    private String MAIL_PASS = "25";
    */

    public static void main(String[] args) throws IOException, IOException, InterruptedException {
/*        final String link = "https://ecep.reniec.gob.pe/dashboard/production/daily";
        final File screenShot = new File("D:\\screenshot.png").getAbsoluteFile();

        System.out.println("Creating Firefox Driver");
        final WebDriver driver = new FirefoxDriver();
//        final WebDriver driver = new ChromeDriver();
        try {
            System.out.println("Opening page: {}" + link);
            driver.get(link);
            System.out.println("Wait a bit for the page to render");
            TimeUnit.SECONDS.sleep(5);
            System.out.println("Taking Screenshot");
            final File outputFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(outputFile, screenShot);
            System.out.println("Screenshot saved: {}" + screenShot);
        } catch(Exception ex){
            System.out.println("Excepción: " + ex.getLocalizedMessage());
        } finally {
            driver.close();
        }
*/
        //String url = "http://ecep.reniec.gob.pe/pkiep-sirs/pages/web/serviciosWeb.jsfx";
        
        ScreenCapture sc = new ScreenCapture();
        if(args == null || args.length == 0){
            System.out.println("args es nulo o vacío");
            System.exit(0);
        }
        
        String url = args[0];
        System.out.println("URL = " + url);
        String imagePath = sc.capturar(url);
        if(imagePath == null){
            System.out.println("Error al capturar imagen");
            System.exit(0);
        }
        System.out.println("Imagen capturada en: " + imagePath);
        
        Map mailParams = sc.getMailParams();
        if(mailParams == null || mailParams.isEmpty()){
            System.out.println("mail params es nulo o vacio");
            System.exit(0);
        }
        System.out.println("MAIL PARAMS = " + mailParams);
        
        if(!sc.enviarReporte(imagePath, mailParams)){
            System.out.println("Error al enviar reporte");
            System.exit(0);
        }
        System.out.println("Reporte enviado con éxito!");
    }

    private String capturar(String url) {
        try{
            System.setProperty("phantomjs.binary.path", PHANTOM_BINARY_PATH);
            WebDriver driver = new PhantomJSDriver();
            
            driver.manage().window().setSize(new Dimension(SC_WIDTH,SC_HEIGHT));        
            driver.get(url);
            TimeUnit.SECONDS.sleep(SC_TIMEOUT);
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            // Now you can do whatever you need to do with it, for example copy somewhere
            //String imagePath = "D:\\screenshot.png";
//            String imagePath = "/home/serviciospki/screenshot.png";
            File tempFile = File.createTempFile("screenshot", ".png");
            //String imagePath = "/home/serviciospki/screenshot.png";
            //FileUtils.copyFile(scrFile, new File(imagePath));
            FileUtils.copyFile(scrFile, tempFile);
            driver.close();
            System.out.println("screenshot done");
//            return imagePath;
            return tempFile.getAbsolutePath();
        }catch(Exception ex){
            System.out.println("Excepción al capturar: " + ex.getLocalizedMessage());
            return null;
        }
    }
    
    public boolean enviarReporte(String imagePath, Map<String,String> params){                
        JMailer mailer = new JMailer();
        mailer.charSet = "UTF-8";
        mailer.host = params.get("MAIL_SERVIDOR");
        mailer.port = Integer.parseInt(params.get("MAIL_PUERTO"));
        mailer.username = params.get("MAIL_REMITECORREO");
        mailer.password = params.get("MAIL_CLAVE");
        mailer.from = params.get("MAIL_REMITECORREO");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fecha = sdf.format(new Date());
        mailer.subject = "Monitoreo desde Canadá " + fecha;
       
        String cid = "123456789";
        mailer.AddAttachment(imagePath, cid);
        mailer.body = "<html><head></head><body><img src=\"cid:"+cid+"\" alt=\"\"/></body></html>";
       
        String destinatarios = params.get("MAIL_DESTINOSSERVICIOS");
        if(destinatarios == null || destinatarios.isEmpty()){
            System.out.println("MAIL_DESTINOSSERVICIOS es nulo o vacio");
            return false;
        }
        
        /*List<String> paras = new ArrayList();
        paras.add("aalejo@pkiep.reniec.gob.pe");
        paras.add("alaings@gmail.com");*/
        List<String> paras = Arrays.asList(destinatarios.split(";"));
        for(String para : paras){
            mailer.addAddress(para);
        }
        
        //mailer.addBCC("alain_ah@hotmail.com");
        mailer.setHTML(true);
        /*if(!mailer.send()){
			return false;
		}
		return true;*/
        boolean enviado = mailer.send();
        return enviado;        
    }    

    // devuelve las direcciones email de los que recibiran el reporte
    /*private List getParas() {
        //List paras = new ArrayList();
        if(!Dao.getConnect()){
            System.out.println("Error de conexion con la base de datos");
            return null;
        }
        
        String sql = "SELECT SPARAMETRO FROM TM_PARAMETRO WHERE SCODIGOPARAMETRO=:SCODIGOPARAMETRO LIMIT 1";
        Dao.open();
        String destinos = (String)Dao.createQuery(sql).addParameter("SCODIGOPARAMETRO", "MAIL_DESTINOSSERVICIOS").executeScalar();
        Dao.close();
        return Arrays.asList(destinos.split(";"));
    }*/

    //
    public Map getMailParams() {
        if (!Dao.getConnect()) {
            System.out.println("Error de conexion con la base de datos");
            return null;
        }
        
        String sql = "SELECT CONCAT(SCODIGOPARAMETRO,'/',SPARAMETRO) FROM TM_PARAMETRO WHERE SCODIGOPARAMETRO LIKE 'MAIL_%'";
        Dao.open();
        List<String> params = Dao.createQuery(sql).executeScalarList(String.class);
        Dao.close();
        
        if(params == null || params.isEmpty()){
            System.out.println("No hay MAIL_%");
            return null;
        }
        
        Map rpta = new HashMap();
        for(String param : params){
            String[] pair = param.split("/");
            rpta.put(pair[0], pair[1]);
        }
        return rpta;
    }
}
