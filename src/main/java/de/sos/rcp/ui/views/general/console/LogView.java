package de.sos.rcp.ui.views.general.console;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class LogView extends ListView<LogRecord> {
	public class Log {
		private static final int MAX_LOG_ENTRIES = 1_000_000;

		private final BlockingDeque<LogRecord> log = new LinkedBlockingDeque<>(MAX_LOG_ENTRIES);

		public void drainTo(Collection<? super LogRecord> collection) {
			log.drainTo(collection);
		}

		public void offer(LogRecord record) {
			log.offer(record);
		}
	}

	public class LogHandler extends Handler {
		public Log mLog = new Log();

		@Override
		public void publish(LogRecord record) {
			mLog.offer(record);
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() throws SecurityException {
		}

	}

	private static final int MAX_ENTRIES = 10_000;

	private final static PseudoClass debug = PseudoClass.getPseudoClass("debug");
	private final static PseudoClass info = PseudoClass.getPseudoClass("info");
	private final static PseudoClass warn = PseudoClass.getPseudoClass("warn");
	private final static PseudoClass error = PseudoClass.getPseudoClass("error");

	private final static SimpleDateFormat timestampFormatter = new SimpleDateFormat("HH:mm:ss.SSS");

	private final BooleanProperty showTimestamp = new SimpleBooleanProperty(false);
	private final ObjectProperty<Level> filterLevel = new SimpleObjectProperty<>(null);
	private final BooleanProperty tail = new SimpleBooleanProperty(false);
	private final BooleanProperty paused = new SimpleBooleanProperty(false);
	private final DoubleProperty refreshRate = new SimpleDoubleProperty(60);

	private final ObservableList<LogRecord> logItems = FXCollections.observableArrayList();

	private LogHandler mLogHandler;

	public BooleanProperty showTimeStampProperty() {
		return showTimestamp;
	}

	public ObjectProperty<Level> filterLevelProperty() {
		return filterLevel;
	}

	public BooleanProperty tailProperty() {
		return tail;
	}

	public BooleanProperty pausedProperty() {
		return paused;
	}

	public DoubleProperty refreshRateProperty() {
		return refreshRate;
	}

	public LogView() {
    	mLogHandler = new LogHandler();
    	Logger l = Logger.getGlobal();
		while(l.getParent() != null && l.getParent() != l){
			l = l.getParent();
		}
    	l.addHandler(mLogHandler);
        getStyleClass().add("log-view");

        Timeline logTransfer = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        event -> {
                            mLogHandler.mLog.drainTo(logItems);

                            if (logItems.size() > MAX_ENTRIES) {
                                logItems.remove(0, logItems.size() - MAX_ENTRIES);
                            }

                            if (tail.get()) {
                                scrollTo(logItems.size());
                            }
                        }
                )
        );
        logTransfer.setCycleCount(Animation.INDEFINITE);
        logTransfer.rateProperty().bind(refreshRateProperty());

        this.pausedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && logTransfer.getStatus() == Animation.Status.RUNNING) {
                logTransfer.pause();
            }

            if (!newValue && logTransfer.getStatus() == Animation.Status.PAUSED && getParent() != null) {
                logTransfer.play();
            }
        });

        this.parentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                logTransfer.pause();
            } else {
                if (!paused.get()) {
                    logTransfer.play();
                }
            }
        });

        filterLevel.addListener((observable, oldValue, newValue) -> {
            setItems(
                    new FilteredList<LogRecord>(
                            logItems,
                            logRecord ->
                                logRecord.getLevel().intValue() >=
                                filterLevel.get().intValue()
                    )
            );
        });
        filterLevel.set(Level.FINE);

        setCellFactory(param -> new ListCell<LogRecord>() {
            {
                showTimestamp.addListener(observable -> updateItem(this.getItem(), this.isEmpty()));
            }

            @Override
            protected void updateItem(LogRecord item, boolean empty) {
                super.updateItem(item, empty);
                
                pseudoClassStateChanged(debug, false);
                pseudoClassStateChanged(info, false);
                pseudoClassStateChanged(warn, false);
                pseudoClassStateChanged(error, false);

                if (item == null || empty) {
                    setText(null);
                    return;
                }

                String context =
                        (item.getMessage() == null)
                                ? ""
                                : item.getMessage() + " ";

                if (showTimestamp.get()) {
                	
                    String timestamp =
                            (item.getMillis() == 0)
                                    ? ""
                                    : timestampFormatter.format(item.getMillis()) + " ";
                    setText(timestamp + context + item.getMessage());
                } else {
                    setText(context + item.getMessage());
                }

                if (item.getLevel() == Level.FINE){
                	pseudoClassStateChanged(debug, true);
                }else if (item.getLevel() == Level.INFO){
                	pseudoClassStateChanged(info, true);
                }else if (item.getLevel() == Level.WARNING){
                	pseudoClassStateChanged(warn, true);
                }else if (item.getLevel() == Level.SEVERE){
                	pseudoClassStateChanged(error, true);
                }
            }
        });
    }
}
