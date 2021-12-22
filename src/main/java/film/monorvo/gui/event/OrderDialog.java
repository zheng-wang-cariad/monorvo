package film.monorvo.gui.event;

import film.monorvo.manager.event.EventChain;
import film.monorvo.manager.event.EventType;
import film.monorvo.manager.order.Customer;
import film.monorvo.manager.order.Order;
import film.monorvo.manager.order.OrderStatus;
import film.monorvo.util.BerlinerTime;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;

public class OrderDialog extends Dialog<Order> {
	private final TextField id = new TextField();
	private final TextField street = new TextField();
	private final TextField houseNumber = new TextField();
	private final TextField name = new TextField();
	private final TextField additional = new TextField();
	private final TextField city = new TextField();
	private final TextField postCode = new TextField();
	private final TextField country = new TextField();
	private final TextField email = new TextField();
	private final EventGUI gui;
	private Order order;

	
	private GridPane grid = new GridPane();


	public OrderDialog(EventGUI gui) {
		this.gui = gui;
		this.setTitle("Create a new order");
		this.setResizable(false);
		
		grid.add(new Label("id:"), 1, 1);
		this.add(id, 2, 1, 200);
		
		grid.add(new Label("name:"), 1, 3);
		this.add(name, 2, 3, 400);
		
		grid.add(new Label("Street/No:"), 1, 4);
		this.add(street, 2, 4, 400);
		this.add(houseNumber, 3, 4, 60);
		
		grid.add(new Label("additional address:"), 1, 5);
		this.add(additional, 2, 5, 400);
		
		grid.add(new Label("City/PostCode:"), 1, 6);
		this.add(city, 2, 6, 300);
		this.add(postCode, 3, 6, 100);

		grid.add(new Label("Country:"), 1, 7);
		this.add(country, 2, 7, 100);
		
		grid.add(new Label("email:"), 1, 8);
		this.add(email, 2, 8, 400);
		
		//grid.add(create, 3, 9);
		this.getDialogPane().setContent(grid);
		
		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().add(buttonTypeOk);
		this.setResultConverter(b -> {
			if(b == buttonTypeOk) {
				this.createOrder();
			}
				return this.order;
			}
		);
	}

	private void createOrder() {
		try {
			if(id.getText().isBlank()) throw new IllegalArgumentException("order id should not be empty");
			if(name.getText().isBlank()) throw new IllegalArgumentException("customer name should not be empty");
			if(street.getText().isBlank()) throw new IllegalArgumentException("street should not be empty");
			//if(houseNumber.getText().isBlank()) throw new IllegalArgumentException("houseNumber should not be empty");
			if(postCode.getText().isBlank()) throw new IllegalArgumentException("post code should not be empty");
			if(city.getText().isBlank()) throw new IllegalArgumentException("city should not be empty");
			if(country.getText().isBlank()) throw new IllegalArgumentException("country should not be empty");
			if(email.getText().isBlank()) throw new IllegalArgumentException("email should not be empty");

		} catch (Exception e) {
			showErrorAlert(e);
			this.order = null;
			this.close();
		}
		var c = new Customer();
		c.addressAdditional = this.additional.getText();
		c.city = this.city.getText();
		c.country = this.country.getText();
		c.email = this.email.getText();
		c.houseNumber = this.houseNumber.getText();
		c.name = this.name.getText();
		c.postCode = this.postCode.getText();
		c.streetName = this.street.getText();
			
		this.order = new Order(id.getText(), BerlinerTime.nowTimeMilli(), OrderStatus.CREATED, c);
		this.close();
		
	}
	
	private void showErrorAlert(Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(e.getMessage());
		alert.showAndWait();
	}
	
	
	private void add(Control c, int x, int y, int width) {
		c.setPrefWidth(width);
		grid.add(c, x, y);
	}

	public OrderDialog(EventChain chain, EventGUI gui) {
		this(gui);
		if(!chain.event.from.contains("transaction@etsy.com")) return;

		try {
			Document doc = Jsoup.parse(new File(this.getHtmlPath(chain)), "utf-8");
			id.setText(this.getOrderId(doc));
			street.setText(getAddressElement(doc, "first-line"));
			name.setText(getAddressElement(doc, "name"));
			city.setText(getAddressElement(doc, "city"));
			postCode.setText(getAddressElement(doc, "zip"));
			country.setText(getAddressElement(doc, "country-name"));
			email.setText(getEmail(doc));
			this.gui.eventView.updateCurrentEventCreated();
		} catch(Exception e) {
			id.setText("");
			street.setText("");
			houseNumber.setText("");
			name.setText("");
			additional.setText("");
			city.setText("");
			postCode.setText("");
			country.setText("");
			email.setText("");
		}

	}

	private String getHtmlPath(EventChain chain){
		var content = chain.event.contents.stream().filter(it -> it.type == EventType.HTML)
				.findFirst();
		if(content.isPresent()) {
			return content.get().content;
		}
		throw new RuntimeException();

	}

	private String getEmail(Document doc) {
		var elements = doc.select("a:contains(Sende dem Käufer eine E-Mail)");
		for (Element e: elements) {
			return e.attr("href").toString().replace("mailto:", "");
		}
		throw new RuntimeException();
	}

	private String getOrderId(Document doc) {
		var elements = doc.select("div:contains(Bestellnummer:)").select("a[href]");
		for (Element e: elements) {
			return e.childNodes().get(0).toString();
		}
		throw new RuntimeException();
	}

	private static String getAddressElement(Document doc, String name) {
		var elements = doc.getElementsByClass(name);
		for (Element e: elements) {
			return e.childNodes().get(0).toString();
		}
		return "";
	}
	
}
