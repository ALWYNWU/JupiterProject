package rpc;

import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class RpcHelper {
	
	// Writes a JSONObject to http response
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) {
		try {
			
			//Set response content type
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = response.getWriter();
			
			// Return obj to frontend
			out.print(obj);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Writes a JSONArray to http response
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) {
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			

			PrintWriter out = response.getWriter();
			out.print(array);
			
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Parse a JSONObject from http request
	//前端发送请求到后端 解析这个request 然后传给itemHistory中的doPost函数
	public static JSONObject readJsonObject(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		
		try {
			BufferedReader reader = request.getReader();
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			reader.close();
			return new JSONObject(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	

}
