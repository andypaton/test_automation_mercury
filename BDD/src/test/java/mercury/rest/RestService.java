package mercury.rest;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.http.ContentTooLongException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mercury.helpers.JsonHelper;

//import mercury.helpers.JsonHelper;


public class RestService {

    private static final Logger logger = LogManager.getLogger();
    private static final Logger loggerPerf = LogManager.getLogger("apiPerformanceLog");

    private String defaultResponseFormat = "json";
    private int statusCode = 0;
    private String responseFormat;
    private String mercuryCookie;
    private String responseAsString = null;
    private String requestVerificationToken;
    private CookieStore cookieStore;

    private static String previousEvent = null;



    /**
     * Add to API performance log file
     * @param method
     * @param endPoint
     * @param elapsedTime
     * @param endTime
     */
    private void logApiEvent (String method, String endPoint,  String payload, int status, Long elapsedTime, Long endTime) {
        String event = method.concat(endPoint).concat(elapsedTime.toString()).concat(endTime.toString());
        if (!event.equals(previousEvent)) {
            previousEvent = event;
            loggerPerf.info("PerfStats", method, endPoint, elapsedTime.toString(), endTime.toString());

            if (payload == null) {
                logger.debug("HTTP request ...\n\tURL     : " + endPoint + "\n\tMethod  : " + method + "\n\tResponse: " + status);
            } else {
                logger.debug("HTTP request ...\n\tURL     : " + endPoint + "\n\tMethod  : " + method + "\n\tPayload : " + payload + "\n\tResponse: " + status);
            }
        }
    }

    private HttpResponse sendHttpGet(HttpClient httpClient, HttpUriRequest request, HttpClientContext context) throws ClientProtocolException, IOException {
        try {
            Long startTime = System.currentTimeMillis();
            HttpResponse httpResponse = httpClient.execute(request, context);
            Long endTime = System.currentTimeMillis();
            Long elapsedTime = endTime - startTime;
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            logApiEvent("GET", request.getURI().toURL().toString(), null, statusCode, elapsedTime, endTime);
            return httpResponse;
        } catch (Exception e) {
            logger.debug("error " + e.getMessage());
            throw e;
        }
    }

    /**
     * Wrapper method for HTTP messages to capture performance stats
     * sendHTTPPost(httpClient, httpPost, context)
     *
     * @param httpClient
     * @param httpPost
     * @param context
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private HttpResponse sendHttpPost(HttpClient httpClient, HttpPost httpPost, HttpClientContext context) throws ClientProtocolException, IOException {
        try {
            Long startTime = System.currentTimeMillis();
            HttpResponse httpResponse = httpClient.execute(httpPost, context);
            Long endTime = System.currentTimeMillis();
            Long elapsedTime = endTime - startTime;

            String payload;
            try {
                payload = httpPost.getEntity() == null ? "None" : EntityUtils.toString(httpPost.getEntity());
            } catch (ContentTooLongException e) {
                payload = "Not logged: Content Too Long";
            }
            int statusCode = httpResponse.getStatusLine().getStatusCode();

            if ( ! httpPost.getURI().toURL().toString().contains("www.unserialize.me") ) {
                logApiEvent("POST", httpPost.getURI().toURL().toString(), payload, statusCode, elapsedTime, endTime);
            }

            return httpResponse;
        } catch (Exception e) {
            logger.debug("error " + e.getMessage());
            throw e;
        }
    }

    /**
     * Wrapper method for HTTP messages to capture performance stats
     * sendHTTPPut(httpClient, httpPut)
     *
     * @param httpClient
     * @param httpPut
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private HttpResponse sendHttpPut(HttpClient httpClient, HttpPut httpPut) throws ClientProtocolException, IOException {
        Long startTime = System.currentTimeMillis();
        HttpResponse httpResponse = httpClient.execute(httpPut);
        Long endTime = System.currentTimeMillis();
        Long elapsedTime = endTime - startTime;
        String payload = httpPut.getEntity() == null ? "None" : EntityUtils.toString(httpPut.getEntity());
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        logApiEvent("PUT", httpPut.getURI().toURL().toString(), payload, statusCode, elapsedTime, endTime);
        return httpResponse;
    }

    /**
     * Wrapper method for HTTP messages to capture performance stats
     * sendHTTPPatch(httpClient, httpPatch)
     *
     * @param httpClient
     * @param httpPatch
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private HttpResponse sendHttpPatch(HttpClient httpClient, HttpPatch httpPatch) throws ClientProtocolException, IOException {
        Long startTime = System.currentTimeMillis();
        HttpResponse httpResponse = httpClient.execute(httpPatch);
        Long endTime = System.currentTimeMillis();
        Long elapsedTime = endTime - startTime;
        String payload = httpPatch.getEntity() == null ? "None" : EntityUtils.toString(httpPatch.getEntity());
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        logApiEvent("PATCH", httpPatch.getURI().toURL().toString(), payload, statusCode, elapsedTime, endTime);
        return httpResponse;
    }

    /**
     * @summary This is used to determine is the user-defined format is one that is acceptable for use with REST services
     * @param format - string; used to determine the format of the response
     * @return true if the format is accepteable, false otherwise
     */
    private boolean valiateAcceptableFormat(String format) {
        if (format.equalsIgnoreCase("xml") || format.equalsIgnoreCase("json"))
            return true;
        return false;
    }

    /**
     * @summary This is used to set the format
     * @param format - string; used to determine the format of the response
     */
    public void setDefaultResponseFormat(String format) {
        if (valiateAcceptableFormat(format)) {
            this.defaultResponseFormat = format.toLowerCase();
        } else {
            throw new RuntimeException("Invalid response format entered. Acceptable formats are 'json' or 'xml'");
        }
    }

    /**
     * @summary This is used to retrieve the current default response format
     * @return Returns the response format as a string
     */
    public String getDefaultResponseFormat() {
        return defaultResponseFormat;
    }

    public String getResponseString() {
        return responseAsString;
    }

    public String sendGetRequest(String url, String cookies) throws ClientProtocolException, IOException{
        HttpUriRequest request = new HttpGet(url);
        request.setHeader("Cookie", cookies);

        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);

        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    public String sendPostRequestWithRedirection(String url, String payload, String cookies) throws ClientProtocolException, IOException{
        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpClientContext context = HttpClientContext.create();

        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Cookie", cookies);
        httpPost.addHeader("Accept" , "text/xml");

        if (payload != null) {
            httpPost.setEntity(new StringEntity(payload));
        }

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setCookieStore(context);
        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    public String sendPostRequestWithRedirectionStorePortal(String url, String payload) throws ClientProtocolException, IOException{

        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpClientContext context = HttpClientContext.create();

        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Accept" , "application/json, text/plain, */*");

        if (payload != null) {
            httpPost.setEntity(new StringEntity(payload));
        }

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setCookieStore(context);
        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    public String sendPostRequestWithRedirectionStorePortalBearerToken(String url, String payload, String token) throws ClientProtocolException, IOException{

        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpClientContext context = HttpClientContext.create();

        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept" , "application/json, text/plain, */*");
        httpPost.setHeader("Authorization", "bearer " + token);

        if (payload != null) {
            httpPost.setEntity(new StringEntity(payload));
        }

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setCookieStore(context);
        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    public String sendGetRequestBearerToken(String url, String token) throws ClientProtocolException, IOException{
        HttpUriRequest request = new HttpGet(url);
        request.setHeader("Authorization", "Bearer " + token);

        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);

        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    public String sendPostRequest(String url, String payload, String cookies) throws ClientProtocolException, IOException{


        HttpClient httpClient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();

        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Cookie", cookies);
        httpPost.addHeader("Accept" , "text/xml");

        if (payload != null) {
            httpPost.setEntity(new StringEntity(payload));
        }

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setRequestVerificationToken(context);
        setCookieStore(context);
        if (httpResponse.getEntity() != null) {
            responseAsString = EntityUtils.toString(httpResponse.getEntity());
        }

        return responseAsString;
    }

    public String sendPostRequestForMobileAuthentication(String url, String payload) throws ClientProtocolException, IOException{
        HttpClient httpClient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();

        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Accept" , "*/*");

        if (payload != null) {
            httpPost.setEntity(new StringEntity(payload));
        }

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setRequestVerificationToken(context);
        setCookieStore(context);
        if (httpResponse.getEntity() != null) {
            responseAsString = EntityUtils.toString(httpResponse.getEntity());
        }

        return responseAsString;
    }

    public String sendPostRequest(String url, String cookies) throws ClientProtocolException, IOException{
        return sendPostRequest(url, null, cookies);
    }

    public String sendPostRequest(HttpPost httpPost) throws ClientProtocolException, IOException{

        HttpClient httpClient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);//httpClient.execute(httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setRequestVerificationToken(context);
        setCookieStore(context);
        if (httpResponse.getEntity() != null) {
            responseAsString = EntityUtils.toString(httpResponse.getEntity());
        }

        return responseAsString;
    }

    public String sendPostJsonRequest(String url, String payload, String cookies) throws ClientProtocolException, IOException{

        HttpClient httpClient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Cookie", cookies);

        if (payload != null) {
            StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);
            httpPost.setEntity(entity);
        }

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);

        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    public String sendPutJsonRequest(String url, String payload, String cookies) throws ClientProtocolException, IOException{

        HttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-type", "application/json");
        httpPut.setHeader("Cookie", cookies);
        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        httpPut.setEntity(entity);

        HttpResponse httpResponse = sendHttpPut(httpClient, httpPut);
        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);

        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    public String sendPatchJsonRequest(String url, String payload, String cookies) throws ClientProtocolException, IOException{
        HttpClient httpClient = HttpClients.createDefault();
        HttpPatch httpPatch = new HttpPatch(url);
        httpPatch.setHeader("Accept", "application/json, text/plain, */*");
        httpPatch.setHeader("Content-type", "application/json");
        httpPatch.setHeader("Cookie", cookies);
        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);
        httpPatch.setEntity(entity);

        HttpResponse httpResponse = sendHttpPatch(httpClient, httpPatch);
        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);

        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    private void setStatusCode(HttpResponse httpResponse){
        statusCode = httpResponse.getStatusLine().getStatusCode();
    }

    public int getStatusCode(){
        return statusCode;
    }

    public String getResponseFormat(){
        return responseFormat;
    }

    public String getMercuryCookie(){
        return mercuryCookie;
    }

    private void setResponseFormat(HttpResponse httpResponse){
        responseFormat = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType().replace("application/", "");
    }

    private void setMercuryCookie(HttpClientContext context){
        for (Cookie cookie : context.getCookieStore().getCookies()) {
            if (cookie.getName().matches("(.*)MERCURY_.*_COOKIE") && (cookie.getDomain().matches("mercury.software") || cookie.getDomain().equals("localhost"))) {
                mercuryCookie = cookie.getName() + "=" + cookie.getValue();
                return;
            }
        }
    }

    private void setRequestVerificationToken(HttpClientContext context){
        for (Cookie cookie : context.getCookieStore().getCookies()) {
            if (cookie.getName().matches("__RequestVerificationToken")) {
                requestVerificationToken = cookie.getName() + "=" + cookie.getValue();
            }
        }
    }

    public String getRequestVerificationToken() {
        return requestVerificationToken;
    }


    public void addCookie(String name, String value, String domain) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);

        if (cookie.getDomain() == null) {
            cookie.setDomain(domain);
        }
        cookieStore.addCookie(cookie);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(HttpClientContext context) {
        this.cookieStore = context.getCookieStore();
    }


    public HttpResponse sendGetRequest(String url) throws ClientProtocolException, IOException{
        HttpUriRequest request = new HttpGet(url);
        HttpClientContext context = HttpClientContext.create();
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpResponse httpResponse = sendHttpGet(httpClient, request,context);


        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setCookieStore(context);
        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return httpResponse;
    }

    public String sendPostRequest(HttpPost httpPost, CookieStore cookieStore) throws ClientProtocolException, IOException{

        HttpClient httpClient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);


        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setRequestVerificationToken(context);
        setCookieStore(context);
        if (httpResponse.getEntity() != null) {
            responseAsString = EntityUtils.toString(httpResponse.getEntity());
        }

        return responseAsString;
    }

    public String sendPostRequest(String url, String payload, CookieStore cookieStore) throws ClientProtocolException, IOException{

        HttpClient httpClient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        HttpPost httpPost = new HttpPost(url);

        String contentType = JsonHelper.isJSONValid(payload) ?  "application/json" : "application/x-www-form-urlencoded";
        httpPost.addHeader("Content-type", contentType);

        String accept = JsonHelper.isJSONValid(payload) ?  "application/json" : "text/xml";
        httpPost.addHeader("Accept" , accept);

        if (payload != null) {
            httpPost.setEntity(new StringEntity(payload));
        }

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setRequestVerificationToken(context);
        setCookieStore(context);
        if (httpResponse.getEntity() != null) {
            responseAsString = EntityUtils.toString(httpResponse.getEntity());
        }

        return responseAsString;
    }

    public String sendPostRequestForMobileWithBearerToken(String url, String payload, String token) throws ClientProtocolException, IOException{
        HttpClient httpClient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Authorization", "bearer " + token);
        String contentType = JsonHelper.isJSONValid(payload) ?  "application/json" : "application/x-www-form-urlencoded";
        httpPost.addHeader("Content-type", contentType);

        String accept = JsonHelper.isJSONValid(payload) ?  "application/json" : "text/xml";
        httpPost.addHeader("Accept" , accept);

        if (payload != null) {
            httpPost.setEntity(new StringEntity(payload));
        }

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setRequestVerificationToken(context);
        setCookieStore(context);
        if (httpResponse.getEntity() != null) {
            responseAsString = EntityUtils.toString(httpResponse.getEntity());
        }

        return responseAsString;
    }

    public String sendGetRequest(String url, CookieStore cookieStore) throws ClientProtocolException, IOException{
        HttpUriRequest request = new HttpGet(url);

        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        request.addHeader("Content-type", "application/x-www-form-urlencoded");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request, context );

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setCookieStore(context);
        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    public String sendGetRequestNoRedirect(String url, CookieStore cookieStore) throws ClientProtocolException, IOException {
        HttpUriRequest request = new HttpGet(url);

        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        request.addHeader("Content-type", "application/x-www-form-urlencoded");
        HttpResponse httpResponse = HttpClientBuilder.create().disableRedirectHandling().build().execute(request, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setCookieStore(context);
        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    public String sendGetJSONRequest(String url, CookieStore cookieStore) throws ClientProtocolException, IOException{
        HttpUriRequest request = new HttpGet(url);

        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        request.addHeader("Content-type", "application/json");
        request.addHeader("Accept" ,  "application/json");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request, context );


        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setCookieStore(context);
        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    public String sendPostRequest(String url, CookieStore cookieStore) throws ClientProtocolException, IOException{

        HttpClient httpClient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Accept" , "text/xml");

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setRequestVerificationToken(context);
        setCookieStore(context);
        if (httpResponse.getEntity() != null) {
            responseAsString = EntityUtils.toString(httpResponse.getEntity());
        }
        return responseAsString;
    }

    public String sendPostRequestWithRedirection(String url, String payload, CookieStore cookieStore) throws ClientProtocolException, IOException{

        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        HttpPost httpPost = new HttpPost(url);

        String contentType = JsonHelper.isJSONValid(payload) ?  "application/json" : "application/x-www-form-urlencoded";
        httpPost.addHeader("Content-type", contentType);

        String accept = JsonHelper.isJSONValid(payload) ?  "application/json" : "text/xml";
        httpPost.addHeader("Accept" , accept);

        if (payload != null) {
            httpPost.setEntity(new StringEntity(payload));
        }

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setCookieStore(context);
        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    public String sendPostJsonRequestWithRedirection(String url, String payload, CookieStore cookieStore) throws ClientProtocolException, IOException{

        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        HttpPost httpPost = new HttpPost(url);

        String contentType = JsonHelper.isJSONValid(payload) ?  "application/json" : "application/x-www-form-urlencoded";
        httpPost.addHeader("Content-type", contentType);

        String accept = JsonHelper.isJSONValid(payload) ?  "application/json" : "text/xml";
        httpPost.addHeader("Accept" , accept);

        if (payload != null) {
            httpPost.setEntity(new StringEntity(payload));
        }

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setCookieStore(context);
        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    @SuppressWarnings("rawtypes")
    public String sendPostFileRequestWithRedirection(String url, String fileName, Map<String, Object> map,  CookieStore cookieStore) throws ClientProtocolException, IOException{

        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        HttpPost httpPost = new HttpPost(url);

        String boundary = UUID.randomUUID().toString();

        File file = new File(fileName);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setBoundary(boundary).
                addBinaryBody("FileToUpload", file, ContentType.create("application/pdf"), "smallquote.pdf");

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            entityBuilder.addPart(pair.getKey().toString(),  new StringBody(pair.getValue().toString(), ContentType.MULTIPART_FORM_DATA));
        }

        HttpEntity entity = entityBuilder.build();

        httpPost.setEntity(entity);
        httpPost.addHeader("Content-type", "multipart/form-data; boundary=" +boundary);
        httpPost.addHeader("Accept" , "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setCookieStore(context);
        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }

    @SuppressWarnings("rawtypes")
    private MultipartEntityBuilder createEntityBuilder(String boundary, Map<String, Object> formDataMap, List<Map<String, Object>> fileData) {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

        Iterator<Map<String, Object>> it = fileData.iterator();
        while (it.hasNext()) {
            Map<String, Object> map = it.next();
            entityBuilder.setBoundary(boundary);
            entityBuilder.addBinaryBody(String.valueOf(map.get("fieldNames")), new File((String) map.get("fileNamesWithPath")), ContentType.create("application/pdf"), String.valueOf(map.get("fileNames")));
        }

        Iterator<Entry<String, Object>> itEntry = formDataMap.entrySet().iterator();
        while (itEntry.hasNext()) {
            Map.Entry pair = itEntry.next();
            entityBuilder.addPart(pair.getKey().toString(),  new StringBody(pair.getValue().toString(), ContentType.MULTIPART_FORM_DATA));
        }
        return entityBuilder;
    }

    public String sendPostFilesRequestWithRedirection(String url, Map<String, Object> formDataMap,  List<Map<String, Object>> fileData, CookieStore cookieStore) throws ClientProtocolException, IOException{

        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        HttpPost httpPost = new HttpPost(url);

        String boundary = UUID.randomUUID().toString();

        MultipartEntityBuilder entityBuilder = createEntityBuilder(boundary, formDataMap, fileData);

        HttpEntity entity = entityBuilder.build();

        httpPost.setEntity(entity);
        httpPost.addHeader("Content-type", "multipart/form-data; boundary=" +boundary);
        httpPost.addHeader("Accept" , "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpPost.addHeader("Accept-Encoding", "gzip, deflate, br");
        httpPost.addHeader("Accept-Language", "en-US,en;q=0.9");

        HttpResponse httpResponse = sendHttpPost(httpClient, httpPost, context);

        setStatusCode(httpResponse);
        setResponseFormat(httpResponse);
        setMercuryCookie(context);
        setCookieStore(context);
        responseAsString = EntityUtils.toString(httpResponse.getEntity());
        return responseAsString;
    }
}

