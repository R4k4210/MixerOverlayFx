package sample;

import chats.MixerChatsBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class ConfigurationController {

	@FXML private AnchorPane settingContainer;
	@FXML private TextField txtChatDelay;
	@FXML private TextField txtCustomOpacity;
	@FXML private TextField txtApiCall;
	@FXML private Label lblError;
	@FXML private Label lblUsernameColor;
	@FXML private ColorPicker cpUsername;
	@FXML private ColorPicker cpChatText;
	@FXML private Button btnCloseSetting;
	@FXML private TextField txtUsernameFont;
	@FXML private TextField txtChatTextFont;
	
	public static Controller sampleController;
	private int chatDelay;
	private double opacity;
	private int apiCall;
	
	private String usernameColor;
	private String chatTextColor;
	private int usernameFontSize;
	private int chatFontSize;
	
	@FXML
	private void initialize(){
		chatDelay = sampleController.getAddChatDelay();
		opacity = sampleController.getCustomOpacity();
		apiCall = sampleController.getApiCallDelay();
		usernameColor = MixerChatsBuilder.getUsernameColor();
		chatTextColor = MixerChatsBuilder.getChatTextColor();
		usernameFontSize = MixerChatsBuilder.getUsernameFontSize();
		chatFontSize = MixerChatsBuilder.getChatFontSize();
		
		txtChatDelay.setText(String.valueOf(chatDelay));
		txtCustomOpacity.setText(String.valueOf(opacity));
		txtApiCall.setText(String.valueOf(apiCall));
		txtUsernameFont.setText(String.valueOf(usernameFontSize));
		txtChatTextFont.setText(String.valueOf(chatFontSize));
		cpUsername.setValue(Color.valueOf(usernameColor));
		cpChatText.setValue(Color.valueOf(chatTextColor));	
	}

	
	public void saveAppSettings(ActionEvent actionEvent) {
		boolean isValid = validateFields();
		
		if(isValid) {
			sampleController.setAddChatDelay(chatDelay);
			sampleController.setCustomOpacity(opacity);
			sampleController.setApiCallDelay(apiCall);
			MixerChatsBuilder.setChatTextColor(cpChatText.getValue().toString());
			MixerChatsBuilder.setUsernameColor(cpUsername.getValue().toString());
			MixerChatsBuilder.setChatFontSize(Integer.valueOf(txtChatTextFont.getText()));
			MixerChatsBuilder.setUsernameFontSize(Integer.valueOf(txtUsernameFont.getText()));
		}
		
	}
	
	public void closeSettings(ActionEvent actionEvent) {
		Stage stage = (Stage) btnCloseSetting.getScene().getWindow();
		stage.close();
	}
	
	private boolean validateFields() {
		boolean isValid = true;
		String errorMsg = "";
		
		try {
			chatDelay = Integer.parseInt(txtChatDelay.getText());
		}catch(NumberFormatException e){
			txtChatDelay.requestFocus();
			errorMsg += "Chat Delay must be a number (ej: 3000)" + "\n";
			isValid = false;
		}
		
		try {
			opacity = Double.parseDouble(txtCustomOpacity.getText());
			if(opacity > 1) {
				opacity = 1.0;
			}else if(opacity < 0){
				opacity = 0.0;
			}
		} catch (NumberFormatException e) {
			txtCustomOpacity.requestFocus();
			errorMsg += "Opacity must be a number between 0 and 1 (ej: 0.5)" + "\n";
			isValid = false;
		}

		try {
			apiCall = Integer.parseInt(txtApiCall.getText());
		} catch (NumberFormatException e) {
			txtApiCall.requestFocus();
			errorMsg += "API Delay must be a number (ej: 3000)";
			isValid = false;
		}	
		
		try {
			usernameFontSize = Integer.valueOf(txtUsernameFont.getText());
		}catch(NumberFormatException e) {
			txtUsernameFont.requestFocus();
			errorMsg += "Username Font must be numeric (ej: 12)";
			isValid = false;
		}
		
		try {
			chatFontSize = Integer.valueOf(txtChatTextFont.getText());
		}catch(NumberFormatException e) {
			txtChatTextFont.requestFocus();
			errorMsg += "Chat Text Font must be numeric (ej: 12)";
			isValid = false;
		}
		
		if(!isValid) {
			showError(errorMsg);
		}
		
		return isValid;
	}
	
	private void showError(String errorMsg) {
		lblError.setVisible(true);
		lblError.setTextFill(Paint.valueOf("RED"));
		lblError.setText(errorMsg);
	}

}
