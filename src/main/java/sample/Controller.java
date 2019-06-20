package sample;

import chats.MixerChatsBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mixer.api.MixerAPI;
import com.mixer.api.resource.channel.MixerChannel;
import com.mixer.api.services.impl.ChannelsService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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
	@FXML
	private Button btnCancel;



	public Controller(){

	}

	@FXML
	private void initialize(){
		normalStyle();
	}


	public ArrayList<String> storedIds = new ArrayList<String>();
	public ArrayList<TextFlow> chats = new ArrayList<TextFlow>();
	public GridPane chat = new GridPane();
	public TimerTask addChatTask = null;
	public TimerTask task = null;

	boolean firstTimeCalledAPI = false;

	int addChatCount = 0;

	public void startChatting(MouseEvent mouseEvent) {

		final String username = txtUsername.getText().trim();

		if (username.isEmpty()) {

			TextFlow tx = new TextFlow(new Text("Mixer username is required."));
			scrPanel.setContent(tx);

		} else {

			task = new TimerTask() {

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

				}
			};

			Timer timer = new Timer();
			timer.schedule(task, new Date(), 180000);

			addChatsToViewPanel();
			changeStatusButtonStart(1);
			changeStatusButtonCancel(0);
		}

	}

	public void cancelChatting(MouseEvent mouseEvent) {
		task.cancel();
		addChatTask.cancel();
		changeStatusButtonStart(0);
		changeStatusButtonCancel(1);
	}


	public void addChatsToViewPanel(){

		//Add chats to text area every xxx seconds..
		addChatTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {

						//If counter is equal to the number of chats on array, stop the task
						if((chats.size() - 1) == addChatCount){
							stopAddChatTask(addChatTask);
						}

						//System.out.println("Se agrega un textflow al scroll panel");
						TextFlow tx = new TextFlow(chats.get(addChatCount));
						tx.setBackground(Background.EMPTY);
						//chat.setBackground(Background.EMPTY);
						chat.addRow(addChatCount, tx);
						scrPanel.setContent(chat);
						//Automatic scroll to bottom
						chat.heightProperty().addListener(new ChangeListener() {

							@Override
							public void changed(ObservableValue observable, Object oldvalue, Object newValue) {

								scrPanel.setVvalue((Double)newValue );
							}
						});

						System.out.println("Tamaño de StoredIDs: " + storedIds.size());
						System.out.println("Tamaño de Chats: " + chats.size());
						System.out.println("Tamaño del contador: " + addChatCount);

						addChatCount++;
					}
				});
			}
		};

		Timer chatTimer = new Timer();
		chatTimer.schedule(addChatTask, 20000, 1000);

	}

	public static void stopAddChatTask(TimerTask addChatTask){
		System.out.println("Se cancela el Timer porque no hay mas chats!");
		addChatTask.cancel();
	}

	public void checkBoxValueChange(ActionEvent actionEvent) {
		boolean selected = chkHideShow.isSelected();
		if(selected){
			transparentStyle();
		}else{
			normalStyle();
		}

	}

	private void transparentStyle(){
		mainContainer.setBackground(Background.EMPTY);
		scrPanel.setBackground(Background.EMPTY);
		splitPane.setBackground(Background.EMPTY);
		topAnchorOnSplitPane.setBackground(Background.EMPTY);
		bottomAnchorOnSplitPane.setBackground(Background.EMPTY);
		btnStart.setBackground(Background.EMPTY);
		txtUsername.setBackground(Background.EMPTY);
		chat.setBackground(Background.EMPTY);
		btnCancel.setBackground(Background.EMPTY);
	}

	private void normalStyle(){
		splitPane.setDividerPosition(20, 20);
		scrPanel.setPadding(new Insets(6));
		scrPanel.setPannable(true);
		scrPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mainContainer.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		scrPanel.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		splitPane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		topAnchorOnSplitPane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		bottomAnchorOnSplitPane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		btnStart.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
		txtUsername.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		chat.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		btnCancel.setBackground(new Background(new BackgroundFill(Color.LIGHTCORAL, CornerRadii.EMPTY, Insets.EMPTY)));
	}

	private void changeStatusButtonStart(int status){
		if(status == 1){
			btnStart.setDisable(true);
			btnStart.setVisible(false);
		}else{
			btnStart.setDisable(false);
			btnStart.setVisible(true);
		}

	}

	private void changeStatusButtonCancel(int status){
		if(status == 0){
			btnCancel.setVisible(true);
			btnCancel.setDisable(false);
		}else{
			btnCancel.setVisible(false);
			btnCancel.setDisable(true);
		}

	}

}
