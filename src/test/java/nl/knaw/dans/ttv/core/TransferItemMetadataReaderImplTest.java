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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TransferItemMetadataReaderImplTest {

    @Test
    void getFilenameAttributes() {
        var service = new TransferItemMetadataReaderImpl(new ObjectMapper());
        try {
            var path = Path.of("doi-10-5072-dar-kxteqtv1.0.zip");
            var attributes = service.getFilenameAttributes(path);
            assertEquals(0, attributes.getVersionMinor());
            assertEquals(1, attributes.getVersionMajor());
            assertEquals(path.toString(), attributes.getDveFilePath());
            assertEquals("10.5072/DAR/KXTEQT", attributes.getDatasetPid());
        }
        catch (InvalidTransferItemException e) {
            fail(e);
        }
    }

    @Test
    void getFilesystemAttributes() {
    }

    @Test
    void getFileContentAttributes() {
    }
}
