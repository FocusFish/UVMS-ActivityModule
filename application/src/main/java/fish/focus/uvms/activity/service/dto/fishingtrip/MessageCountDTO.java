/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package fish.focus.uvms.activity.service.dto.fishingtrip;

import javax.json.bind.annotation.JsonbProperty;

public class MessageCountDTO {

    @JsonbProperty("noOfReports")
    private int noOfReports;

    @JsonbProperty("noOfDeclarations")
    private int noOfDeclarations;

    @JsonbProperty("noOfNotifications")
    private int noOfNotifications;

    @JsonbProperty("noOfCorrections")
    private int noOfCorrections;

    @JsonbProperty("noOfFishingOperations")
    private int noOfFishingOperations;

    @JsonbProperty("noOfDeletions")
    private int noOfDeletions;

    @JsonbProperty("noOfCancellations")
    private int noOfCancellations;


    @JsonbProperty("noOfReports")
    public int getNoOfReports() {
        return noOfReports;
    }
    @JsonbProperty("noOfReports")
    public void setNoOfReports(int noOfReports) {
        this.noOfReports = noOfReports;
    }
    @JsonbProperty("noOfDeclarations")
    public int getNoOfDeclarations() {
        return noOfDeclarations;
    }
    @JsonbProperty("noOfDeclarations")
    public void setNoOfDeclarations(int noOfDeclarations) {
        this.noOfDeclarations = noOfDeclarations;
    }
    @JsonbProperty("noOfNotifications")
    public int getNoOfNotifications() {
        return noOfNotifications;
    }
    @JsonbProperty("noOfNotifications")
    public void setNoOfNotifications(int noOfNotifications) {
        this.noOfNotifications = noOfNotifications;
    }
    @JsonbProperty("noOfCorrections")
    public int getNoOfCorrections() {
        return noOfCorrections;
    }
    @JsonbProperty("noOfCorrections")
    public void setNoOfCorrections(int noOfCorrections) {
        this.noOfCorrections = noOfCorrections;
    }
    @JsonbProperty("noOfFishingOperations")
    public int getNoOfFishingOperations() {
        return noOfFishingOperations;
    }
    @JsonbProperty("noOfFishingOperations")
    public void setNoOfFishingOperations(int noOfFishingOperations) {
        this.noOfFishingOperations = noOfFishingOperations;
    }
    @JsonbProperty("noOfDeletions")
    public int getNoOfDeletions() {
        return noOfDeletions;
    }
    @JsonbProperty("noOfDeletions")
    public void setNoOfDeletions(int noOfDeletions) {
        this.noOfDeletions = noOfDeletions;
    }
    @JsonbProperty("noOfCancellations")
    public int getNoOfCancellations() {
        return noOfCancellations;
    }
    @JsonbProperty("noOfCancellations")
    public void setNoOfCancellations(int noOfCancellations) {
        this.noOfCancellations = noOfCancellations;
    }

}
