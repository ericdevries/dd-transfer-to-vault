/*
 * Copyright (C) 2021 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.ttv.core.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileStore;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipFile;

public interface FileService {

    Path moveFile(Path current, Path newPath) throws IOException;

    boolean deleteFile(Path path) throws IOException;

    void deleteDirectory(Path path) throws IOException;

    Object getFilesystemAttribute(Path path, String property) throws IOException;

    String calculateChecksum(Path path) throws IOException;

    long getFileSize(Path path) throws IOException;

    long getPathSize(Path path) throws IOException;

    ZipFile openZipFile(Path path) throws IOException;

    InputStream openFileFromZip(ZipFile datasetVersionExport, Path of) throws IOException;

    Path moveFileAtomically(Path filePath, Path newPath) throws IOException;

    void ensureDirectoryExists(Path path) throws IOException;

    void rejectFile(Path path, Exception exception) throws IOException;

    boolean exists(Path path);

    boolean canRead(Path path, int timeout) throws TimeoutException;

    boolean canRead(Path path);

    boolean canWrite(Path path);

    FileStore getFileStore(Path path) throws IOException;
}
