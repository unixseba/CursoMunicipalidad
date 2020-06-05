package py.com.excelsis.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.math.BigDecimal;
import java.util.Calendar;

import java.util.HashMap;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.share.security.SecurityContext;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import org.apache.myfaces.trinidad.util.Service;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;

import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.List;
import java.util.Random;

import javax.faces.context.ExternalContext;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.naming.NamingException;

import javax.servlet.http.HttpServletRequest;

import javax.sql.DataSource;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.binding.AttributeBinding;

import oracle.jbo.Row;

import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.DBTransactionImpl;
import oracle.jbo.server.ViewObjectImpl;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.time.LocalDate;
import java.time.Period;

import java.time.ZoneId;

import java.time.temporal.ChronoUnit;

import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import oracle.jbo.AttributeDef;
import oracle.jbo.ReadOnlyAttrException;
import oracle.jbo.ValidationException;
import oracle.jbo.ViewObject;
import oracle.jbo.server.Entity;

import oracle.jbo.server.ViewRowImpl;

public class Utiles {

    private static String obtener;
    private Object valorCampo = null;
    static String verFiscalia;

    public Utiles() {
        super();
    }

    public static void showPopup(String mensajes, javax.faces.application.FacesMessage.Severity severidad) {

        FacesMessage fm = new FacesMessage(mensajes);

        fm.setSeverity(severidad);
        FacesContext context = FacesContext.getCurrentInstance();
        //context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, fm);

    }

    public static void showPopup(String mensajes, javax.faces.application.FacesMessage.Severity severidad,
                                 String component) {

        FacesMessage fm = new FacesMessage(mensajes);

        fm.setSeverity(severidad);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(component, fm);
    }

    public static void showPopup(javax.faces.application.FacesMessage fm, String component) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(component, fm);
    }

    public static String getUserLogged() {
        ADFContext adfCtx = ADFContext.getCurrent();
        SecurityContext secCntx = adfCtx.getSecurityContext();
        return secCntx.getUserName();
    }         

    public static void ejecutarActionBinding(String operation) throws Exception {
        BindingContainer bc = BindingContext.getCurrent().getCurrentBindingsEntry();
        OperationBinding operationBinding = bc.getOperationBinding(operation);
        HashMap result = (HashMap) operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            //return null;
            throw new Exception("No se pudo ejecutar la operaci�n" + operation);
        }
    }

    public static void cerrarVentana() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExtendedRenderKitService service = Service.getRenderKitService(facesContext, ExtendedRenderKitService.class);
        service.addScript(facesContext, "window.opener = self;window.close();");
    }

    public static String convertPassToHash(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        System.out.println("Hex format : " + sb.toString());

        return sb.toString();

        //convert the byte to hex format method 2
        /*
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(0xff & byteData[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        System.out.println("Hex format : " + hexString.toString());
        */
    }

    private static Connection getDataSourceConnection(String dataSourceName) throws Exception {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup(dataSourceName);
        return ds.getConnection();
    }

    public static Connection getConnection(String jdbcConn) throws Exception {
        //return getDataSourceConnection("java:comp/env/jdbc/Connection1DS");// Nombre de la conexion JNDI, para verificar esta en aplicacion Resources
        return getDataSourceConnection(jdbcConn); // Nombre de la conexion JNDI, para verificar esta en aplicacion Resources
    }


    public String getValorLista(String campoVista, String campoLista, String iterador, BindingContainer bindings) {

        String valorCampo = null;

        AttributeBinding binding = (AttributeBinding) bindings.getControlBinding(campoVista);


        // To get the actual value, we need the binding to the iterator that got the list.
        DCIteratorBinding estadoListIter = ((DCBindingContainer) bindings).findIteratorBinding(iterador);

        if (binding.getInputValue() != null) {
            // Then we find the row of the list that is pointed to by the index.
            Row estadoRow = estadoListIter.getRowAtRangeIndex(((Integer) (binding.getInputValue())).intValue());

            // And get the attribute for the value from that row.
            Object valor = estadoRow.getAttribute(campoLista);
            if (valor != null) {
                valorCampo = valor.toString();
            } else {
                valorCampo = " ";
            }

        }

        return valorCampo;

    }

    public static Date convertirStringDate(String dateInString) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = formatter.parse(dateInString);
            System.out.println("## date " + date);
            System.out.println("## format " + formatter.format(date));
            return date;
        } catch (ParseException e) {
            System.out.println("## Error: " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Date convertirStringDate2(String dateInString) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = formatter.parse(dateInString);
            System.out.println("## date " + date);
            System.out.println("## format " + formatter.format(date));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Date convertirStringDateHoras(String dateInString) {
         String oldFormat = "yyyy-MM-dd HH:mm:ss";
        String newFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf1 = new SimpleDateFormat(oldFormat);
         SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);


           try {
            
               System.out.println(sdf2.format(sdf1.parse(dateInString)));
               String nueva= sdf2.format(sdf1.parse(dateInString));
                return convertirStringDate2(nueva);

           } catch (ParseException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
           return null;
    }


    public static Date convertirStringDate3(String dateInString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(dateInString);
            System.out.println(date);
            System.out.println(formatter.format(date));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Integer d = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String getCadenaAlfanumAleatoria(int longitud) {
        String cadenaAleatoria = "";
        long milis = new java.util.GregorianCalendar().getTimeInMillis();
        Random r = new Random(milis);
        int i = 0;
        while (i < longitud) {
            char c = (char) r.nextInt(255);
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')) {
                cadenaAleatoria += c;
                i++;
            }
        }
        return cadenaAleatoria;
    }

    public static Connection getConnectionDS(String dsName) throws NamingException, SQLException {
        Connection con = null;
        DataSource datasource = null;

        Context initialContext = new InitialContext();
        if (initialContext == null) {
        }
        datasource = (DataSource) initialContext.lookup(dsName);
        if (datasource != null) {
            con = datasource.getConnection();
        } else {
            System.out.println("Failed to Find JDBC DataSource.");
        }
        return con;
    }

    

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public static String getTemplateDir() throws Exception {
        String mailFrom = null;
        String sql = "SELECT VALOR_ALF FROM REFERENCIAS WHERE DOMINIO = 'TEMP_DIR'";
        Connection conn = getConnection("jdbc/scpg");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                mailFrom = rs.getString(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (conn != null)
                conn.close();
        }
        return mailFrom;
    }

    

    public static void actualizarPass(String mail, String newPass) throws Exception {
        String update = "UPDATE USERS SET U_PASSWORD =? WHERE U_EMAIL = ?";
        Connection conn = getConnection("jdbc/ventas");
        try (PreparedStatement pre = conn.prepareStatement(update)) {
            pre.setString(1, newPass);
            pre.setString(2, mail);
            pre.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null)
                conn.close();
        }
    }

    public static String getContextName(HttpServletRequest origRequest) {
        String url =
            origRequest.getScheme() + "://" + origRequest.getServerName() + ":" + origRequest.getServerPort() + "/" +
            origRequest.getContextPath() + "/";

        return url;
    }

    public static void redirect(String destino) {
        try {
            System.out.println("##redireccionando##: " + destino);
            FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .redirect(destino);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    

    public static String validarContrasenha(String newPass, String newPassConfirm) {
        if (!newPass.equalsIgnoreCase(newPassConfirm)) {

            return "No coinciden las contrase�as ingresadas. Verifique";
        }
        if (newPass.length() < 8) {

            return "La contrase�a ingresada es insegura. Verifique los requisitos";
        }
        if (!newPass.matches("[0-9A-Za-z]*")) {

            return "La contrase�a ingresada es insegura. Verifique los requisitos";
        }
        return null;
    }

    

    public static void addScriptOnPartialRequest(String script) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (AdfFacesContext.getCurrentInstance().isPartialRequest(context)) {
            //System.out.println("Ejecutando javascript");
            ExtendedRenderKitService erks = Service.getRenderKitService(context, ExtendedRenderKitService.class);
            erks.addScript(context, script);
        }
    }

   

    public static String getRemoteIpAdress() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance()
                                                                  .getExternalContext()
                                                                  .getRequest();        
        return req.getRemoteAddr();
    }

    public static String getRemoteHost() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance()
                                                                  .getExternalContext()
                                                                  .getRequest();
        return req.getRemoteHost();
    }

    public static ViewObjectImpl traerVO(String iteratorName) {
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry(); //Edited
        DCBindingContainer bc = (DCBindingContainer) bindings;
        DCIteratorBinding iterator = bc.findIteratorBinding(iteratorName);
        return (ViewObjectImpl) iterator.getViewObject();
    }

   


    

    
    public static String getRowStatus(Row row){
     //EviEvidenciasViewIngresoRowImpl rwImpl = (EviEvidenciasViewIngresoRowImpl)row;
     ViewRowImpl rwImpl  =  (ViewRowImpl)row;
     
     String rwStatus = translateStatusToString(rwImpl.getEntity(0).getEntityState());
     return rwStatus;
    }

    private static String translateStatusToString(byte b) {
        String ret = null;
        switch (b) {
        case Entity.STATUS_INITIALIZED:
            {
                ret = "Initialized";
                break;
            }
        case Entity.STATUS_MODIFIED:
            {
                ret = "Modified";
                break;
            }
        case Entity.STATUS_UNMODIFIED:
            {
                ret = "Unmodified";
                break;
            }
        case Entity.STATUS_NEW:
            {
                ret = "New";
                break;
            }
        }
        return ret;
    }

    
    public static void createRow(Object[] r,String iterator)  {        
        FacesContext fctx = FacesContext.getCurrentInstance();
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding iter = bindings.findIteratorBinding(iterator);
        ViewObject vo = iter.getViewObject();
        Row rw = vo.createRow();
        for(int i = 0;i<rw.getAttributeCount();i++){
            try{                                        
                if (rw.isAttributeUpdateable(i))                
                    rw.setAttribute( i, r[i]);         
                //rw.setAttribute(i, r[i]);
            }
            catch (ReadOnlyAttrException roe) {
                continue;
            }
        }
        vo.insertRow(rw);        
        fctx.renderResponse();        
    }
    
    public static String estadoRow(String iterator)  {
        FacesContext fctx = FacesContext.getCurrentInstance();
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding iter = bindings.findIteratorBinding(iterator);
        Row rw = iter.getCurrentRow();         
        //Almacen temporal de datos
        if(rw != null){            
            return getRowStatus(rw);                                  
        }
        return null;
    }    
    
    public static Boolean tablaVacia(String iterator)  {
        FacesContext fctx = FacesContext.getCurrentInstance();
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding iter = bindings.findIteratorBinding(iterator);
        Row[] rws = iter.getAllRowsInRange();
        //Almacen temporal de datos
        if(rws.length > 0)
            return false;
        else
            return true;
        
    }        
    
    public static Long differenceMonths(java.util.Date fechaInicio, java.util.Date fechaFin){
        LocalDate inicio = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate fin = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        Period diff = Period.between(inicio, fin);    
//        System.out.println("Meses :" + diff.getMonths() + "Anho :" + diff.getYears());        
        long diffInDays = ChronoUnit.DAYS.between(inicio, fin);
        long diffInMonths = ChronoUnit.MONTHS.between(inicio, fin);
        long diffInYears = ChronoUnit.YEARS.between(inicio, fin);
        
        System.out.println("Diferencia dias-mes-anios:" + diffInDays + "-" + diffInMonths + "-" + diffInYears);
        
        //return diff.getMonths() + diff.getYears() * 12;
        return diffInMonths;
    }   
    
    public static Object getPageFlowVar(String name){
        return AdfFacesContext.getCurrentInstance().getPageFlowScope().get(name);
    }
    
    public static void setPageFlowVar(String name, Object value){
        AdfFacesContext.getCurrentInstance().getPageFlowScope().put(name, value);
    }
    
    public static int getCurrentYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        } catch(NullPointerException e) {
            return false;
        }        
        return true;
    }        
       
}
