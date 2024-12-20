module DynaCRUD {
	requires javafx.controls;
	requires javafx.base;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires transitive java.sql;
	
	opens application.Controller to javafx.fxml;
	exports application;

	exports application.Controller;
	exports application.Util;
}
