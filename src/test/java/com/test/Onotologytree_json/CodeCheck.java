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


	@BeforeTest

	public void setup() throws Exception
	{
	DesiredCapabilities dc = DesiredCapabilities.chrome();
	       URL url = new URL("http://172.20.23.7:5555/wd/hub");
	       driver = new RemoteWebDriver(url, dc);
	       wait = new WebDriverWait(driver, 30);
	}

	@Test(priority=1)
	public void login() throws InterruptedException
	{
	driver.get("http://dev2meena.humanbrain.in/annotation/portal");
	driver.manage().window().maximize();
	  String currentURL = driver.getCurrentUrl();
	  System.out.println("Current URL: " + currentURL);
	WebDriverWait wait = new WebDriverWait(driver, 60);
	driver.switchTo().defaultContent(); // Switch back to default content
	WebElement viewerElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@title='Ontology Editor']")));
	if (viewerElement.isEnabled() && viewerElement.isDisplayed()) {
	   viewerElement.click();
	   System.out.println("Viewer icon is clicked");
	} else {
	   System.out.println("Viewer icon is not clickable");
	}


	  String parentWindow = driver.getWindowHandle();
	  WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()=' Log In ']")));
	  if (loginButton != null) {
	     loginButton.click();
	     System.out.println("Login button clicked successfully.");
	  } else {
	     System.out.println("Login button is not clicked.");
	  }

	wait.until(ExpectedConditions.numberOfWindowsToBe(2));
	   Set<String> allWindows = driver.getWindowHandles();
	  for (String window : allWindows) {
	     if (!window.equals(parentWindow)) {
	         driver.switchTo().window(window);
	         break;
	     }
	  }
	  WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='email']")));
	  if (emailInput != null && emailInput.isDisplayed()) {
	     emailInput.sendKeys("teamsoftware457@gmail.com");
	     System.out.println("Email was entered successfully.");
	  } else {
	    System.out.println("Email was not entered.");
	  }


	  WebElement nextButton1 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Next']")));
	  if (nextButton1 != null) {
	     nextButton1.click();
	     System.out.println("Next button 1 is clicked.");
	  } else {
	     System.out.println("Next button 1 is not clicked.");
	  }
	 
	  WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@aria-label='Enter your password']")));
	  passwordInput.sendKeys("Health#123");
	  if (passwordInput.getAttribute("value").equals("Health#123")) {
	     System.out.println("Password was entered successfully.");
	  } else {
	     System.out.println("Password was not entered.");
	  }

	 
	  WebElement nextButton2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Next']")));
	  if (nextButton2 != null) {
	     nextButton2.click();
	     System.out.println("Next button 2 is clicked.");
	  } else {
	     System.out.println("Next button 2 is not clicked.");
	  }
	 
	  driver.switchTo().window(parentWindow);
	  System.out.println("Login successfully");
	 
	  System.out.println("************************Login validation done***********************");      
	}

	@Test(priority=2)

	public  void StatusCheck() {
//	    String apiUrl = "http://dev2mani.humanbrain.in:8000/GW/getOntology/";
	   String apiUrl = "http://dev2meena.humanbrain.in:8000/GW/getOntology/";


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
	           JSONArray apiResponse = new JSONArray(new JSONTokener(response.body()));

	           // Initialize map to track validity status for each app_annotation_tree_name
	           Map<String, Boolean> validityMap = new HashMap<>();
	           
	           System.out.println(apiResponse);
	           

	           // Loop through each item in the API response
	           for (int i = 0; i < apiResponse.length(); i++) {
	               JSONObject item = apiResponse.getJSONObject(i);

	               String annotationName = item.optString("app_annotation_tree_name");
	           
	               String colorJson = item.optString("app_annotation_tree_json");
	               JSONObject newMy = new JSONObject(colorJson);
	               
	               boolean annotationValid = true;

	               // Check if hex color code is valid
	                    JSONArray msgArray = newMy.getJSONArray("msg");

	               for (int j = 0; j < msgArray.length(); j++) {
	                        JSONObject msgItem = msgArray.getJSONObject(j);
	                        String hexColor = msgItem.optString("color_hex_triplet");
	                        boolean isValidHexColor = isValidHexColor(hexColor);

	                        // Print details which we got
	                        System.out.println("Annotation Name: " + annotationName);
	                        System.out.println("Hex Color code: " + hexColor);
	                        System.out.println("Is Hex Color code valid: " + isValidHexColor);
	                        System.out.println("-".repeat(30));
	                       
	                        annotationValid &= isValidHexColor;
	                       
	                     // Check if "children" key exists
	                        if (msgItem.has("children")) {
	                            // Accessing the "children" array within "msg" array
	                            JSONArray childrenArray = msgItem.getJSONArray("children");
	                            for (int k = 0; k < childrenArray.length(); k++) {
	                                JSONObject childItem = childrenArray.getJSONObject(k);
	                                String childHexColor = childItem.optString("color_hex_triplet");
	                                boolean isValidChildHexColor = isValidHexColor(childHexColor);

	                                // Print details for child items
	                                System.out.println("Child Item Hex Color code: " + childHexColor);
	                                System.out.println("Is Child Item Hex Color code valid: " + isValidChildHexColor);
	                                annotationValid &= isValidChildHexColor;                                
	                               
	                                // the below one will print color with the annotation name
	                                if (!isValidChildHexColor) {
	                                    System.out.println("Invalid hex color found for annotation " + annotationName + ": " + childHexColor);
	                                }
	                                System.out.println("-".repeat(30));

	                            }
	                        }
	                    }
	                    validityMap.put(annotationName, annotationValid);
	               
	           }

	           // Print final result for each app_annotation_tree_name
	           for (Map.Entry<String, Boolean> entry : validityMap.entrySet()) {
	               String annotationName = entry.getKey();
	               boolean isValid = entry.getValue();
	               if (isValid) {
	                   System.out.println("All hex color codes are valid for: " + annotationName);
	               } else {
	                   System.out.println("At least one hex color code is invalid for: " + annotationName);
	               }
	           }
	       } else {
	           System.out.println("Failed to retrieve data. HTTP Status: " + response.statusCode());
	       }
	   } catch (Exception e) {
	       e.printStackTrace();
	   }
	}


	// Function to check if a hex color code is valid
	private static boolean isValidHexColor(String hexColor) {
	   // Check if the hex color code has less than 6 characters

	   return hexColor.matches("^[0-9A-Fa-f]{6}$") || hexColor.matches("^#[0-9A-Fa-f]{6}$") ;

	}
	   
	   @AfterTest
	   public void afterTest() {
	    driver.quit();
	   }

}
