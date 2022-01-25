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
import nl.knaw.dans.ttv.config.CollectConfiguration;
import nl.knaw.dans.ttv.db.TransferItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class CollectTaskManager implements Managed {
    private static final Logger log = LoggerFactory.getLogger(CollectTaskManager.class);
    private final List<CollectConfiguration.InboxEntry> inboxes;
    private final Path outbox;
    private final ExecutorService executorService;
    private final TransferItemService transferItemService;
    private final TransferItemMetadataReader metadataReader;
    private List<InboxWatcher> inboxWatchers;

    public CollectTaskManager(List<CollectConfiguration.InboxEntry> inboxes, String outbox, ExecutorService executorService,
        TransferItemService transferItemService, TransferItemMetadataReader metadataReader) {
        // TODO add Objects.requiresNonNull etc
        this.inboxes = inboxes;
        this.outbox = Path.of(outbox);
        this.executorService = executorService;
        this.transferItemService = transferItemService;
        this.metadataReader = metadataReader;
    }

    @Override
    public void start() throws Exception {
        // scan inboxes
        log.info("scanning all inboxes for initial state");

        this.inboxWatchers = inboxes.stream().map(entry -> {
            try {
                return new InboxWatcher(Path.of(entry.getPath()), entry.getName(), this::onFileAdded, 500);
            }
            catch (Exception e) {
                log.error("unable to create InboxWatcher", e);
            }
            return null;
        }).collect(Collectors.toList());

        for (var inboxWatcher : this.inboxWatchers) {
            if (inboxWatcher != null) {
                inboxWatcher.start();
            }
        }
    }

    private void onFileAdded(File file, String datastationName) {
        if (file.isFile() && file.getName().endsWith(".zip")) {
            var transferTask = new CollectTask(
                file.toPath(), outbox, datastationName, transferItemService, metadataReader
            );

            executorService.execute(transferTask);
        }
    }

    @Override
    public void stop() throws Exception {
        for (var inboxWatcher : this.inboxWatchers) {
            if (inboxWatcher != null) {
                inboxWatcher.stop();
            }
        }
    }
}
