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

import java.time.LocalDateTime;

public class TransferItemDTO {

    private long id;
    private String datasetPid;
    private String datasetVersion;
    private int versionMajor;
    private int versionMinor;
    private LocalDateTime creationTime;
    private String dveFilePath;
    private String bagId;
    private String nbn;
    private String otherId;
    private String otherIdVersion;
    private String swordToken;
    private String datasetDvInstance;
    private String bagChecksum;
    private LocalDateTime queueDate;
    private long bagSize;
    private TransferItem.TransferStatus transferStatus;
    private byte[] oaiOre;
    private byte[] pidMapping;
    private String aipTarEntryName;
    private String aipsTar;
    private LocalDateTime bagDepositDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDatasetPid() {
        return datasetPid;
    }

    public void setDatasetPid(String datasetPid) {
        this.datasetPid = datasetPid;
    }

    public String getDatasetVersion() {
        return datasetVersion;
    }

    public void setDatasetVersion(String datasetVersion) {
        this.datasetVersion = datasetVersion;
    }

    public int getVersionMajor() {
        return versionMajor;
    }

    public void setVersionMajor(int versionMajor) {
        this.versionMajor = versionMajor;
    }

    public int getVersionMinor() {
        return versionMinor;
    }

    public void setVersionMinor(int versionMinor) {
        this.versionMinor = versionMinor;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getDveFilePath() {
        return dveFilePath;
    }

    public void setDveFilePath(String dveFilePath) {
        this.dveFilePath = dveFilePath;
    }

    public String getBagId() {
        return bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
    }

    public String getNbn() {
        return nbn;
    }

    public void setNbn(String nbn) {
        this.nbn = nbn;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public String getOtherIdVersion() {
        return otherIdVersion;
    }

    public void setOtherIdVersion(String otherIdVersion) {
        this.otherIdVersion = otherIdVersion;
    }

    public String getSwordToken() {
        return swordToken;
    }

    public void setSwordToken(String swordToken) {
        this.swordToken = swordToken;
    }

    public String getDatasetDvInstance() {
        return datasetDvInstance;
    }

    public void setDatasetDvInstance(String datasetDvInstance) {
        this.datasetDvInstance = datasetDvInstance;
    }

    public String getBagChecksum() {
        return bagChecksum;
    }

    public void setBagChecksum(String bagChecksum) {
        this.bagChecksum = bagChecksum;
    }

    public LocalDateTime getQueueDate() {
        return queueDate;
    }

    public void setQueueDate(LocalDateTime queueDate) {
        this.queueDate = queueDate;
    }

    public long getBagSize() {
        return bagSize;
    }

    public void setBagSize(long bagSize) {
        this.bagSize = bagSize;
    }

    public TransferItem.TransferStatus getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(TransferItem.TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

    public byte[] getOaiOre() {
        return oaiOre;
    }

    public void setOaiOre(byte[] oaiOre) {
        this.oaiOre = oaiOre;
    }

    public byte[] getPidMapping() {
        return pidMapping;
    }

    public void setPidMapping(byte[] pidMapping) {
        this.pidMapping = pidMapping;
    }

    public String getAipTarEntryName() {
        return aipTarEntryName;
    }

    public void setAipTarEntryName(String aipTarEntryName) {
        this.aipTarEntryName = aipTarEntryName;
    }

    public String getAipsTar() {
        return aipsTar;
    }

    public void setAipsTar(String aipsTar) {
        this.aipsTar = aipsTar;
    }

    public LocalDateTime getBagDepositDate() {
        return bagDepositDate;
    }

    public void setBagDepositDate(LocalDateTime bagDepositDate) {
        this.bagDepositDate = bagDepositDate;
    }

}
