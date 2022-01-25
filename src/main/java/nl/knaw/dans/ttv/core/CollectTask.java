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
package nl.knaw.dans.ttv.core;

import io.dropwizard.hibernate.UnitOfWork;
import nl.knaw.dans.ttv.db.TransferItem;
import nl.knaw.dans.ttv.db.TransferItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(CollectTask.class);
    private static final String DOI_PATTERN = "(?<doi>doi-10-[0-9]{4,}-[A-Za-z0-9]{2,}-[A-Za-z0-9]{6})-?";
    private static final String SCHEMA_PATTERN = "(?<schema>datacite)?.?";
    private static final String DATASET_VERSION_PATTERN = "v(?<major>[0-9]+).(?<minor>[0-9]+)";
    private static final String EXTENSION_PATTERN = "(?<extension>.zip|.xml)";
    private static final Pattern PATTERN = Pattern.compile(DOI_PATTERN + SCHEMA_PATTERN + DATASET_VERSION_PATTERN + EXTENSION_PATTERN);
    private final Path filePath;
    private final Path outbox;
    private final String datastationName;
    private final TransferItemService transferItemService;
    private final TransferItemMetadataReader metadataReader;

    public CollectTask(Path filePath, Path outbox, String datastationName, TransferItemService transferItemService,
        TransferItemMetadataReader metadataReader) {
        this.filePath = filePath;
        this.outbox = outbox;
        this.datastationName = datastationName;
        this.transferItemService = transferItemService;
        this.metadataReader = metadataReader;
    }

    @Override
    public void run() {
        try {
            var transferItem = createTransferItem(this.filePath);
            //            extractMetadata(transferItem);
            cleanUpXmlFile(this.filePath);
            moveFileToOutbox(transferItem, this.filePath, this.outbox);
        }
        catch (IOException | InvalidTransferItemException e) {
            log.error("unable to create TransferItem for path {}", this.filePath, e);
        }
    }

    private TransferItem createTransferItem(Path path) throws InvalidTransferItemException {
        var filenameAttributes = metadataReader.getFilenameAttributes(path);
        var filesystemAttributes = metadataReader.getFilesystemAttributes(path);
        var fileContentAttributes = metadataReader.getFileContentAttributes(path);

        var transferItem = transferItemService.createTransferItem(datastationName,
            filenameAttributes, filesystemAttributes, fileContentAttributes);

        return transferItem;

    }
    //
    //    @UnitOfWork
    //    public void extractMetadata(TransferItem transferItem) throws IOException {
    //        ZipFile datasetVersionExport = new ZipFile(this.filePath.toFile());
    //        String metadataFilePath = Objects.requireNonNull(datasetVersionExport.stream().filter(zipEntry -> zipEntry.getName().endsWith(".jsonld")).findAny().orElse(null),
    //            "metadataFilePath can't be null" + transferItem.onError()).getName();
    //        String pidMappingFilePath = Objects.requireNonNull(datasetVersionExport.stream().filter(zipEntry -> zipEntry.getName().contains("pid-mapping.txt")).findAny().orElse(null),
    //            "pidMappingFilePath can't be null" + transferItem.onError()).getName();
    //        byte[] pidMapping = Objects.requireNonNull(IOUtils.toByteArray(datasetVersionExport.getInputStream(datasetVersionExport.getEntry(pidMappingFilePath))),
    //            "pidMapping can't be null" + transferItem.onError());
    //        byte[] oaiOre = Objects.requireNonNull(IOUtils.toByteArray(datasetVersionExport.getInputStream(datasetVersionExport.getEntry(metadataFilePath))),
    //            "oaiOre can't be null" + transferItem.onError());
    //
    //        JsonNode jsonNode = Objects.requireNonNull(objectMapper.readTree(oaiOre), "jsonld metadata can't be null" + transferItem.onError());
    //
    //        String nbn = Objects.requireNonNull(jsonNode.get("ore:describes").get("dansDataVaultMetadata:NBN").asText(), "NBN can't be null" + transferItem.onError());
    //        String dvPidVersion = Objects.requireNonNull(jsonNode.get("ore:describes").get("dansDataVaultMetadata:DV PID Version").asText(), "DV PID Version can't be null" + transferItem.onError());
    //        String bagId = Objects.requireNonNull(jsonNode.get("ore:describes").get("dansDataVaultMetadata:Bag ID").asText(), "Bag ID can't be null" + transferItem.onError());
    //        //TODO Other ID, Other ID Version and SWORD token missing
    //        transferItem.setPidMapping(pidMapping);
    //        transferItem.setOaiOre(oaiOre);
    //        transferItem.setNbn(nbn);
    //        transferItem.setDatasetVersion(dvPidVersion);
    //        transferItem.setBagId(bagId);
    //        transferItem.setQueueDate(LocalDateTime.now());
    //        transferItem.setTransferStatus(TransferItem.TransferStatus.MOVE);
    //        transferItemService.save(transferItem);
    //    }

    public void cleanUpXmlFile(Path path) throws IOException {
        Matcher matcher = PATTERN.matcher(path.getFileName().toString());
        String xml = matcher.matches() ? matcher.group("doi") + "-datacite.v" + matcher.group("major") + "." + matcher.group("minor") + ".xml" : null;

        if (xml != null) {
            Files.deleteIfExists(path.getParent().resolve(xml));
        }
    }

    @UnitOfWork
    public void moveFileToOutbox(TransferItem transferItem, Path filePath, Path outboxPath) throws IOException {
        log.trace("filePath is {}, outboxPath is {}", filePath, outboxPath);
        var newPath = outboxPath.resolve(filePath.getFileName());
        log.info("moving file {} to location {}", filePath, newPath);
        // TODO move to some service for testability reasons
        Files.move(filePath, newPath);
        transferItemService.moveTransferItem(transferItem, newPath);
    }
}
