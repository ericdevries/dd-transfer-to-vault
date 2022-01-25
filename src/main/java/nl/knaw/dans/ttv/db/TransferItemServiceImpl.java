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

import io.dropwizard.hibernate.UnitOfWork;
import nl.knaw.dans.ttv.core.FileContentAttributes;
import nl.knaw.dans.ttv.core.FilenameAttributes;
import nl.knaw.dans.ttv.core.FilesystemAttributes;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class TransferItemServiceImpl implements TransferItemService {
    private final TransferItemDAO transferItemDAO;

    public TransferItemServiceImpl(TransferItemDAO transferItemDAO) {
        this.transferItemDAO = transferItemDAO;
    }

    @Override
    public List<TransferItem> findByDvePath(List<String> paths) {
        return null;
    }

    @Override
    @UnitOfWork
    public TransferItem createTransferItem(String datastationName, FilenameAttributes filenameAttributes, FilesystemAttributes filesystemAttributes, FileContentAttributes fileContentAttributes) {
        var transferItem = new TransferItem();
        transferItem.setTransferStatus(TransferItem.TransferStatus.EXTRACT);
        transferItem.setQueueDate(LocalDateTime.now());
        transferItem.setDatasetDvInstance(datastationName);

        // filename attributes
        transferItem.setDveFilePath(filenameAttributes.getDveFilePath());
        transferItem.setDatasetPid(filenameAttributes.getDatasetPid());
        transferItem.setVersionMajor(filenameAttributes.getVersionMajor());
        transferItem.setVersionMinor(filenameAttributes.getVersionMinor());

        // filesystem attributes
        transferItem.setCreationTime(filesystemAttributes.getCreationTime());
        transferItem.setBagChecksum(filesystemAttributes.getBagChecksum());
        transferItem.setBagSize(filesystemAttributes.getBagSize());

        // file content attributes
        transferItem.setDatasetVersion(fileContentAttributes.getDatasetVersion());
        transferItem.setBagId(fileContentAttributes.getBagId());
        transferItem.setNbn(fileContentAttributes.getNbn());
        transferItem.setOaiOre(fileContentAttributes.getOaiOre());
        transferItem.setPidMapping(fileContentAttributes.getPidMapping());

        System.out.println("ITEM: " + transferItem);
        transferItemDAO.save(transferItem);

        return transferItem;
    }

    @Override
    @UnitOfWork
    public TransferItem moveTransferItem(TransferItem transferItem, Path newPath) {
        transferItem.setDveFilePath(newPath.toString());
        transferItem.setTransferStatus(TransferItem.TransferStatus.COLLECTED);
        transferItemDAO.merge(transferItem);
        return transferItem;
    }

}
