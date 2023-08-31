package steps;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ContactListSteps {
    private RequestSpecification request;
    private Response response;
    private String token;
    private String contactId;

    @Given("the base uri is {string}")
    public void theBaseUrlIs(String baseUri){
        RestAssured.baseURI = baseUri;
        request = RestAssured.given();
    }

    @Given("the user has the list of contacts")
    public void theUserHasTheListOfContacts() {
        request.header(new Header("Authorization", "Bearer " + token));
        response = request.get("/contacts");
    }

    @When("the user deletes the second contact")
    public void theUserDeletesTheSecondContact() {
        contactId = response.jsonPath().get("[1]._id");
        request.header(new Header("Authorization", "Bearer " + token));
        request.contentType("application/json");
        response = request.delete("/contacts/" + contactId);
    }

    @Given("a new user is created")
    public void aNewUserIsCreated() {
        String firstName = UUID.randomUUID().toString().substring(0, 6);
        String lastName = UUID.randomUUID().toString().substring(0, 6);
        String email = UUID.randomUUID().toString().substring(0, 6) + "@gmail.com";
        String password = "myPassword";

        String requestBody = "{\n" +
                "    \"firstName\": \"" + firstName + "\",\n" +
                "    \"lastName\": \"" + lastName + "\",\n" +
                "    \"email\": \""+ email +"\",\n" +
                "    \"password\": \""+ password +"\"\n" +
                "}";
        request.contentType("application/json");
        request.body(requestBody);
        response = request.post("/users");
        this.token = response.jsonPath().getString("token");
    }

    @Given("{string} contacts are created")
    public void contactsAreCreated(String noOfContacts) {

        int loop = Integer.parseInt(noOfContacts);
        for (int i = 0; i < loop; i++){
            String firstName = UUID.randomUUID().toString().substring(0, 6);
            String lastName = UUID.randomUUID().toString().substring(0, 6);
            String email = UUID.randomUUID().toString().substring(0, 6) + "@gmail.com";
            String requestBody = "{\n" +
                "    \"firstName\": \"" + firstName + "\",\n" +
                "    \"lastName\": \"" + lastName +"\",\n" +
                "    \"birthdate\": \"1970-01-01\",\n" +
                "    \"email\": \"" + email +"\",\n" +
                "    \"phone\": \"8005555555\",\n" +
                "    \"street1\": \"1 Main St.\",\n" +
                "    \"street2\": \"Apartment A\",\n" +
                "    \"city\": \"Anytown\",\n" +
                "    \"stateProvince\": \"KS\",\n" +
                "    \"postalCode\": \"12345\",\n" +
                "    \"country\": \"USA\"\n" +
                "}";
            request.header(new Header("Authorization", "Bearer " + token));
            request.contentType("application/json");
            request.body(requestBody);
            response = request.post("/contacts");
        }
    }

    @Then("the contacts list size is {string}")
    public void theContactsListSizeIs(String expectedListSize) {
        request.header(new Header("Authorization", "Bearer " + token));
        response = request.get("/contacts");
        List<Object> contactList = response.jsonPath().getList("");
        assertEquals(Integer.parseInt(expectedListSize), contactList.size());
    }

    @Then("the contact must not be in the list of contacts")
    public void theContactMustNotBeInTheListOfContacts() {
        request.header(new Header("Authorization", "Bearer " + token));
        response = request.get("/contacts");
        List<String> contactIdList = response.jsonPath().getList("_id");
        assertFalse(contactIdList.contains(contactId));
    }
}
