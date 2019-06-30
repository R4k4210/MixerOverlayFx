package mixer.api.operation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MixerApiOperation {

	public static JsonArray getHistoryByChannelId(int id) throws IOException {

		String urlPath = "https://mixer.com/api/v1/chats/"+id+"/history";
		URL url = new URL(urlPath);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestMethod("GET");
		int status = con.getResponseCode();
		JsonArray jsonArray = null;
		//System.out.println("Status: " + status);

		if(status == 200){
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}

			Gson gson = new Gson();
			jsonArray = gson.fromJson(content.toString(), JsonArray.class);

			in.close();

			con.disconnect();
		}

		return jsonArray;
	}
}
