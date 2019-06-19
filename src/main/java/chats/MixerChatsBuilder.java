package chats;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import sample.Controller;

import java.util.ArrayList;
import java.util.Random;

public class MixerChatsBuilder {

	public static TextFlow buildChatStringFromJSONObject (JsonElement element, ArrayList<String> storedIds, double scrollWidth){

		String[] fontColors = {"RED", "BLACK", "BLUE", "GREEN", "ORANGE"};
		Random r = new Random();
		int randomNumber = r.nextInt((fontColors.length-1 - 0) + 1) + 0;
		String randomColorSelected = fontColors[randomNumber];

		double scrollWidthSize = scrollWidth;
		JsonElement rawJsonElement = element;
		String chat = "";
		//Image defaultAvatar = new Image("/icons/user-default.png");
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
			System.out.println("El ID no está en el Array por lo tanto se agrega.");
			storedIds.add(chatId);

			//Get raw chat
			JsonObject messageObject = object.get("message").getAsJsonObject();
			//Set default avatar if user haven't one.
			//avatar = defaultAvatar;
			if(!object.get("user_avatar").toString().isEmpty()){
				String partialAvatar = cleanSymbols(object.get("user_avatar").toString());
				avatar = new Image(partialAvatar);
			}
			imgView.setImage(avatar);
			System.out.println("avatar URL -" +object.get("user_avatar").toString());
			//Get username from raw object
			String partialUsername = cleanSymbols(object.get("user_name").toString());
			username = new Text(space + partialUsername  + " -" + space);
			username.setTextAlignment(TextAlignment.CENTER);
			username.setFont(Font.font ("Verdana", FontWeight.BOLD,16));
			username.setFill(Paint.valueOf(randomColorSelected));
			//Get the text from the raw message
			String partialMsg = cleanSymbols(messageObject.get("message").getAsJsonArray().get(0).getAsJsonObject().get("text").toString());
			msg = new Text(partialMsg);
			msg.setTextAlignment(TextAlignment.CENTER);
			msg.setFont(Font.font ("Verdana", FontWeight.BOLD,13));
			msg.setFill(Paint.valueOf(randomColorSelected));


			//Add all to text flow
			finalChat = new TextFlow(imgView, username, msg);
			finalChat.setPrefWidth(scrollWidthSize - 5);

		}else{
			System.out.println("El ID ya se encuentra en el Array.");
		}

		return finalChat;
	}

	private static String cleanSymbols(String text){
		String cleanedText = text.replace("\"", "");
		return cleanedText;
	}
}
