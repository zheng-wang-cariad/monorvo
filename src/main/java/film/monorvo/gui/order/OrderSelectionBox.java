package film.monorvo.gui.order;

import film.monorvo.manager.order.Order;
import film.monorvo.manager.order.OrderStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class OrderSelectionBox extends VBox {
    private final ListView<OrderHeader> orderList = new ListView<>();
    private final HashMap<Order, OrderHeader> headers = new HashMap<>();
    public final ObservableList<OrderHeader> orderHeaders = FXCollections.observableArrayList();
    private final OrderGUI gui;
    private final TextField searchById = new TextField();
    ObservableList<String> list = FXCollections.observableArrayList();
    private final ComboBox<String> comboBox = new ComboBox<>(list);

    public OrderSelectionBox(OrderGUI gui) {
        this.gui = gui;

        setupIdSearch();
        setupCombox();
        setupOrderSelectionList();
    }

    private void setupCombox() {
        list.add("ALL");
        for(OrderStatus s: OrderStatus.values()) {
            list.add(s.name());
        }
        this.getChildren().add(comboBox);
        this.comboBox.setValue("ALL");

        comboBox.setOnAction(a -> {
//            orderHeaders.clear();
//            if(comboBox.getValue().equals("ALL")) {
//                orderHeaders.addAll(headers.values());
//            } else {
//                var status = OrderStatus.valueOf(comboBox.getValue());
//                for(OrderHeader o: headers.values()) {
//                    if(o.order.status == status) {
//                        orderHeaders.add(o);
//                    }
//                }
//            }
            updateList();
        });
    }

    private void setupIdSearch() {
        this.getChildren().add(searchById);
        searchById.textProperty().addListener((observable, oldValue, newValue) -> {
//                this.comboBox.setValue("ALL");
//                var list = this.headers.values().stream()
//                        .filter( it->it.order.id.toLowerCase().contains(newValue))
//                        .collect(Collectors.toList());
//                this.orderHeaders.clear();
//                orderHeaders.addAll(list);
                updateList();
            }
        );
    }

    private void updateList() {
        var list = this.headers.values().stream().filter(it -> {
            boolean a;
            boolean b;
            if(!this.searchById.getText().isBlank()) {
                var text = searchById.getText();
                a = it.order.id.contains(text.toLowerCase());
            } else a = true;

            if(this.comboBox.getValue().equals("ALL")) {
                b = true;
            } else {
                b = it.order.status == OrderStatus.valueOf(this.comboBox.getValue());
            }

            return a && b;

        }).sorted(Comparator.comparingLong(OrderHeader::getTimestamp))
                .collect(Collectors.toList());

        var currentSelection = this.orderList.getSelectionModel().getSelectedItem();
        var index = list.contains(currentSelection) ? list.indexOf(currentSelection) : 0;

        this.orderHeaders.clear();
        this.orderHeaders.addAll(list);
        this.orderList.getSelectionModel().select(index);

    }

    public int getOrderNumber () {
        return orderHeaders.size();
    }

    public OrderHeader getOrderHeader(int index) {
        return this.orderHeaders.get(0);
    }

    private void setupOrderSelectionList() {
        this.getChildren().add(orderList);
        orderList.setEditable(false);
        orderList.setItems(orderHeaders);
        orderList.setOnMouseClicked(e -> this.gui.update(orderList.getSelectionModel().getSelectedItem().order));
    }

    public void updateOrders(Collection<Order> activeOrders, OrderGUI gui) {
        for (Order o : activeOrders) {
            if (!headers.containsKey(o))
                headers.put(o, new OrderHeader(o, gui));
        }
//        orderHeaders.clear();
//        orderHeaders.addAll(headers.values());
        updateList();
    }

    public void checkOrderHeader(Order order) {
        this.headers.get(order).setTheCheckBox();
    }

    public void updateOrder(Order order) {
        this.headers.get(order).refresh();
    }
}
