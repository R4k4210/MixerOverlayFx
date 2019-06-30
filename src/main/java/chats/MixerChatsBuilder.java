package chats;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import java.util.ArrayList;
import static java.nio.charset.StandardCharsets.*;

public class MixerChatsBuilder {
	
	private static String usernameColor = "GREEN";
	private static String chatTextColor = "WHITE";
	private static int usernameFontSize = 14;
	private static int chatFontSize = 12;
	
	public static TextFlow buildChatStringFromJSONObject (JsonElement element, ArrayList<String> storedIds, double scrollWidth){

		double scrollWidthSize = scrollWidth;
		JsonElement rawJsonElement = element;
		Image defaultAvatar = new Image("/user.png");
		String space = " ";
		Text msg = null;
		Text username = null;
		Image avatar = null;
		ImageView imgView = new ImageView();
		imgView.setFitHeight(25);
		imgView.setFitWidth(25);
		TextFlow finalChat = null;

		//Obtain text from message array on position 0
		JsonObject object = new Gson().fromJson(rawJsonElement.toString(), JsonObject.class);

		//Guardo el ID
		String chatId = object.get("id").toString();
		
		if(!storedIds.contains(chatId)){
			storedIds.add(chatId);
			//Get raw chat
			JsonObject messageObject = object.get("message").getAsJsonObject();
			//Set default avatar if user haven't one.
			avatar = defaultAvatar;
			if(!object.get("user_avatar").toString().isEmpty()){
				String partialAvatar = cleanSymbols(object.get("user_avatar").toString());
				try {
					avatar = new Image(partialAvatar);
				}catch (Exception e){
				}
			}
			imgView.setImage(avatar);
			//Get username from raw object
			String partialUsername = cleanSymbols(object.get("user_name").toString());
			username = new Text(space + partialUsername  + " -" + space);
			username.setTextAlignment(TextAlignment.CENTER);
			username.setFont(Font.font ("Verdana", FontWeight.BOLD, usernameFontSize));
			username.setFill(Paint.valueOf(usernameColor));
			
			//Get the text from the raw message
			String partialMsg = ""; 
			
			partialMsg = cleanSymbols(messageObject.get("message").getAsJsonArray().get(0).getAsJsonObject().get("text").toString());
			
			if(messageObject.get("message").getAsJsonArray().size() > 1) {
				if(messageObject.get("message").getAsJsonArray().get(1) != null) {
					partialMsg += cleanSymbols(messageObject.get("message").getAsJsonArray().get(1).getAsJsonObject().get("text").toString());
					if(messageObject.get("message").getAsJsonArray().get(2) != null) {
						partialMsg += cleanSymbols(messageObject.get("message").getAsJsonArray().get(2).getAsJsonObject().get("text").toString());
					}
				}
			}
			
			byte[] ptext = partialMsg.getBytes(ISO_8859_1); 
			String encodedMsg = new String(ptext, UTF_8);
			
			
			msg = new Text(encodedMsg);
			msg.setTextAlignment(TextAlignment.CENTER);
			msg.setFont(Font.font ("Verdana", FontWeight.BOLD, chatFontSize));
			msg.setFill(Paint.valueOf(chatTextColor));
				
			//Add all to text flow
			finalChat = new TextFlow(imgView, username, msg);
			finalChat.setPrefWidth(scrollWidthSize - 10);

		}else{
			return null;
		}
		
		return finalChat;
	}

	private static String cleanSymbols(String text){
		String cleanedText = text.replace("\"", "");
		return cleanedText;
	}


	//GETTERS AND SETTERS

	public static String getUsernameColor() {
		return usernameColor;
	}

	public static void setUsernameColor(String usernameColor) {
		MixerChatsBuilder.usernameColor = usernameColor;
	}

	public static String getChatTextColor() {
		return chatTextColor;
	}

	public static void setChatTextColor(String chatTextColor) {
		MixerChatsBuilder.chatTextColor = chatTextColor;
	}

	public static int getUsernameFontSize() {
		return usernameFontSize;
	}

	public static void setUsernameFontSize(int usernameFontSize) {
		MixerChatsBuilder.usernameFontSize = usernameFontSize;
	}

	public static int getChatFontSize() {
		return chatFontSize;
	}

	public static void setChatFontSize(int chatFontSize) {
		MixerChatsBuilder.chatFontSize = chatFontSize;
	}


	
}
