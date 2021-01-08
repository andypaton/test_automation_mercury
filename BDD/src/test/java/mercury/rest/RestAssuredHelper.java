package mercury.rest;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.WebDriver;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;


public class RestAssuredHelper {

    private static Cookies cookies;

    public void setCookies(WebDriver driver) {
        Set<org.openqa.selenium.Cookie> seleniumCookies = driver.manage().getCookies();

        // This is where the Cookies will live going forward
        List<Cookie> restAssuredCookies = new ArrayList<>();

        // Simply pull all the cookies into Rest-Assured
        for (org.openqa.selenium.Cookie cookie : seleniumCookies) {
            restAssuredCookies.add(new io.restassured.http.Cookie.Builder(cookie.getName(), cookie.getValue()).build());
        }

        cookies = new Cookies(restAssuredCookies);
    }

    public static Response getJson(String url) {
        assertNotNull("Rest Assured cookies not set!!!", cookies);

        RestAssured.defaultParser = Parser.JSON;

        RequestSpecification reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addCookies(cookies)
                .build();

        return given().spec(reqSpec)
                .when().get(url)
                .then().statusCode(200).contentType(ContentType.JSON).extract().response();
    }

}
