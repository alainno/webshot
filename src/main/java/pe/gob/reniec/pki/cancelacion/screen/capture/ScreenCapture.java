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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author aalain
 */
public class ScreenCapture {
    
    

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

        ScreenCapture sc = new ScreenCapture();
        String url = "http://ecep.reniec.gob.pe/pkiep-sirs/pages/web/serviciosWeb.jsfx";
        int width = 1024;
        int height = 1500;
        int timeout = 5;
        //String imagePath = sc.capturar(url, width, height, timeout);

//        List paras = new ArrayList();
//        paras.add("aalejo@pkiep.reniec.gob.pe");
        List paras = sc.getParas();
        
        
        //sc.enviarReporte(imagePath, paras);
    }

    private String capturar(String url, int width, int height, int timeout) {
        try{
            System.setProperty("webdriver.gecko.driver", "C:\\Users\\aalain\\Downloads\\geckodriver.exe");
            WebDriver driver = new FirefoxDriver();        
            driver.manage().window().setSize(new Dimension(width,height));        
            driver.get(url);
            TimeUnit.SECONDS.sleep(timeout);
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            // Now you can do whatever you need to do with it, for example copy somewhere
            String imagePath = "D:\\screenshot.png";
            FileUtils.copyFile(scrFile, new File(imagePath));
            driver.close();
            System.out.println("done");
            return imagePath;
        }catch(Exception ex){
            System.out.println("Excepción al capturar: " + ex.getLocalizedMessage());
            return null;
        }
    }
    
    public boolean enviarReporte(String imagePath, List<String> paras){
        JMailer mailer = new JMailer();
        mailer.charSet = "UTF-8";
        mailer.host = "172.24.2.43";
        mailer.port = Integer.parseInt("25");
        mailer.username = "prueba";
        mailer.password = "Reniec022016";
        mailer.from = "prueba@pkiep.reniec.gob.pe";
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fecha = sdf.format(new Date());
        mailer.subject = "Monitoreo de Servicios " + fecha;
        //String cid = ContentIdGenerator.getContentId();
        //String cid = "logoreniec";
        //System.out.println("********** CID: " + cid);
        //Map datos = new HashMap();
        //datos.put("cid", cid);
        //mailer.body = Utils.parseHtmlTemplate(CFG.prop("noti.template"), mailData);
        //////Reporte reporte = new Reporte();
        //mailer.body = reporte.getNotificationEmail(CFG.prop("noti.template"), mailData);
        
        String cid = "123456789";
        
        
            mailer.AddAttachment(imagePath, cid);
        
        
        mailer.body = "<html><head></head><body><img src=\"cid:"+cid+"\" alt=\"\"/></body></html>";
        //mailer.body = "Matílde lleva tílde";		
        //ClassLoader classLoader = getClass().getClassLoader();
        //mailer.AddAttachment(classLoader.getResource("logoreniec.png").getFile(), cid);
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
    private List getParas() {
        List paras = new ArrayList();
        if(!Dao.getConnect()){
            System.out.println("Error de conexion con la base de datos");
        }else{
            System.out.println("Base de datos conectada");
        }
        
        return paras;
    }
}
