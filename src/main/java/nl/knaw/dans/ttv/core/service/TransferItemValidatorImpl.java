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

import nl.knaw.dans.ttv.core.InvalidTransferItemException;
import nl.knaw.dans.ttv.db.TransferItem;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class TransferItemValidatorImpl implements TransferItemValidator {
    private static final Pattern VERSION_PATTERN = Pattern.compile("^([0-9]+)\\.([0-9]+)$");

    @Override
    public void validateTransferItem(TransferItem transferItem) throws InvalidTransferItemException {

        // check if datasetVersion is a valid version (eg 1.0)
        if (!isValidVersion(transferItem.getDataversePidVersion())) {
            throw new InvalidTransferItemException(String.format("Dataset Version is invalid: '%s'", transferItem.getDataversePidVersion()));
        }

        if (!isValidBagId(transferItem.getBagId())) {
            throw new InvalidTransferItemException(String.format("Bag ID is invalid: %s", transferItem.getBagId()));
        }

        if (!isValidNbn(transferItem.getNbn())) {
            throw new InvalidTransferItemException(String.format("NBN is invalid: %s", transferItem.getNbn()));
        }
    }

    boolean isValidVersion(String version) {
        // the dataverse will have a version, but the vaas bags won't
        if (version == null) {
            return true;
        }

        var matches = VERSION_PATTERN.matcher(version);

        if (!matches.matches()) {
            return false;
        }

        var versionMajor = Optional.ofNullable(matches.group(1)).map(Integer::parseInt).orElse(0);
        var versionMinor = Optional.ofNullable(matches.group(2)).map(Integer::parseInt).orElse(0);

        return versionMajor >= 1 && versionMinor >= 0;
    }

    boolean isValidVersionMajorAndMinor(int versionMajor, int versionMinor) {
        return versionMajor >= 1 && versionMinor >= 0;
    }

    boolean isValidBagId(String bagId) {
        var prefix = "urn:uuid:";

        if (bagId == null) {
            return false;
        }

        if (!bagId.startsWith(prefix)) {
            return false;
        }

        try {
            UUID.fromString(bagId.substring(prefix.length()));
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    boolean isValidNbn(String nbn) {
        var prefix = "urn:nbn:";
        return nbn != null && nbn.startsWith(prefix) && nbn.length() > prefix.length();
    }
}
