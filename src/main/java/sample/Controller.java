package sample;

import chats.MixerChatsBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mixer.api.MixerAPI;
import com.mixer.api.resource.channel.MixerChannel;
import com.mixer.api.services.impl.ChannelsService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import mixer.api.operation.MixerApiOperation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class Controller {

	@FXML
	private TextField txtUsername;
	@FXML
	private ScrollPane scrPanel;

	public Controller(){

	}

	//scrollPane.vvalueProperty().bind(vBox.heightProperty());

	ArrayList<String> storedIds = new ArrayList<String>();
	ArrayList<TextFlow> chats = new ArrayList<TextFlow>();
	GridPane chat = new GridPane();

	int addChatCount = 0;

	public void startChatting(MouseEvent mouseEvent) {

		final String username = txtUsername.getText().trim();

		if (username.isEmpty()) {

			TextFlow tx = new TextFlow(new Text("Mixer username is required."));
			scrPanel.setContent(tx);

		} else {

			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					MixerAPI mixer = null;
					mixer = new MixerAPI("Click here to get your Client ID!");
					MixerChannel channel = null;

					try {
						channel = mixer.use(ChannelsService.class).findOneByToken(username).get();
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					} catch (ExecutionException ex) {
						ex.printStackTrace();
					}

					//65962121
					int channelId = channel.id;

					System.out.println(new Date());

					JsonArray jsonResponse = null;

					try {
						jsonResponse = MixerApiOperation.getHistoryByChannelId(channelId);
					} catch (IOException ex) {
						ex.printStackTrace();
					}

					for (JsonElement obj : jsonResponse) {

						double scrollWidth = scrPanel.getWidth();
						TextFlow chatToAdd = new TextFlow(MixerChatsBuilder.buildChatStringFromJSONObject(obj, storedIds, scrollWidth));
						chatToAdd.setPrefWidth(scrPanel.getWidth());
						System.out.println(chatToAdd);
						chats.add(chatToAdd);
					}

					System.out.println(new Date());

					System.out.println("Tamaño de StoredIDs: " + storedIds.size());
					System.out.println("Tamaño de Chats: " + chats.size());
				}
			};

			Timer timer = new Timer();
			timer.schedule(task, new Date(), 180000);

			//Add chats to text area every 10 seconds..

			TimerTask addChatTask = new TimerTask() {
				@Override
				public void run() {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {

							//System.out.println("Se agrega un textflow al scroll panel");
							TextFlow tx = new TextFlow(chats.get(addChatCount));
							chat.addRow(addChatCount, tx);
							scrPanel.setContent(chat);
							scrPanel.setPadding(new Insets(6));
							//scrPanel.setPannable(true);
							scrPanel.setVvalue(1);

							addChatCount++;
						}
					});
				}
			};

			Timer chatTimer = new Timer();
			chatTimer.schedule(addChatTask, 20000, 1000);


		}

	}


	public TextField getTxtUsername() {
		return txtUsername;
	}

	public void setTxtUsername(TextField txtUsername) {
		this.txtUsername = txtUsername;
	}

	public ScrollPane getScrPanel() {
		return scrPanel;
	}

	public void setScrPanel(ScrollPane scrPanel) {
		this.scrPanel = scrPanel;
	}

}
