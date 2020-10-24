package de.flapdoodle.embed.process.config;

import de.flapdoodle.embed.process.config.store.DownloadConfig;
import de.flapdoodle.embed.process.config.store.PackageResolver;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.Directory;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;
import de.flapdoodle.embed.process.store.ArtifactStore;
import de.flapdoodle.embed.process.store.Downloader;

public abstract class Defaults {
	
	public static Directory tempDirFactory() {
		return PropertyOrPlatformTempDir.defaultInstance();
	}
	
	public static UUIDTempNaming executableNaming() {
		return new UUIDTempNaming();
	}
	
	public static ArtifactStore artifactStore(DownloadConfig downloadConfig) {
		return ArtifactStore.builder()
				.downloadConfig(downloadConfig)
				.downloader(Downloader.platformDefault())
				.tempDirFactory(tempDirFactory())
				.executableNaming(executableNaming())
				.build();
	}

	public static DownloadConfig genericDownloadConfig(String name, String downloadPath, PackageResolver packageResolver) {
		String prefix = "."+name;
		return DownloadConfig.builder()
				.downloadPath((__) -> downloadPath)
				.downloadPrefix(prefix)
				.packageResolver(packageResolver)
				.artifactStorePath(new UserHome(prefix))
				.fileNaming(executableNaming())
				.progressListener(progressListener())
				.userAgent("Mozilla/5.0 (compatible; embedded "+name+"; +https://github.com/flapdoodle-oss/de.flapdoodle.embed.process)")
				.build();
	}

	public static StandardConsoleProgressListener progressListener() {
		return new StandardConsoleProgressListener();
	}
}
