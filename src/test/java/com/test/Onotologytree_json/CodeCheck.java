package com.test.Onotologytree_json;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class CodeCheck {

private RemoteWebDriver driver;
private WebDriverWait wait;


			@Test(priority=1)
			
			    public void PixelAnnotation() {
			        testAnnotationValidity("PixelAnnotation");
			    }
			
			@Test(priority=2)
			    public void Atlas() {
			        testAnnotationValidity("Atlas");
			    }
			
			@Test(priority=3)
			    public void FlatTree() {
			        testAnnotationValidity("FlatTree");
			    }
			
			@Test(priority=4)
			    public void fetalatlas_v124() {
			        testAnnotationValidity("fetalatlas_v124");
			    }
			
			@Test(priority=5)
			    public void ImageQc() {
			        testAnnotationValidity("ImageQc");
			    }
			
			@Test(priority=6)
			    public void CellCenter() {
			        testAnnotationValidity("CellCenter");
			    }
			
			@Test(priority=7)
			    public void New_Ontologys() {
			        testAnnotationValidity("New Ontologys");
			    }
			
			@Test(priority=8)
			    public void Fetal_Atlas_V1_40() {
			        testAnnotationValidity("Fetal_Atlas_V1.40");
			    }
			
			@Test(priority=9)
			    public void Superannotate_FB3() {
			        testAnnotationValidity("Superannotate_FB3");
			    }
			
			@Test(priority=10)
			    public void flatnomenclature_v105() {
			        testAnnotationValidity("flatnomenclature_v105");
			    }
			
			@Test(priority=11)
			    public void fetalatlas_test() {
			        testAnnotationValidity("fetalatlas_test");
			    }
			
			@Test(priority=12)
			    public void Superannotate_FB3_First_Hemisphere() {
			        testAnnotationValidity("Superannotate_FB3_First_Hemisphere");
			    }
			
			@Test(priority=13)
			    public void Cell_classification_Rakic() {
			        testAnnotationValidity("Cell classification Rakic");
			    }
			
			@Test(priority=14)
			    public void Registration() {
			        testAnnotationValidity("Registration");
			    }
			
			    private void testAnnotationValidity(String annotationName) {
			        String apiUrl = "https://apollo2.humanbrain.in/GW/getOntology/";
			
			        // Create an instance of HttpClient
			        HttpClient client = HttpClient.newHttpClient();
			
			        // Create an HTTP request
			        HttpRequest request = HttpRequest.newBuilder()
			                .uri(URI.create(apiUrl))
			                .build();
			
			        try {
			            // Send the request and get the response
			            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			            if (response.statusCode() == 200) {
			                // Parse the JSON response
			                JSONArray apiResponse = new JSONArray(response.body());
			
			                // Search for the annotation by name in the API response
			                for (int i = 0; i < apiResponse.length(); i++) {
			                    JSONObject item = apiResponse.getJSONObject(i);
			
			                    String responseAnnotationName = item.optString("app_annotation_tree_name");
			
			                    // Check if the response contains the desired annotation
			                    if (responseAnnotationName.equals(annotationName)) {
			                        JSONObject annotationJson = new JSONObject(item.optString("app_annotation_tree_json"));
			                        boolean isValid = checkAnnotationValidity(annotationName, annotationJson);
			
			                        // Assert the validity of the annotation
			                        Assert.assertTrue(isValid, "Annotation " + annotationName + " has invalid hex color.");
			                        return; // Exit the loop if annotation found and tested
			                    }
			                }
			                // If the desired annotation is not found in the response
			                Assert.fail("Annotation " + annotationName + " not found in API response.");
			            } else {
			                Assert.fail("Failed to retrieve data. HTTP Status: " + response.statusCode());
			            }
			        } catch (Exception e) {
			            Assert.fail("Exception occurred: " + e.getMessage());
			        }
			    }
			
			    private boolean checkAnnotationValidity(String annotationName , JSONObject annotationJson) {
			        boolean annotationValid = true;
			
			        JSONArray msgArray = annotationJson.getJSONArray("msg");
			
			        for (int j = 0; j < msgArray.length(); j++) {
			            JSONObject msgItem = msgArray.getJSONObject(j);
			            String hexColor = msgItem.optString("color_hex_triplet");
			
			            // Check hex color validity
			            boolean isValidHexColor = isValidHexColor(hexColor);
			
			            // Update annotation validity
			            annotationValid &= isValidHexColor;
			
			            // If the hex color is invalid, print a message
			            if (!isValidHexColor) {
			            String name = msgItem.getString("text");
			                System.out.println("Invalid hex color found for Annotation Name " + annotationName + "," + name + " : " + hexColor);
			            }
			
			            // Check hex color for children, if present
			            if (msgItem.has("children")) {
			                JSONArray childrenArray = msgItem.getJSONArray("children");
			                for (int k = 0; k < childrenArray.length(); k++) {
			                    JSONObject childItem = childrenArray.getJSONObject(k);
			                    String childHexColor = childItem.optString("color_hex_triplet");
			
			                    // Check hex color validity for child
			                    boolean isValidChildHexColor = isValidHexColor(childHexColor);
			
			                    // Update annotation validity
			                    annotationValid &= isValidChildHexColor;
			
			                    // If the hex color is invalid for child, print a message
			                    if (!isValidChildHexColor) {
			                    String name = childItem.optString("name");
			                        System.out.println("Invalid hex color found for Annotation Name " + annotationName + "," + name + " : " + childHexColor);                    }
			                }
			            }
			        }
			
			        return annotationValid;
			    }
			
			
			// Function to check if a hex color code is valid
			private static boolean isValidHexColor(String hexColor) {
			   // Check if the hex color code has less than 6 characters
			
			   return hexColor.matches("^#?[0-9a-fA-F]{6}$");
			}
			   
			   @AfterTest
			   public void afterTest() {
			    driver.quit();
			   }
			
			}

