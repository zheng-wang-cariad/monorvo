module film.monovo {
    requires javafx.controls;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.base;
	requires com.google.gson;
	requires javax.mail;
	requires javafx.web;
	requires org.apache.commons.io;
	requires spring.core;
	requires javafx.swing;
    requires jsoup;
    requires barcode4j;
    exports film.monorvo;
    exports film.monorvo.config;
	exports film.monorvo.manager.event;
	exports film.monorvo.manager.order;
}