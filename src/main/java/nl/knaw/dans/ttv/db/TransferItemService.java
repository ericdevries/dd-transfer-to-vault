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
package nl.knaw.dans.ttv.db;

import nl.knaw.dans.ttv.core.FileContentAttributes;
import nl.knaw.dans.ttv.core.FilenameAttributes;
import nl.knaw.dans.ttv.core.FilesystemAttributes;

import java.nio.file.Path;
import java.util.List;

public interface TransferItemService {

    List<TransferItem> findByDvePath(List<String> paths);

    TransferItem createTransferItem(String datastationName, FilenameAttributes filenameAttributes, FilesystemAttributes filesystemAttributes, FileContentAttributes fileContentAttributes);

    TransferItem moveTransferItem(TransferItem transferItem, Path newPath);
}
