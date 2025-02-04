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

import io.dropwizard.lifecycle.Managed;
import nl.knaw.dans.ttv.core.service.FileService;
import nl.knaw.dans.ttv.core.service.InboxWatcher;
import nl.knaw.dans.ttv.core.service.InboxWatcherFactory;
import nl.knaw.dans.ttv.core.service.TransferItemMetadataReader;
import nl.knaw.dans.ttv.core.service.TransferItemService;
import nl.knaw.dans.ttv.core.service.TransferItemValidator;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ExtractMetadataTaskManager implements Managed {
    private static final Logger log = LoggerFactory.getLogger(ExtractMetadataTaskManager.class);
    private final Path inbox;
    private final Path outbox;
    private final long pollingInterval;
    private final ExecutorService executorService;
    private final TransferItemService transferItemService;
    private final TransferItemMetadataReader metadataReader;
    private final FileService fileService;
    private final InboxWatcherFactory inboxWatcherFactory;
    private final TransferItemValidator transferItemValidator;
    private final VaultCatalogRepository vaultCatalogRepository;
    private InboxWatcher inboxWatcher;

    public ExtractMetadataTaskManager(Path inbox, Path outbox, long pollingInterval, ExecutorService executorService,
        TransferItemService transferItemService, TransferItemMetadataReader metadataReader, FileService fileService, InboxWatcherFactory inboxWatcherFactory,
        TransferItemValidator transferItemValidator, VaultCatalogRepository vaultCatalogRepository) {

        this.inbox = Objects.requireNonNull(inbox);
        this.outbox = Objects.requireNonNull(outbox);
        this.pollingInterval = pollingInterval;
        this.executorService = Objects.requireNonNull(executorService);
        this.transferItemService = Objects.requireNonNull(transferItemService);
        this.metadataReader = Objects.requireNonNull(metadataReader);
        this.fileService = Objects.requireNonNull(fileService);
        this.inboxWatcherFactory = Objects.requireNonNull(inboxWatcherFactory);
        this.transferItemValidator = transferItemValidator;
        this.vaultCatalogRepository = vaultCatalogRepository;
    }

    @Override
    public void start() throws Exception {
        // scan inboxes
        log.info("creating InboxWatcher's for configured inboxes");

        this.inboxWatcher = inboxWatcherFactory.getInboxWatcher(inbox, null, this::onFileAdded, pollingInterval);
        this.inboxWatcher.start();
    }

    public void onFileAdded(File file, String datastationName) {
        log.debug("Received file creation event for file '{}'", file);
        if (FilenameUtils.getExtension(file.getName()).toLowerCase(Locale.ROOT).equals("zip")) {
            var metadataTask = new ExtractMetadataTask(
                file.toPath(), outbox, transferItemService, metadataReader, fileService,
                transferItemValidator, vaultCatalogRepository);

            log.debug("Executing task {}", metadataTask);
            executorService.execute(metadataTask);
        }
    }

    @Override
    public void stop() throws Exception {
        log.debug("Shutting down MetadataTaskManager");

        this.inboxWatcher.stop();
        this.executorService.shutdownNow();
    }
}
