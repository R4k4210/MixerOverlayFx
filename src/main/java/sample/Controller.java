package sample;

import chats.MixerChatsBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mixer.api.MixerAPI;
import com.mixer.api.resource.channel.MixerChannel;
import com.mixer.api.services.impl.ChannelsService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
	@FXML
	private CheckBox chkHideShow;
	@FXML
	private AnchorPane mainContainer;
	@FXML
	private SplitPane splitPane;
	@FXML
	private AnchorPane topAnchorOnSplitPane;
	@FXML
	private AnchorPane bottomAnchorOnSplitPane;
	@FXML
	private Button btnStart;



	public Controller(){

	}


	ArrayList<String> storedIds = new ArrayList<String>();
	ArrayList<TextFlow> chats = new ArrayList<TextFlow>();
	GridPane chat = new GridPane();

	boolean firstTimeCalledAPI = false;

	int addChatCount = 0;

	public void startChatting(MouseEvent mouseEvent) {

		final String username = txtUsername.getText().trim();

		if (username.isEmpty()) {

			TextFlow tx = new TextFlow(new Text("Mixer username is required."));
			scrPanel.setContent(tx);

		} else {

			addChatsToViewPanel();

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
						chats.add(chatToAdd);
					}

					System.out.println("Tamaño de StoredIDs: " + storedIds.size());
					System.out.println("Tamaño de Chats: " + chats.size());

				}
			};

			Timer timer = new Timer();
			timer.schedule(task, new Date(), 180000);

			firstTimeCalledAPI = true;
			System.out.println("Pongo el flag en true");
		}

	}


	public void addChatsToViewPanel(){

		//Add chats to text area every xxx seconds..

		TimerTask addChatTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {

						//System.out.println("Se agrega un textflow al scroll panel");
						TextFlow tx = new TextFlow(chats.get(addChatCount));
						tx.setBackground(Background.EMPTY);
						//chat.setBackground(Background.EMPTY);
						chat.addRow(addChatCount, tx);
						scrPanel.setContent(chat);
						scrPanel.setPadding(new Insets(6));
						//scrPanel.setPannable(true);

						addChatCount++;
					}
				});
			}
		};

		Timer chatTimer = new Timer();
		chatTimer.schedule(addChatTask, 20000, 1000);
/*
		if(firstTimeCalledAPI){
			if(chats.size() == addChatCount){
				addChatTask.cancel();
				firstTimeCalledAPI = false;
				System.out.println("Se cancela el llamado porque no tengo chats! Reinicio el flag para empezar denuevo");

			}
		}
*/
	}


	public void checkBoxValueChange(ActionEvent actionEvent) {
		boolean selected = chkHideShow.isSelected();
		if(selected){
			System.out.println("Seleccionado - " + selected);
			mainContainer.setBackground(Background.EMPTY);
			scrPanel.setBackground(Background.EMPTY);
			splitPane.setBackground(Background.EMPTY);
			topAnchorOnSplitPane.setBackground(Background.EMPTY);
			bottomAnchorOnSplitPane.setBackground(Background.EMPTY);
			btnStart.setBackground(Background.EMPTY);
			txtUsername.setBackground(Background.EMPTY);
			chat.setBackground(Background.EMPTY);
		}else{
			System.out.println("Desseleccionado - " + selected);
			mainContainer.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
			scrPanel.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
			splitPane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
			topAnchorOnSplitPane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
			bottomAnchorOnSplitPane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
			btnStart.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
			txtUsername.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
			chat.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
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
