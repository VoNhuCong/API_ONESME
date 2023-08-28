package com.vmt.api;

import com.ftl.sql.Database;
import com.ftl.util.Log;
import com.ftl.wak.mgr.DataSourceManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vmt.model.SafeChildrenResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.vmt.model.ChildChartData;
import org.json.JSONObject;

@Path("/")
public class SafeChildrenAPI {

    private static final Logger logger = Log.getLogger();

    private final Gson gson = new Gson();

    @Context
    private HttpServletRequest request;

    public SafeChildrenAPI() {
        try {
        } catch (Exception ex) {
            logger.log(Level.WARNING, "error with {0}", ex.getMessage());
        }
    }

    private Response validation() throws Exception {
        if (!this.request.getHeader("Content-Type").contains("application/json")) {
            String resError = "{\n\t\"code\": \"404\"\n\t\"desc\": \"Bad request. Mostly due to incorrect in requested body or Content-Type is not\napplication/json\"\n}";
            return Response.status(Response.Status.BAD_REQUEST).entity(resError).build();
        }
        String authorized = this.request.getHeader("token");
        if (authorized == null || !authorized.equals("NUG4hHQkHMwzGjMd"))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/transaction")
    @Produces({ "application/json" })
    public Response transaction(InputStream data) throws Exception {
        Response validation = validation();
        if (validation.getStatus() != 200) {
            return validation;
        }
        String body = getBody(request);
        JsonObject jsonReq = new Gson().fromJson(body, JsonObject.class);
        SafeChildrenResponse res = new SafeChildrenResponse();
        try (Connection cn = DataSourceManager.getInstance().getDataSource().getConnection()) {
            saveRequest(cn, jsonReq);
            res.setCode("200");
            res.setDesc("ok");
        } catch (Exception e) {
            e.printStackTrace();
            res.setCode("-1");
            res.setDesc("Exeption: " + e.getMessage());
        }
        return Response.status(Response.Status.OK).entity(this.gson.toJson(res)).build();
    }
    
     // api get data chart
    @GET
    @Path("/dataChart")
    @Produces({" application/json "})
    public Response getDataChart (InputStream data) throws Exception{
        String response = "{\n\t\"code\":\"200\"\n\t\"status\":\"url doesn't exist\"\n}";
        String url = request.getParameter("url");
        String ip = request.getParameter("ip");
        Connection cn = DataSourceManager.getInstance().getDataSource().getConnection();
        String resultFromDb = null;
        //ChildChartData res = new ChildChartData();
        SafeChildrenResponse res = new SafeChildrenResponse();
        try{
            IP_UrlDetail(url, ip, cn);
            resultFromDb = getUrlDetail(url, cn);
            ChildChartData result = new ChildChartData(resultFromDb);
            JSONObject item = result.getData_parent();
            if(item != null || !"".equals(item))
                response = item.toString();
            res.setCode("200");
            res.setDesc(response);
        }catch(Exception ex){
            res.setCode("-1");
            res.setDesc("Exeption: " + ex.getMessage()); 
        }   
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @GET
    @Path("/urldetail")
    @Produces({ "application/json" })
    public Response getUrlDetail(InputStream data) throws Exception {
        String response = "{\n\t\"code\": \"200\"\n\t\"status\": \"url doesn't exist\"\n}";
        String url = request.getParameter("url");
        String ip = request.getParameter("ip");
        Connection cn = DataSourceManager.getInstance().getDataSource().getConnection();
        String resultFromDb = null;
        SafeChildrenResponse res = new SafeChildrenResponse();
        try{
            IP_UrlDetail(url, ip, cn);
            resultFromDb = getUrlDetail(url, cn);
            if(resultFromDb != null || !"".equals(resultFromDb))
                response = resultFromDb;
            res.setCode("200");
            res.setDesc(response);
        }catch(Exception e){
            res.setCode("-1");
            res.setDesc("Exeption: " + e.getMessage());
        }
        return Response.status(Response.Status.OK).entity(this.gson.toJson(res)).build();
    }
    
    
    private String IP_UrlDetail(String url, String ip, Connection cn) throws Exception {
        String sql = "insert into URL_SEARCH_HISTORY ( IP       ,\n"  +
                "   URL          ,\n" +
                "   TIME         ,\n" +
                "   CYBER_RES      )" +
                "   valuse "          +  "(?,?,sysdate,?)";
        PreparedStatement stmt = null;
        Timestamp timestamp = Timestamp.now();
        try{
            stmt = cn.prepareStatement(sql);
            int i = 1;
            stmt.setString(i++, ip);
            stmt.setString(i++, url);
            stmt.setTime(i++, timestamp);
            stmt.setString(i++, req.toString());
            stmt.execute();
        }finally{
            Database.closeObject(stmt);
        }   
    } 

    public void saveRequest(Connection cn, JsonObject req) throws Exception {
        String sql = "insert into HOST_MALICIOUS (   UUID               ,\n" +
                "   HOST               ,\n" +
                "   STATUS_DISPLAY     ,\n" +
                "   SCORE              ,\n" +
                "   ALCOHOL_CIGARETTE  ,\n" +
                "   DRUG_HEROIN        ,\n" +
                "   GORY_HORROR        ,\n" +
                "   NEUTRAL            ,\n" +
                "   PORN               ,\n" +
                "   SEXY               ,\n" +
                "   WEAPON             ,\n" +
                "   HUMAN              ,\n" +
                "   RELATED            ,\n" +
                "   CREATED_AT         ,\n" +
                "   CREATED            ,\n" +
                "   REQ_RAW            )" +
                " values " +
                "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?)";
        PreparedStatement stmt = null;
        try {
            stmt = cn.prepareStatement(sql);
            int i = 1;
            stmt.setString(i++, req.get("uuid").getAsString());
            stmt.setString(i++, req.get("host").getAsString());
            stmt.setString(i++, req.get("status_display").getAsString());
            JsonObject jsonScore = req.get("scores").getAsJsonObject();
            stmt.setString(i++, jsonScore.get("score").getAsString());
            stmt.setString(i++, jsonScore.get("alcohol_cigarette").getAsString());
            stmt.setString(i++, jsonScore.get("drug_heroin").getAsString());
            stmt.setString(i++, jsonScore.get("gory_horror").getAsString());
            stmt.setString(i++, jsonScore.get("neutral").getAsString());
            stmt.setString(i++, jsonScore.get("porn").getAsString());
            stmt.setString(i++, jsonScore.get("sexy").getAsString());
            stmt.setString(i++, jsonScore.get("weapon").getAsString());
            stmt.setString(i++, jsonScore.get("human").getAsString());
            stmt.setString(i++, jsonScore.get("related").getAsString());
            stmt.setString(i++, req.get("created_at").getAsString());
            stmt.setString(i++, req.toString());
            stmt.executeUpdate();
        } finally {
            Database.closeObject(stmt);
        }
    }

    private String getUrlDetail(String url, Connection cn) throws Exception {
        JsonObject urldetail = new JsonObject();
        PreparedStatement stmt = null;
        try {
            String query = "SELECT * FROM HOST_MALICIOUS WHERE HOST_MALICIOUS.HOST = ?";
            stmt = cn.prepareStatement(query);
            stmt.setString(1, url);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                urldetail.addProperty("host", rs.getString(2));
                urldetail.addProperty("status_display", rs.getString(3));
                urldetail.addProperty("alcohol_cigarette", rs.getString(5));
                urldetail.addProperty("drug_heroin", rs.getString(6));
                urldetail.addProperty("gory_horror", rs.getString(7));
                urldetail.addProperty("neutral", rs.getString(8));
                urldetail.addProperty("porn", rs.getString(9));
                urldetail.addProperty("sexy", rs.getString(10));
                urldetail.addProperty("weapon", rs.getString(11));
            }
            rs.close();
            stmt.close();
            return (urldetail.get("host") == null) ? url : urldetail.toString();
        } finally {
            Database.closeObject(stmt);
        }
    }

    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }
}
