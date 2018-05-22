package de.flapdoodle.embed.process.parts;

import static de.flapdoodle.transition.NamedType.typeOf;

import java.nio.file.Path;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Version;
import de.flapdoodle.transition.initlike.InitLike;
import de.flapdoodle.transition.initlike.InitRoutes;
import de.flapdoodle.transition.initlike.State;
import de.flapdoodle.transition.routes.RoutesAsGraph;
import de.flapdoodle.transition.routes.SingleDestination;

@Immutable
public abstract class ProcessFactory {
	public abstract Version version();

	public abstract String baseDownloadUrl();

	public abstract Path artifactsBasePath();

	public abstract ArchiveTypeOfDistribution archiveTypeForDistribution();

	public abstract FileSetOfDistribution fileSetOfDistribution();

	public abstract UrlOfDistributionAndArchiveType urlOfDistributionAndArchiveType();

	public abstract LocalArtifactPathOfDistributionAndArchiveType localArtifactPathOfDistributionAndArchiveType();

	public abstract ArtifactPathForUrl artifactPathForUrl();

	@Auxiliary
	protected InitRoutes<SingleDestination<?>> routes() {
		return InitRoutes.fluentBuilder()
				.start(Version.class).withValue(version())
				.start(BaseUrl.class).withValue(BaseUrl.of(baseDownloadUrl()))
				.start(ArtifactsBasePath.class).withValue(ArtifactsBasePath.of(artifactsBasePath()))
				.bridge(Version.class, Distribution.class).withMapping(Distribution::detectFor)
				.bridge(Distribution.class, ArchiveType.class).withMapping(archiveTypeForDistribution())
				.bridge(Distribution.class, FileSet.class).withMapping(fileSetOfDistribution())
				.merge3(typeOf(BaseUrl.class), typeOf(Distribution.class), typeOf(ArchiveType.class), typeOf(ArtifactUrl.class))
				.with((baseUrl, distribution, archiveType) -> State
						.of(urlOfDistributionAndArchiveType().apply(baseUrl, distribution, archiveType)))
				.merge(typeOf(Distribution.class), typeOf(ArchiveType.class), typeOf(LocalArtifactPath.class))
				.withMapping(localArtifactPathOfDistributionAndArchiveType())
				.merge3(typeOf(ArtifactsBasePath.class), typeOf(ArtifactUrl.class), typeOf(LocalArtifactPath.class),
						typeOf(ArtifactPath.class))
				.with((base, url, localPath) -> State.of(artifactPathForUrl().apply(base, url, localPath)))
				.build();
	}

	@Auxiliary
	public String setupAsDot(String appName) {
		return RoutesAsGraph.routeGraphAsDot(appName, RoutesAsGraph.asGraphIncludingStartAndEnd(routes().all()));
	}

	@Auxiliary
	public InitLike initLike() {
		return InitLike.with(routes());
	}

	public static ImmutableProcessFactory.Builder builder() {
		return ImmutableProcessFactory.builder();
	}
}
