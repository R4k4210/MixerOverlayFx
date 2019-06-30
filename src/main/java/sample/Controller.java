package sample;

import chats.MixerChatsBuilder;
import javafx.scene.Parent;
import javafx.scene.Scene;

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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mixer.api.operation.MixerApiOperation;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Controller {

	@FXML private TextField txtUsername;
	@FXML private ScrollPane scrPanel;
	@FXML private CheckBox chkHideShow;
	@FXML private AnchorPane mainContainer;
	@FXML private SplitPane splitPane;
	@FXML private AnchorPane topAnchorOnSplitPane;
	@FXML private AnchorPane bottomAnchorOnSplitPane;
	@FXML private Button btnStart;
	@FXML private Button btnCancel;
	@FXML private Button btnClose;
	@FXML private MenuBar mnbMenu;
	@FXML private MenuItem btmMenuItem;
	@FXML private AnchorPane opacityWindows;

	private Stage stage = Main.getPrimaryStage();

	public Controller(){

	}

	@FXML
	private void initialize(){
		normalStyle();
		chat.setPadding(new Insets(0, 10, 0, 0));
	}

	public ArrayList<String> storedIds = new ArrayList<String>();
	public ArrayList<TextFlow> chats = new ArrayList<TextFlow>();
	public GridPane chat = new GridPane();
	public TimerTask addChatTask = null;
	public TimerTask task = null;
	private boolean addChatTaskIsStopped = false;
	private int chatLimit = 5000;
	private boolean isTransparent;
	private double customOpacity = 1;
	private int addChatDelay = 1000;
	private int apiCallDelay = 5000;

	int addChatCount = 0;

	public void startChatting(MouseEvent mouseEvent) {

		final String username = txtUsername.getText().trim();

		if (username.isEmpty()) {

			TextFlow tx = new TextFlow(new Text("Mixer username is required."));
			scrPanel.setContent(tx);

		} else {
			
			MixerAPI mixer = new MixerAPI("Click here to get your Client ID!");;
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
			
			task = new TimerTask() {

				@Override
				public void run() {
					
					JsonArray jsonResponse = null;

					try {
						jsonResponse = MixerApiOperation.getHistoryByChannelId(channelId);
					} catch (IOException ex) {
						ex.printStackTrace();
						System.out.println("Error getting chat history \n" + ex);
					}
					System.out.println("Empieza = " + new Date());
					for (JsonElement obj : jsonResponse) {

						double scrollWidth = scrPanel.getWidth();

						try {
							TextFlow chatToAdd = new TextFlow(MixerChatsBuilder.buildChatStringFromJSONObject(obj, storedIds, scrollWidth));
							if(chatToAdd != null) {
								chatToAdd.setPrefWidth(scrPanel.getWidth());
								chats.add(chatToAdd);
							}
						}catch (Exception e){
							//System.out.println("Error building TextFlow String \n" + e);
						}

					}
					System.out.println("Finaliza = " + new Date());
					if(addChatTaskIsStopped){
						if((chats.size() - 1) > addChatCount){
							System.out.println("chats size greater than counter, resume add chat task.");
							addChatTaskIsStopped = false;
							addChatsToViewPanel();
						}
					}

				}
			};

			Timer timer = new Timer();
			timer.schedule(task, new Date(), apiCallDelay);

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
		chats.clear();
		chat.getChildren().clear();
		addChatCount = 0;
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
							System.out.println("chats equals counter, stopping chat task.");
							stopAddChatTask(addChatTask);
						}else {
							if(addChatCount >= chatLimit){
								System.out.println("counter greater than chatLimit, reseting application.");
								chat.getChildren().clear();
								clearAllArraysAndStartAgain();
							}else{
								if(addChatCount > chats.size()) {
									addChatCount = 0;
									System.out.println("counter and chat size are equals, reset counter.");
								}
								try {
									
									TextFlow tx = new TextFlow(chats.get(addChatCount));
									tx.setBackground(Background.EMPTY);
									tx.setPadding(new Insets(6));
									chat.addRow(addChatCount, tx);
									scrPanel.setContent(chat);
									
								}catch(Exception e) {
									e.printStackTrace();
								}
								//Automatic scroll to bottom
								chat.heightProperty().addListener(new ChangeListener() {

									@Override
									public void changed(ObservableValue observable, Object oldvalue, Object newValue) {

										scrPanel.setVvalue((Double) newValue);
									}
								});

								addChatCount++;
							}

						}
					}
				});
			}
		};

		Timer chatTimer = new Timer();
		chatTimer.schedule(addChatTask, new Date(), addChatDelay);

	}

	public void stopAddChatTask(TimerTask addChatTask){
		addChatTask.cancel();
		addChatTaskIsStopped = true;
	}

	public void checkBoxValueChange(ActionEvent actionEvent) {
		boolean selected = chkHideShow.isSelected();
		if(selected){
			btnCancel.setDisable(true);
			btnStart.setDisable(true);
			btnClose.setDisable(true);
			txtUsername.setDisable(true);
			transparentStyle();
		}else{
			btnCancel.setDisable(false);
			btnStart.setDisable(false);
			btnClose.setDisable(false);
			txtUsername.setDisable(false);
			normalStyle();
		}

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

	private void clearAllArraysAndStartAgain(){
		//Stop all tasks
		task.cancel();
		addChatTask.cancel();

		//Reindexing elements
		chats.clear();
		storedIds.clear();

		addChatCount = 0;
		//Resume Operations
		task.run();
		addChatsToViewPanel();
	}
	
	
	public void openSettingsWindows(ActionEvent actionEvent) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("configuration.fxml"));
		Parent root = (Parent) loader.load();
        Stage stage = new Stage();
        stage.setTitle("Settings");
        stage.setScene(new Scene(root, 400, 400));
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
		
	}


	public void closeApp(ActionEvent actionEvent) {

		if(task != null){
			task.cancel();
		}
		if(addChatTask != null){
			addChatTask.cancel();
		}

		Platform.exit();

	}
	
	private void transparentStyle(){
		opacityWindows.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		opacityWindows.setOpacity(customOpacity);
		opacityWindows.setMouseTransparent(true);
		mainContainer.setBackground(Background.EMPTY);
		scrPanel.setBackground(Background.EMPTY);
		scrPanel.setMouseTransparent(true);
		splitPane.setBackground(Background.EMPTY);
		topAnchorOnSplitPane.setBackground(Background.EMPTY);
		bottomAnchorOnSplitPane.setBackground(Background.EMPTY);
		btnStart.setBackground(Background.EMPTY);
		txtUsername.setBackground(Background.EMPTY);
		chat.setBackground(Background.EMPTY);
		btnCancel.setBackground(Background.EMPTY);
		btnClose.setBackground(Background.EMPTY);
		mnbMenu.setBackground(Background.EMPTY);
		mnbMenu.setMouseTransparent(true);

		//Disable windows movement
		isTransparent = false;
	}

	private void normalStyle(){
		scrPanel.setPannable(true);
		scrPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrPanel.setMouseTransparent(false);
		opacityWindows.setMouseTransparent(false);
		opacityWindows.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		mainContainer.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		scrPanel.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		splitPane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		topAnchorOnSplitPane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		bottomAnchorOnSplitPane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		btnStart.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
		txtUsername.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		chat.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		btnCancel.setBackground(new Background(new BackgroundFill(Color.LIGHTCORAL, CornerRadii.EMPTY, Insets.EMPTY)));
		btnClose.setBackground(new Background(new BackgroundFill(Color.INDIANRED, CornerRadii.EMPTY, Insets.EMPTY)));
		mnbMenu.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
		mnbMenu.setMouseTransparent(false);

		//Enable to move the windows
		isTransparent = true;
	}



	//Getters and Setters

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

	public CheckBox getChkHideShow() {
		return chkHideShow;
	}

	public void setChkHideShow(CheckBox chkHideShow) {
		this.chkHideShow = chkHideShow;
	}

	public AnchorPane getMainContainer() {
		return mainContainer;
	}

	public void setMainContainer(AnchorPane mainContainer) {
		this.mainContainer = mainContainer;
	}

	public SplitPane getSplitPane() {
		return splitPane;
	}

	public void setSplitPane(SplitPane splitPane) {
		this.splitPane = splitPane;
	}

	public AnchorPane getTopAnchorOnSplitPane() {
		return topAnchorOnSplitPane;
	}

	public void setTopAnchorOnSplitPane(AnchorPane topAnchorOnSplitPane) {
		this.topAnchorOnSplitPane = topAnchorOnSplitPane;
	}

	public AnchorPane getBottomAnchorOnSplitPane() {
		return bottomAnchorOnSplitPane;
	}

	public void setBottomAnchorOnSplitPane(AnchorPane bottomAnchorOnSplitPane) {
		this.bottomAnchorOnSplitPane = bottomAnchorOnSplitPane;
	}

	public Button getBtnStart() {
		return btnStart;
	}

	public void setBtnStart(Button btnStart) {
		this.btnStart = btnStart;
	}

	public Button getBtnCancel() {
		return btnCancel;
	}

	public void setBtnCancel(Button btnCancel) {
		this.btnCancel = btnCancel;
	}

	public Button getBtnClose() {
		return btnClose;
	}

	public void setBtnClose(Button btnClose) {
		this.btnClose = btnClose;
	}

	public boolean isTransparent() {
		return isTransparent;
	}

	public void setTransparent(boolean transparent) {
		isTransparent = transparent;
	}

	public double getCustomOpacity() {
		return customOpacity;
	}

	public void setCustomOpacity(double customOpacity) {
		this.customOpacity = customOpacity;
	}

	public int getAddChatDelay() {
		return addChatDelay;
	}

	public void setAddChatDelay(int addChatDelay) {
		this.addChatDelay = addChatDelay;
	}

	public int getApiCallDelay() {
		return apiCallDelay;
	}

	public void setApiCallDelay(int apiCallDelay) {
		this.apiCallDelay = apiCallDelay;
	}

	public AnchorPane getOpacityWindows() {
		return opacityWindows;
	}

	public void setOpacityWindows(AnchorPane opacityWindows) {
		this.opacityWindows = opacityWindows;
	}
	
	
	
}
