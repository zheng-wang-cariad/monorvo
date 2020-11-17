package film.monovo.gui.order;

import film.monovo.manager.order.Order;
import film.monovo.manager.order.OrderStatus;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class OrderHeader extends HBox{
	public final Order order;
	public final CheckBox check = new CheckBox();
	public final OrderGUI gui;
	
	public OrderHeader(Order order, OrderGUI gui) {
		this.gui = gui;
		this.order = order;
		this.getChildren().add(check);
		check.setSelected(false);
		setTheCheckBox();
		this.getChildren().add(new Label(order.id));
		refresh();

		check.setOnAction( action -> {
			var selected = check.isSelected();
			if(selected) {
				this.gui.addToBatch(this);
			} else {
				this.gui.removeFromBatch(this);
			}
		});
	}

	public Long getTimestamp() { return this.order.timestamp; }

	public void setTheCheckBox(){
		check.setDisable(this.order.status == OrderStatus.CREATED || order.status == OrderStatus.IMPORTED);
	}
	
	public void refresh(){
		Color backgroundColor;
        if(this.order.status == OrderStatus.CREATED){
        		backgroundColor = Color.web("0xE5FFCC");
        } else if(this.order.status == OrderStatus.IMPORTED){
        		backgroundColor = Color.web("0xB2FF66");
        } else if(this.order.status ==  OrderStatus.PROCESSED){
        		backgroundColor = Color.web("0x08FF00");
        } else if(this.order.status == OrderStatus.PRINTED){
        		backgroundColor = Color.web("0xCCFFFF");
        } else if(this.order.status == OrderStatus.DELIVERY_READY){
        		backgroundColor = Color.web("0x66FFFF");
        } else if(this.order.status == OrderStatus.DELIVERED){
        		backgroundColor = Color.web("0x99CCFF");
        } else if(this.order.status == OrderStatus.CLOSED){
        		backgroundColor = Color.web("0xFFCCFF");
        } else {
                backgroundColor = Color.web("0xC0C0C0");
        }
        this.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
	}
}
