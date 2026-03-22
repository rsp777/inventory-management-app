package com.pawar.inventory.app.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

public class CsMeasureData {

	
    private Long id; // Primary key
    @JsonProperty("Status")
    private String status;

    @JsonProperty("Location")
    private String location;

    @JsonProperty("Package_Count")
    private String packageCount;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("Date_Time")
    private LocalDateTime dateTime;

    @JsonProperty("Length_Value")
    private String lengthValue;

    @JsonProperty("Length_Status")
    private String lengthStatus;

    @JsonProperty("Length_Units")
    private String lengthUnits;

    @JsonProperty("Width_Value")
    private String widthValue;

    @JsonProperty("Width_Status")
    private String widthStatus;

    @JsonProperty("Width_Units")
    private String widthUnits;

    @JsonProperty("Height_Value")
    private String heightValue;

    @JsonProperty("Height_Status")
    private String heightStatus;

    @JsonProperty("Height_Units")
    private String heightUnits;

    @JsonProperty("Weight_Value")
    private String weightValue;

    @JsonProperty("Weight_Status")
    private String weightStatus;

    @JsonProperty("Weight_Units")
    private String weightUnits;

    @JsonProperty("DimWeight_Value")
    private String dimWeightValue;

    @JsonProperty("DimWeight_Status")
    private String dimWeightStatus;

    @JsonProperty("DimWeight_Units")
    private String dimWeightUnits;

    @JsonProperty("Factor_Value")
    private String factorValue;

    @JsonProperty("Factor_Type")
    private String factorType;

    @JsonProperty("Barcode1")
    private String barcode1;

    @JsonProperty("Barcode2")
    private String barcode2;

    @JsonProperty("Measure_CRC")
    private String measureCRC;

	    // Getters and Setters
	    public String getStatus() {
	        return status;
	    }

	    public void setStatus(String status) {
	        this.status = status;
	    }

	    public String getLocation() {
	        return location;
	    }

	    public void setLocation(String location) {
	        this.location = location;
	    }

	    public String getPackageCount() {
	        return packageCount;
	    }

	    public void setPackageCount(String packageCount) {
	        this.packageCount = packageCount;
	    }

	    public LocalDateTime getDateTime() {
	        return dateTime;
	    }

	    public void setDateTime(LocalDateTime dateTime) {
	        this.dateTime = dateTime;
	    }

	    public String getLengthValue() {
	        return lengthValue;
	    }

	    public void setLengthValue(String lengthValue) {
	        this.lengthValue = lengthValue;
	    }

	    public String getLengthStatus() {
	        return lengthStatus;
	    }

	    public void setLengthStatus(String lengthStatus) {
	        this.lengthStatus = lengthStatus;
	    }

	    public String getLengthUnits() {
	        return lengthUnits;
	    }

	    public void setLengthUnits(String lengthUnits) {
	        this.lengthUnits = lengthUnits;
	    }

	    public String getWidthValue() {
	        return widthValue;
	    }

	    public void setWidthValue(String widthValue) {
	        this.widthValue = widthValue;
	    }

	    public String getWidthStatus() {
	        return widthStatus;
	    }

	    public void setWidthStatus(String widthStatus) {
	        this.widthStatus = widthStatus;
	    }

	    public String getWidthUnits() {
	        return widthUnits;
	    }

	    public void setWidthUnits(String widthUnits) {
	        this.widthUnits = widthUnits;
	    }

	    public String getHeightValue() {
	        return heightValue;
	    }

	    public void setHeightValue(String heightValue) {
	        this.heightValue = heightValue;
	    }

	    public String getHeightStatus() {
	        return heightStatus;
	    }

	    public void setHeightStatus(String heightStatus) {
	        this.heightStatus = heightStatus;
	    }

	    public String getHeightUnits() {
	        return heightUnits;
	    }

	    public void setHeightUnits(String heightUnits) {
	        this.heightUnits = heightUnits;
	    }

	    public String getWeightValue() {
	        return weightValue;
	    }

	    public void setWeightValue(String weightValue) {
	        this.weightValue = weightValue;
	    }

	    public String getWeightStatus() {
	        return weightStatus;
	    }

	    public void setWeightStatus(String weightStatus) {
	        this.weightStatus = weightStatus;
	    }

	    public String getWeightUnits() {
	        return weightUnits;
	    }

	    public void setWeightUnits(String weightUnits) {
	        this.weightUnits = weightUnits;
	    }

	    public String getDimWeightValue() {
	        return dimWeightValue;
	    }
	    public void setDimWeightValue(String dimWeightValue) {
	        this.dimWeightValue = dimWeightValue;
	    }

	    public String getDimWeightStatus() {
	        return dimWeightStatus;
	    }

	    public void setDimWeightStatus(String dimWeightStatus) {
	        this.dimWeightStatus = dimWeightStatus;
	    }

	    public String getDimWeightUnits() {
	        return dimWeightUnits;
	    }

	    public void setDimWeightUnits(String dimWeightUnits) {
	        this.dimWeightUnits = dimWeightUnits;
	    }

	    public String getFactorValue() {
	        return factorValue;
	    }

	    public void setFactorValue(String factorValue) {
	        this.factorValue = factorValue;
	    }

	    public String getFactorType() {
	        return factorType;
	    }

	    public void setFactorType(String factorType) {
	        this.factorType = factorType;
	    }

	    public String getBarcode1() {
	        return barcode1;
	    }

	    public void setBarcode1(String barcode1) {
	        this.barcode1 = barcode1;
	    }

	    public String getBarcode2() {
	        return barcode2;
	    }

	    public void setBarcode2(String barcode2) {
	        this.barcode2 = barcode2;
	    }

	    public String getMeasureCRC() {
	        return measureCRC;
	    }

	    public void setMeasureCRC(String measureCRC) {
	        this.measureCRC = measureCRC;
	    }

		@Override
		public String toString() {
			return "CsMeasureData [id=" + id + ", status=" + status + ", location=" + location + ", packageCount="
					+ packageCount + ", dateTime=" + dateTime + ", lengthValue=" + lengthValue + ", lengthStatus="
					+ lengthStatus + ", lengthUnits=" + lengthUnits + ", widthValue=" + widthValue + ", widthStatus="
					+ widthStatus + ", widthUnits=" + widthUnits + ", heightValue=" + heightValue + ", heightStatus="
					+ heightStatus + ", heightUnits=" + heightUnits + ", weightValue=" + weightValue + ", weightStatus="
					+ weightStatus + ", weightUnits=" + weightUnits + ", dimWeightValue=" + dimWeightValue
					+ ", dimWeightStatus=" + dimWeightStatus + ", dimWeightUnits=" + dimWeightUnits + ", factorValue="
					+ factorValue + ", factorType=" + factorType + ", barcode1=" + barcode1 + ", barcode2=" + barcode2
					+ ", measureCRC=" + measureCRC + "]";
		}
}
