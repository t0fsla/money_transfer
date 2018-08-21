import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import moneytransfer.Application;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;

import javax.naming.NamingException;

import static com.google.common.collect.ImmutableMap.of;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class ITMoneyTransfer {

    private static final String API_URL = "http://localhost:4567";
    private static RequestSpecification builder = new RequestSpecBuilder()
            .setBaseUri(API_URL)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .setContentType("application/json")
            .build();

    @BeforeClass
    public static void beforeClass() throws NamingException {
        System.setProperty("devMode", "");
        Application.main(null);
        given().spec(builder).post("/test").then().statusCode(200);
    }

    @AfterClass
    public static void afterClass() {
        Spark.stop();
    }

    @Test
    public void transfer_can_be_done() {
        given().spec(builder).body(of("sourceAccountId", "acnt1", "destinationAccountId", "acnt2",
                "amount", of("value", "10", "currency", "EUR")))
                .post("/transfers").then().statusCode(200)
                .body("id", notNullValue())
                .body("amount.currency", equalTo("EUR"))
                .body("amount.value", equalTo("10.00"))
                .body("transactionTime", notNullValue());

        given().spec(builder).get("/accounts/{accountId}", "acnt1")
                .then().statusCode(200)
                .body("id", equalTo("acnt1"))
                .body("balance.value", equalTo("101.00"));
        given().spec(builder).get("/accounts/{accountId}", "acnt2")
                .then().statusCode(200)
                .body("id", equalTo("acnt2"))
                .body("balance.value", equalTo("232.00"));
    }

    @Test
    public void transfer_must_fail_if_source_account_amount_is_not_enough() {
        given().spec(builder).body(of("sourceAccountId", "acnt1", "destinationAccountId", "acnt2",
                "amount", of("value", "200", "currency", "EUR")))
                .post("/transfers").then().statusCode(400)
                .body("message", equalTo("Source account does not have enough amount"));
    }

    @Test
    public void currencies_of_accounts_and_amount_must_be_the_same() {
        given().spec(builder).body(of("sourceAccountId", "acnt1", "destinationAccountId", "acnt2",
                "amount", of("value", "200", "currency", "RUR")))
                .post("/transfers").then().statusCode(400)
                .body("message", equalTo("Could not make transfer in [RUR]from source account in [EUR]"));
    }
}
