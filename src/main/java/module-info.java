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
    exports film.monovo;
    exports film.monovo.config;
	exports film.monovo.manager.event;
	exports film.monovo.manager.order;
}