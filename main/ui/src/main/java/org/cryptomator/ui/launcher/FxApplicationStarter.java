package org.cryptomator.ui.launcher;

import javafx.application.Platform;
import org.cryptomator.ui.fxapp.FxApplication;
import org.cryptomator.ui.fxapp.FxApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

@Singleton
public class FxApplicationStarter {

	private static final Logger LOG = LoggerFactory.getLogger(FxApplicationStarter.class);

	private final FxApplicationComponent.Builder fxAppComponent;
	private final ExecutorService executor;
	private final CompletableFuture<FxApplication> future;

	@Inject
	public FxApplicationStarter(FxApplicationComponent.Builder fxAppComponent, ExecutorService executor) {
		this.fxAppComponent = fxAppComponent;
		this.executor = executor;
		this.future = new CompletableFuture<>();
	}

	public synchronized CompletionStage<FxApplication> get(boolean hasTrayIcon) {
		if (!future.isDone()) {
			start(hasTrayIcon);
		}
		return future;
	}

	private void start(boolean hasTrayIcon) {
		executor.submit(() -> {
			LOG.debug("Starting JavaFX runtime...");
			Platform.startup(() -> {
				assert Platform.isFxApplicationThread();
				LOG.info("JavaFX Runtime started.");
				FxApplication app = fxAppComponent.trayMenuSupported(hasTrayIcon).build().application();
				app.start();
				future.complete(app);
			});
		});
	}

}
