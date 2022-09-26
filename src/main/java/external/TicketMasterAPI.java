package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;


public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "tAhQCSw8OtweOr2OS7Q9AANrDllJL15J";
	
	
	public List<Item> search(double lat, double lon, String keyword) {
		
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		
		try {
			// encoded keyword in url since it may contain special characters
			keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		// convert lat/lon to geo hash
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
		
		// Make the url query
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", 
				API_KEY, geoHash, keyword, 80);
		
		try {
			
			// Open a HTTP connection between java app and ticketMaster based on url
			// openconnection ==> URLConnection ==> HttpURLConnection
			HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();
			
			/**
			 * Send request to TicketMaster and get response, the response code could be return directly
			 * Response body is saved in InputStream of connection
			 */
			int responseCode = connection.getResponseCode();
			
			System.out.println("\nSending 'GET' request to URL: " + URL + "?" + query);
			System.out.println("Response code: " + responseCode);
			
			// Read response body to get event data
			// buffered reader means read line by line
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String inputLine;
			StringBuilder response = new StringBuilder();
			
			// in.readLine means read one line then append it to stringbuilder
			while((inputLine = in.readLine())!=null) {
				response.append(inputLine);
			}
			
			// Close bufferReader
			in.close();
			
			// Convert response JSON
			JSONObject obj = new JSONObject(response.toString());
			
			// _embedded store events list
			if (obj.isNull("_embedded")) {
				return new ArrayList<>();
			}
			
			// get JSONObject
			JSONObject embedded = obj.getJSONObject("_embedded");
			
			// Get events array
			JSONArray events = embedded.getJSONArray("events"); 
			
			// Convert JSONArrays to list
			// ItemList store event objects
			return getItemList(events);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
        return new ArrayList<>();
	}
	
	private void queryAPI(double lat, double lon) {
		List<Item> events = search(lat, lon, null);
		try {
		    for (Item event: events) {
		        System.out.println(event.toJSONObject());
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

  /*
  {
	    "name": "cinema ",
                  "id": "12345",
                  "url": "www.xxxxx.com",
	    ...
	    "_embedded": {
		    "venues": [
		        {
			        "address": {
			           "line1": "101 First St,",
			           "line2": "Suite 101",
			           "line3": "...",
			        },
			        "city": {
			        	"name": "San Francisco"
			        }
			        ...
		        },
		        ...
		    ]
	    }
	    ...
	  }
	 */
	
	// Extract address information
	private String getAddress(JSONObject event) throws JSONException{
		
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				
				// 避免数组第一个是空
				for (int i = 0; i < venues.length(); i++) {
					JSONObject venue = venues.getJSONObject(i);
					
					StringBuilder sb = new StringBuilder();
					
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						
						if (!address.isNull("line1")) {
							sb.append(address.getString("line1"));
						}
						if (!address.isNull("line2")) {
							sb.append(" ");
							sb.append(address.getString("line2"));
						}
						if (!address.isNull("line3")) {
							sb.append(" ");
							sb.append(address.getString("line3"));
						}
					}
					
					if (!venue.isNull("city")) {						
						JSONObject city = venue.getJSONObject("city");
						
						if (!city.isNull("name")) {
							sb.append(" ");
							sb.append(city.getString("name"));
						}	
					}
					if (!sb.toString().equals("")) {
						return sb.toString();
					}				
 				}
			}
		}
				
		return "";
	}
	
	// Extract categories infor
	private Set<String> getCategories(JSONObject event) throws JSONException{
		
		Set<String> categories = new HashSet<>();
		
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			
			for (int i = 0; i < classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject(i);
				
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					
					if (!segment.isNull("name")) {
						String name = segment.getString("name");
						categories.add(name);
					}
				}
			}
		}
		return categories;
	}
	
	// {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
	private String getImageUrl(JSONObject event) throws JSONException{
		if (!event.isNull("images")) {
			JSONArray images = event.getJSONArray("images");
			
			for (int i = 0; i < images.length(); i++) {
				JSONObject image = images.getJSONObject(0);
				
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return " ";
	}
	
	private String getLocalDate(JSONObject event) throws JSONException{
		if (!event.isNull("dates")) {
			JSONObject dates = event.getJSONObject("dates");
			
			if (!dates.isNull("start")) {
				JSONObject start = dates.getJSONObject("start");
				
				if (!start.isNull("localDate")) {
					return start.getString("localDate");
				}			
			}			
		}
		return " ";
	}
	
	
	private List<Item> getItemList(JSONArray events) throws JSONException{
		List<Item> itemList = new ArrayList<>();
		
		 for (int i = 0; i < events.length(); i++) {
			 
			 // Get enents one by one
			 JSONObject event = events.getJSONObject(i);
			 
			 // User builder design pattern
			 ItemBuilder builder = new ItemBuilder();
			 
			 if (!event.isNull("name")) {
				 builder.setName(event.getString("name"));
			 }
			 
			 if (!event.isNull("id")) {
				 builder.setItemId(event.getString("id"));
			 }
			 
			 if (!event.isNull("url")) {
				 builder.setUrl(event.getString("url"));
			 }
			 
			 // api update: remove rating
			 if (!event.isNull("rating")) {
				 builder.setRating(event.getDouble("rating"));
			 }
			 
			 if (!event.isNull("distance")) {
				 builder.setDistance(event.getDouble("distance"));
			 }
			 
			 builder.setCategories(getCategories(event));
			 builder.setAddress(getAddress(event));
			 builder.setImageUrl(getImageUrl(event));
			 
			 builder.setLocalDate(getLocalDate(event));
			 
			 itemList.add(builder.build());
		 }
		
		return itemList;
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		TicketMasterAPI mApi = new TicketMasterAPI();
		mApi.queryAPI(53.48, -113.51);	
	}


	
	
}


