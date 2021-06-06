package zems.core.contentbus.persistence;

import zems.core.concept.Content;
import zems.core.concept.PersistenceProvider;
import zems.core.concept.Properties;
import zems.core.utils.ZemsIoUtils;
import zems.core.utils.ZemsJsonUtils;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class FilePersistenceProvider implements PersistenceProvider<FilePersistenceProvider> {

  private static final String PROPERTIES_FILENAME = ".properties.json";
  private final Path containerDirectory;
  private final ZemsJsonUtils jsonUtils = new ZemsJsonUtils().withOwnObjectMapper();

  public FilePersistenceProvider(Path containerDirectory) {
    Objects.requireNonNull(containerDirectory);

    this.containerDirectory = containerDirectory;
  }

  @Override
  public Optional<Content> read(String path) {
    Objects.requireNonNull(path);
    ZemsIoUtils.ensureDirExistsAndIsReadable(containerDirectory);

    Path contentPath = containerDirectory.resolve("." + path);
    Path propertyPath = contentPath.resolve(PROPERTIES_FILENAME);
    if (Files.isRegularFile(propertyPath)) {
      return Optional.of(new Content(path, jsonUtils.fromPath(propertyPath)));
    }

    return Optional.empty();
  }

  @Override
  public Optional<ByteChannel> readBinary(String binaryId) {
    return Optional.empty();
  }

  @Override
  public FilePersistenceProvider write(Content content) {
    Objects.requireNonNull(content);
    ZemsIoUtils.ensureDirExistsAndIsWritable(containerDirectory);

    try {
      Path propertyPath = containerDirectory.resolve("." + content.path()).resolve(PROPERTIES_FILENAME);
      ZemsIoUtils.createParentDirectories(propertyPath);
      applyProperties(propertyPath, content.properties());
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
    return this;
  }

  private void applyProperties(Path path, Properties properties) throws IOException {
    ZemsIoUtils.write(path, jsonUtils.asJsonString(properties));
  }

}
